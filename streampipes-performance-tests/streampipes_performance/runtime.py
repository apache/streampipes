#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

"""Timing, bounded readiness checks, and ownership-aware resource cleanup."""

import logging
import time
from dataclasses import dataclass, field

from requests import HTTPError

from streampipes_performance.config import Cleanup
from streampipes_performance.targets import STREAMPIPES

logger = logging.getLogger(__name__)


class EvaluationError(RuntimeError):
    """A framework-owned diagnostic that is safe to include in reports."""


def checked(value):
    """Reject explicit application-level failures even on successful HTTP responses."""
    if isinstance(value, dict):
        if value.get("success") is False or value.get("successful") is False:
            raise EvaluationError("api_operation_failed")
    return value


def error_record(error):
    # Server messages and request URLs can contain credentials or payloads.
    record = {"type": type(error).__name__}
    if isinstance(error, EvaluationError):
        record["reason"] = str(error)
    if isinstance(error, HTTPError) and error.response is not None:
        record["http_status"] = error.response.status_code
    return record


def missing(error):
    return isinstance(error, HTTPError) and error.response is not None and error.response.status_code == 404


def as_dict(value):
    return value.model_dump(by_alias=True) if hasattr(value, "model_dump") else value


@dataclass
class Resource:
    kind: str
    identifier: str
    name: str
    deleted: bool = False


