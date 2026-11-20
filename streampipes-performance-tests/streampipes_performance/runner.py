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

"""Sequential isolated RunConfig execution with durable partial results."""

import logging
import platform
import time
import uuid
from dataclasses import asdict
from datetime import datetime, timezone
from functools import partial
from pathlib import Path

from streampipes_performance.cases import REGISTRY
from streampipes_performance.targets import target
from streampipes_performance.metrics import operation_metrics, threshold_failures
from streampipes_performance.reporting import atomic_json, write_summary
from streampipes_performance.report.run_report import write_report
from streampipes_performance.runtime import Context, error_record

logger = logging.getLogger(__name__)


def execute_run_config(run_config, scenarios, client, output_directory=None, clock=time, runtime_settings=None):
    run_id = datetime.now(timezone.utc).strftime("%Y%m%dT%H%M%SZ") + "-" + uuid.uuid4().hex[:8]
    directory = Path(output_directory or run_config.output_directory) / run_id
    directory.mkdir(parents=True, exist_ok=False)
    run_target = target(run_config.target)
    extra_apis = run_target.apis(client, scenarios)
    manifest = {
        "schema_version": 1,
        "run_id": run_id,
        "status": "running",
        "target": run_config.target,
        "timing": run_config.execution.timing.model_dump(),
        "environment": {
            "python": platform.python_version(),
            "platform": platform.platform(),
            "server_version": str(getattr(client, "server_version", "unknown")),
        },
        "planned_scenarios": [s.model_dump() for s in scenarios],
        "results": [],
    }
    if runtime_settings is not None:
        manifest["settings"] = runtime_settings.redacted()
    atomic_json(directory / "manifest.json", manifest)
    results = []
    logger.info("Starting performance run %s: %d scenarios; results: %s", run_id, len(scenarios), directory)
    try:
        logger.info("Checking that the instance is empty")
        Context(client, run_config.execution.timing, run_id, clock=clock, extra_apis=dict(extra_apis),
                target=run_target).assert_empty()
        for index, scenario in enumerate(scenarios, 1):
            if hasattr(client, "request_session"):
                client.request_session.request = partial(
                    client.request_session.request, timeout=scenario.timing.request_timeout_seconds
                )
            context = Context(
                client,
                scenario.timing,
                f"perf-{uuid.uuid4().hex[:12]}",
                clock=clock,
                cleanup_config=scenario.cleanup,
                scenario_id=scenario.id,
                extra_apis=dict(extra_apis),
                runtime_settings=runtime_settings,
                target=run_target,
            )
            context.log(
                "Running case %d/%d: %s; resources=%s, interval=%s ms, repetition=%d, cleanup=%s",
                index,
                len(scenarios),
                scenario.type,
                next((scenario.parameters[key] for key in (
                    "number_of_assets", "number_of_datasets", "number_of_adapters", "number_of_pipelines",
                ) if key in scenario.parameters), "n/a"),
                scenario.parameters.get("wait_time_ms", "n/a"),
                scenario.repetition,
                scenario.cleanup.strategy,
            )
            case = (REGISTRY[scenario.type](scenario.parameters, runtime_settings=runtime_settings)
                    if runtime_settings is not None else REGISTRY[scenario.type](scenario.parameters))
            result = {
                "schema_version": 1,
                "run_id": run_id,
                "scenario": scenario.model_dump(),
                "environment": manifest["environment"],
                "timing": scenario.timing.model_dump(),
                "status": "running",
                "metrics": {},
                "errors": [],
                "phase_durations_ms": {},
                "cleanup": {"status": "pending", "strategy": scenario.cleanup.strategy},
            }
            interrupted = False
            path = directory / f"{scenario.id}.json"

            def phase(name, function):
                context.phase = name
                context.log("Starting %s", name)
                start = clock.perf_counter()
                outcome = "completed"
                try:
                    value = function()
                    if name == "cleanup" and value is False:
                        outcome = "failed"
                    return value
                except (Exception, KeyboardInterrupt) as error:
                    outcome = "failed"
                    context.log("%s failed: %s", name, error_record(error), level=logging.ERROR)
                    raise
                finally:
                    result["phase_durations_ms"][name] = (clock.perf_counter() - start) * 1000
                    context.log("%s %s in %.1f ms", name, outcome, result["phase_durations_ms"][name])

            def snapshot():
                result["samples"] = context.samples
                result["resources"] = [asdict(r) for r in context.resources]
                result["metrics"].update(operation_metrics(context.samples))
                atomic_json(path, result)

            try:
                phase("setup", lambda: case.setup(context))
                phase("measurement", lambda: case.run(context))
            except (Exception, KeyboardInterrupt) as error:
                interrupted = isinstance(error, KeyboardInterrupt)
                result["errors"].append({"phase": context.phase, **error_record(error)})
            finally:
                try:
                    result["metrics"].update(phase("preparation", lambda: case.prepare_results(context)))
                except (Exception, KeyboardInterrupt) as error:
                    interrupted |= isinstance(error, KeyboardInterrupt)
                    result["errors"].append({"phase": "preparation", **error_record(error)})
                try:
                    snapshot()
                finally:
                    try:
                        cleaned = phase("cleanup", lambda: case.cleanup(context))
                    except (Exception, KeyboardInterrupt) as error:
                        interrupted |= isinstance(error, KeyboardInterrupt)
                        cleaned = False
                        context.cleanup_errors.append(error_record(error))
                    result["cleanup"] = {
                        "strategy": scenario.cleanup.strategy,
                        "status": "completed" if cleaned else "failed",
                        "errors": context.cleanup_errors,
                    }
            result["metrics"].update(operation_metrics(context.samples))
            result["threshold_failures"] = threshold_failures(scenario.thresholds, result["metrics"])
            incomplete = result["metrics"].get("collection_complete") is False
            failed = result["errors"] or result["threshold_failures"] or incomplete or not cleaned
            result["status"] = "interrupted" if interrupted else "failed" if failed else "passed"
            snapshot()
            context.log(
                "Case %s; events=%s/%s, throughput=%s events/s, ratio=%s, cleanup=%s",
                result["status"],
                result["metrics"].get("observed_events", "n/a"),
                result["metrics"].get("expected_events", "n/a"),
                result["metrics"].get("persisted_events_per_second", "n/a"),
                result["metrics"].get("throughput_ratio", "n/a"),
                result["cleanup"]["status"],
                level=logging.INFO if result["status"] == "passed" else logging.WARNING,
            )
            if result["threshold_failures"]:
                context.log("Threshold failures: %s", result["threshold_failures"], level=logging.WARNING)
            if context.cleanup_errors:
                context.log("Cleanup errors: %s", context.cleanup_errors, level=logging.ERROR)
            context.log("Wrote JSON result to %s", path)
            results.append(result)
            manifest["results"].append(
                {"scenario_id": scenario.id, "file": path.name, "status": result["status"]}
            )
            write_summary(directory, results)
            atomic_json(directory / "manifest.json", manifest)
            if not cleaned or interrupted:
                context.log(
                    "Stopping run: %s",
                    "interrupted" if interrupted else "cleanup failed",
                    level=logging.WARNING,
                )
                break
        if any(r["status"] == "interrupted" for r in results):
            manifest["status"] = "interrupted"
        elif len(results) == len(scenarios) and all(r["status"] == "passed" for r in results):
            manifest["status"] = "passed"
        else:
            manifest["status"] = "failed"
    except (Exception, KeyboardInterrupt) as error:
        manifest["status"] = "interrupted" if isinstance(error, KeyboardInterrupt) else "failed"
        manifest["error"] = error_record(error)
        logger.error("Run failed: %s", error_record(error))
    finally:
        atomic_json(directory / "manifest.json", manifest)
    logger.info(
        "Run %s: %d/%d scenarios finished; manifest: %s",
        manifest["status"],
        len(results),
        len(scenarios),
        directory / "manifest.json",
    )
    if results:
        logger.info("Wrote CSV summary to %s", directory / "summary.csv")
    report = write_report(directory)
    logger.info("Wrote HTML report to %s", report)
    return directory, manifest["status"] == "passed"
