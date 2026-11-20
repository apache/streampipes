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

"""Resource operations shared by cases; workload choices belong to case.py."""

import re
from dataclasses import dataclass
from urllib.request import urlopen

from streampipes_performance.cases.influx import InfluxConfig, InfluxUploader
from streampipes_performance.runtime import EvaluationError
from streampipes_performance.settings import resolve_settings

STORAGE_METRICS_ENV = "INFLUXDB_METRICS_URL"


@dataclass(frozen=True)
class AdapterSource:
    identifier: str
    stream: str
    name: str


def create_adapter(context, identifier, name, payload, *, missing_stream_error="Adapter has no stream ID"):
    context.track("adapter", identifier, name)
    context.call("adapter.create", context.client.adapterApi.create_result, payload)
    context.wait("adapter.created", lambda: context.details("adapter", identifier).get("running") is True)
    details = context.details("adapter", identifier)
    stream = details.get("correspondingDataStreamElementId") or (details.get("dataStream") or {}).get("elementId")
    if not stream:
        raise EvaluationError(missing_stream_error)
    return AdapterSource(identifier, stream, name)


def create_pipeline(context, identifier, name, payload):
    context.track("pipeline", identifier, name)
    context.datasets.append(name)
    context.call("pipeline.create", context.client.pipelineApi.create_result, payload)
    context.wait("pipeline.created", lambda: context.details("pipeline", identifier).get("running") is True)
    return identifier


def upload_rows(context, name, headers, count, batch_size, rows, *, tag_fields=()):
    """Prefer direct writes, retrying a failed batch through the dataset API."""
    uploader = None
    try:
        settings = getattr(context, "runtime_settings", None) or resolve_settings()
        if settings.influx_token and settings.influx_token.get_secret_value().strip():
            uploader = InfluxUploader(InfluxConfig(
                url=settings.influx_url, org=settings.influx_org, bucket=settings.influx_bucket,
                token=settings.influx_token,
            ), tag_fields)
            context.log("Dataset %s: uploading directly to InfluxDB", name)
        else:
            context.log("Dataset %s: InfluxDB token is not configured; using StreamPipes API", name)
    except ValueError:
        context.log("Dataset %s: invalid Influx configuration; using StreamPipes API", name)

    def upload(name, headers, batch):
        nonlocal uploader
        if uploader is not None:
            try:
                return uploader.upload(name, headers, batch)
            except EvaluationError:
                context.log("Dataset %s: Influx upload failed; using StreamPipes API for this and remaining batches", name)
                uploader = None
        return context.client.datasetApi.upload(name, headers, batch)

    next_progress_percent = 10
    for start in range(0, count, batch_size):
        batch = list(rows(start, min(start + batch_size, count)))
        context.call("dataset.upload", upload, name, headers, batch)
        uploaded = start + len(batch)
        percent = uploaded * 100 // count
        if percent >= next_progress_percent:
            context.log("Dataset %s: uploaded %d%% (%d/%d rows)", name, percent, uploaded, count)
            next_progress_percent = (percent // 10 + 1) * 10


def wait_for_rows(context, identifier, name, expected):
    observed = None

    def ready():
        nonlocal observed
        count = context.call("dataset.count", context.client.datasetApi.count, identifier)
        if type(count) is not int or count < 0:
            raise EvaluationError("dataset_count_invalid_response")
        if count != observed:
            context.log("Dataset %s: persisted %d/%d rows", name, count, expected)
        observed = count
        return count == expected

    try:
        context.wait("dataset.populated", ready)
    except TimeoutError as error:
        raise EvaluationError(
            f"dataset_readiness_timeout: expected_rows={expected}, observed_rows={observed}"
        ) from error


def storage_metrics(url, timeout):
    """Read the InfluxDB Prometheus endpoint and sum its storage gauges."""
    with urlopen(url, timeout=timeout) as response:
        return storage_metrics_from_text(response.read().decode("utf-8", "replace"))


def storage_metrics_from_text(text):
    """Sum the gauges that describe unflushed and uncompacted data across all shards."""
    totals = {"cache_bytes": 0.0, "compactions_queued": 0.0, "compactions_done": 0.0, "tsm_files": 0.0}
    names = {
        "storage_cache_inuse_bytes": "cache_bytes",
        "storage_compactions_queued": "compactions_queued",
        "storage_compactions_duration_seconds_count": "compactions_done",
        "storage_tsm_files_total": "tsm_files",
    }
    for match in re.finditer(r"^(storage_\w+)(?:\{[^}]*\})? (\S+)$", text, re.M):
        key = names.get(match.group(1))
        if key:
            totals[key] += float(match.group(2))
    return totals


def wait_for_storage_settled(context):
    """Wait until uploaded rows reached compacted TSM files, so queries measure the production storage state.

    Reads the InfluxDB Prometheus endpoint named by INFLUXDB_METRICS_URL; without it the step is skipped and
    queries run against whatever the cache holds. Settled means: no cache data, and neither the number of
    finished compactions nor the number of TSM files changed for settle_stable_seconds. The queued-compactions
    gauge is logged only: InfluxDB 2.6 leaves it at 1 for shards whose full compaction was planned but skipped.
    Recorded as readiness sample storage.settled.
    """
    settings = getattr(context, "runtime_settings", None) or resolve_settings()
    url = settings.influxdb_metrics_url
    if not url:
        return None
    timing = context.timing
    start = context.clock.perf_counter()
    success = False
    quiet_since = None
    previous = None
    logged_at = None
    try:
        while True:
            totals = storage_metrics(url, timing.request_timeout_seconds)
            now = context.clock.perf_counter()
            progress = (totals["compactions_done"], totals["tsm_files"])
            # A non-empty cache is never quiet; compaction progress resets the quiet period.
            if previous is None or progress != previous or totals["cache_bytes"] > 0:
                quiet_since = now
            if logged_at is None or previous is None or progress != previous or now - logged_at >= 30:
                context.log("Storage: cache %.1f MB, %d queued, %d compactions done, %d TSM files",
                            totals["cache_bytes"] / 1e6, totals["compactions_queued"],
                            totals["compactions_done"], totals["tsm_files"])
                logged_at = now
            previous = progress
            if now - quiet_since >= timing.settle_stable_seconds:
                success = True
                context.log("Storage settled after %.1f s: %d TSM files", now - start, totals["tsm_files"])
                return totals
            if now - start >= timing.settle_timeout_seconds:
                raise EvaluationError(
                    f"storage_settle_timeout: cache_bytes={totals['cache_bytes']:.0f}, "
                    f"compactions_done={totals['compactions_done']:.0f}, tsm_files={totals['tsm_files']:.0f}"
                )
            context.clock.sleep(min(max(timing.poll_seconds, 2.0), timing.settle_timeout_seconds - (now - start)))
    finally:
        context.samples.append(dict(
            phase=context.phase, kind="readiness", operation="storage.settled", success=success,
            duration_ms=(context.clock.perf_counter() - start) * 1000,
        ))