@dataclass
class Context:
    client: object
    timing: object
    prefix: str
    clock: object = time
    phase: str = "setup"
    samples: list = field(default_factory=list)
    resources: list[Resource] = field(default_factory=list)
    datasets: list[str] = field(default_factory=list)
    cleanup_errors: list = field(default_factory=list)
    cleanup_config: Cleanup = field(default_factory=Cleanup)
    scenario_id: str = ""
    extra_apis: dict = field(default_factory=dict)
    runtime_settings: object = None
    target: object = STREAMPIPES

    def log(self, message, *args, level=logging.INFO):
        """Report progress with a scenario identifier, excluding request payloads."""
        logger.log(level, "[%s] " + message, self.scenario_id or self.prefix, *args)

    def call(self, operation, function, *args, **kwargs):
        start = self.clock.perf_counter()
        sample = dict(
            phase=self.phase,
            kind="api",
            operation=operation,
            success=False,
            started_at_ms=int(self.clock.time() * 1000),
        )
        if args and isinstance(args[0], str):
            sample["resource_id"] = args[0]
        try:
            value = checked(function(*args, **kwargs))
            sample["success"] = True
            return value
        except BaseException as error:
            sample["error"] = error_record(error)
            raise
        finally:
            sample["duration_ms"] = (self.clock.perf_counter() - start) * 1000
            self.samples.append(sample)
            resource = sample.get("resource_id")
            if resource is None and args:
                candidate = getattr(args[0], "name", None)
                resource = candidate if isinstance(candidate, str) else None
            detail = f" ({resource})" if resource else ""
            level = (
                logging.INFO
                if operation.rsplit(".", 1)[-1] in {"create", "start", "stop", "delete", "reset"}
                else logging.DEBUG
            )
            if not sample["success"]:
                level = logging.WARNING
            self.log(
                "%s%s %s in %.1f ms%s",
                operation,
                detail,
                "completed" if sample["success"] else "failed",
                sample["duration_ms"],
                f"; {sample['error']}" if "error" in sample else "",
                level=level,
            )

    def track(self, kind, identifier, name):
        if not any(r.kind == kind and r.identifier == identifier for r in self.resources):
            self.resources.append(Resource(kind, identifier, name))
        return identifier

    def api(self, kind):
        if kind in self.extra_apis:
            return self.extra_apis[kind]
        if kind == "stream":
            return self.client.streamApi
        return {
            "adapter": self.client.adapterApi,
            "pipeline": self.client.pipelineApi,
            "dataset": self.client.datasetApi,
        }[kind]

    def running_kinds(self):
        """Extra resource kinds with a lifecycle; they are stopped and deleted before pipelines."""
        return [kind for kind, api in self.extra_apis.items() if hasattr(api, "stop_result")]

    def static_kinds(self):
        return [kind for kind in self.extra_apis if kind not in self.running_kinds()]

    def details(self, kind, identifier):
        api = self.api(kind)
        method = api.get_details if kind == "pipeline" else api.get
        return as_dict(self.call(f"{kind}.get", method, identifier))

    def wait(self, operation, predicate):
        start = self.clock.perf_counter()
        success = False
        try:
            while True:
                try:
                    ready = predicate()
                except HTTPError as error:
                    if not missing(error):
                        raise
                    ready = False
                if ready:
                    success = True
                    return
                elapsed = self.clock.perf_counter() - start
                if elapsed >= self.timing.readiness_timeout_seconds:
                    raise TimeoutError(f"Readiness deadline exceeded for {operation}")
                self.clock.sleep(
                    min(self.timing.poll_seconds, self.timing.readiness_timeout_seconds - elapsed)
                )
        finally:
            self.samples.append(
                dict(
                    phase=self.phase,
                    kind="readiness",
                    operation=operation,
                    success=success,
                    duration_ms=(self.clock.perf_counter() - start) * 1000,
                )
            )

    def lifecycle(self, kind, identifier, running, force_stop=False):
        api = self.api(kind)
        action = "start" if running else "stop"
        start = self.clock.perf_counter()
        if running:
            self.call(f"{kind}.{action}", api.start_result, identifier)
        else:
            self.call(f"{kind}.{action}", api.stop_result, identifier, force_stop=force_stop)

        def ready():
            try:
                return self.details(kind, identifier).get("running") is running
            except HTTPError as error:
                if missing(error):
                    return False
                raise

        try:
            self.wait(f"{kind}.{action}", ready)
        finally:
            # Include the triggering request in successful and timed-out observations.
            self.samples[-1]["duration_ms"] = (self.clock.perf_counter() - start) * 1000
            self.samples[-1]["resource_id"] = identifier

    def inventory(self):
        stream_ids = {r.identifier for r in self.resources if r.kind == "stream"}
        # The registry also contains installed/unrelated streams. Only explicitly
        # tracked or prefix-owned streams belong to this benchmark's cleanup.
        streams = []
        if stream_ids:
            streams = [
                entry for entry in self.client.streamApi.all()
                if entry.get("elementId") in stream_ids
                or (entry.get("name") or "").startswith(self.prefix)
            ]
        return {
            **{kind: api.all() for kind, api in self.extra_apis.items()},
            "stream": streams,
            "adapter": [as_dict(v) for v in self.client.adapterApi.all()],
            "pipeline": [as_dict(v) for v in self.client.pipelineApi.all()],
            "dataset": self.client.datasetApi.all(),
        }

    def assert_empty(self):
        inventory = self.inventory()
        if any(inventory.values()):
            raise EvaluationError("instance_not_empty")

    def reconcile(self):
        """Discover prefix-owned resources after an uncertain creation response."""
        for kind, entries in self.inventory().items():
            for entry in entries:
                name = entry.get("name") or entry.get("measureName") or entry.get("assetName") or ""
                identifier = (
                    entry.get("elementId") or entry.get("pipelineId") or entry.get("_id") or entry.get("id")
                )
                owns = getattr(self.api(kind), "owns", None)
                if identifier and (name.startswith(self.prefix) or name in self.datasets
                                   or (owns is not None and owns(identifier, self.prefix))):
                    self.track(kind, identifier, name)
        for name in self.datasets:
            try:
                entry = self.client.datasetApi.get_by_name(name)
                self.track("dataset", entry["elementId"], name)
            except HTTPError as error:
                if not missing(error):
                    raise

    def cleanup(self):
        """Run the selected cleanup strategy and verify isolation."""
        self.log("Cleanup using %s", self.cleanup_config.strategy)
        if self.cleanup_config.strategy == "reset":
            return self.reset_cleanup()
        return self.delete_cleanup()

    def reset_cleanup(self):
        """Measure one instance reset; do not retry or fall back to deletion."""
        if not self.target.supports_reset:
            raise self.target.reset_unsupported()
        self.log("Resetting instance (timeout %.1f s)", self.cleanup_config.reset_timeout_seconds)
        try:
            self.call(
                "instance.reset",
                lambda: self.client.request_json(
                    "POST", "api/v2/reset", timeout=self.cleanup_config.reset_timeout_seconds
                ),
            )
        except Exception as error:
            self.cleanup_errors.append({"operation": "instance.reset", **error_record(error)})
            return False
        try:
            self.wait("cleanup.empty", lambda: not any(self.inventory().values()))
            for resource in self.resources:
                resource.deleted = True
        except Exception as error:
            self.cleanup_errors.append({"operation": "verify_empty", **error_record(error)})
        return not self.cleanup_errors

    def delete_cleanup(self):
        """Attempt every resource deletion, preserving errors and checking isolation."""

        def attempt(operation, function, *args, **kwargs):
            try:
                return self.call(operation, function, *args, **kwargs)
            except Exception as error:
                if not missing(error):
                    self.cleanup_errors.append({"operation": operation, **error_record(error)})
                return None

        try:
            self.reconcile()
        except Exception as error:
            self.cleanup_errors.append({"operation": "reconcile", **error_record(error)})
        running = self.running_kinds()
        for kind in ("adapter", *running, "pipeline"):
            for resource in self.resources:
                if resource.kind == kind:
                    attempt(f"{kind}.stop", self.api(kind).stop_result, resource.identifier, force_stop=True)
        for kind in (*running, "pipeline", "adapter", "stream", *reversed(self.static_kinds()), "dataset"):
            present_ids = None
            try:
                entries = self.inventory()[kind]
                present_ids = {
                    entry.get("elementId") or entry.get("pipelineId") or entry.get("_id") or entry.get("id")
                    for entry in entries
                }
            except Exception as error:
                self.cleanup_errors.append({"operation": "inventory", **error_record(error)})
            if kind == "dataset":
                # Datasets may have appeared while producers were being stopped.
                try:
                    self.reconcile()
                    present_ids = None
                except Exception as error:
                    self.cleanup_errors.append({"operation": "reconcile", **error_record(error)})
            for resource in self.resources:
                if resource.kind == kind and (present_ids is None or resource.identifier in present_ids):
                    api = self.api(kind)
                    attempt(
                        f"{kind}.delete",
                        api.delete if kind == "dataset" else api.delete_result,
                        resource.identifier,
                    )
        try:
            self.wait("cleanup.empty", lambda: not any(self.inventory().values()))
            for resource in self.resources:
                resource.deleted = True
        except Exception as error:
            self.cleanup_errors.append({"operation": "verify_empty", **error_record(error)})
        return not self.cleanup_errors
