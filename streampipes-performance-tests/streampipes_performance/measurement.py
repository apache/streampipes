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

"""Complete, bounded measurement-window collection."""

import hashlib
import json
import math
from datetime import datetime, timezone

from requests import HTTPError

from streampipes_performance.metrics import summarize
from streampipes_performance.runtime import missing


def timestamp_ms(value):
    if isinstance(value, str):
        try:
            value = float(value)
        except ValueError:
            value = datetime.fromisoformat(value.replace("Z", "+00:00")).timestamp() * 1000
    value = float(value)
    if not math.isfinite(value):
        raise ValueError("Invalid event timestamp")
    return value


def collect_pages(context, name, start_ms, end_ms, page_size, max_pages, deadline):
    timestamps = []
    fingerprint = hashlib.sha256()
    for page in range(max_pages):
        if context.clock.perf_counter() >= deadline:
            return timestamps, fingerprint.hexdigest(), False
        result = context.call(
            "dataset.query",
            context.client.dataLakeMeasureApi.get,
            name,
            start_date=datetime.fromtimestamp(start_ms / 1000, timezone.utc),
            end_date=datetime.fromtimestamp((end_ms - 1) / 1000, timezone.utc),
            limit=page_size,
            offset=page * page_size,
            order="ASC",
        )
        if result.query_status != "OK":
            return timestamps, fingerprint.hexdigest(), False
        rows = [row for series in result.all_data_series for row in series.rows]
        headers = result.headers or []
        if rows:
            timestamp_index = headers.index("time") if "time" in headers else headers.index("timestamp")
            timestamps.extend(timestamp_ms(row[timestamp_index]) for row in rows)
            fingerprint.update(json.dumps(rows, sort_keys=True).encode())
        if len(rows) < page_size:
            return timestamps, fingerprint.hexdigest(), True
    return timestamps, fingerprint.hexdigest(), False


def collect_window(context, names, start_ms, end_ms, *, wait_time_ms, page_size, max_pages):
    context.log(
        "Collecting %d datasets for window [%s, %s); drain timeout %.1f s",
        len(names),
        start_ms,
        end_ms,
        context.timing.drain_timeout_seconds,
    )
    deadline = context.clock.perf_counter() + context.timing.drain_timeout_seconds
    last_fingerprint = None
    stable_since = context.clock.perf_counter()
    details = []
    complete = False
    queried = set()
    while context.clock.perf_counter() < deadline:
        details, fingerprints = [], []
        all_complete = True
        for name in names:
            if name not in queried:
                context.log("Querying measurement %s", name)
                queried.add(name)
            try:
                metadata = context.call("dataset.lookup", context.client.datasetApi.get_by_name, name)
                context.track("dataset", metadata["elementId"], name)
                timestamps, fingerprint, fetched = collect_pages(
                    context,
                    metadata.get("measureName", name),
                    start_ms,
                    end_ms,
                    page_size,
                    max_pages,
                    deadline,
                )
            except HTTPError as error:
                if not missing(error):
                    raise
                timestamps, fingerprint, fetched = [], "missing", False
            ordered_timestamps = sorted(timestamps)
            gaps = [b - a for a, b in zip(ordered_timestamps, ordered_timestamps[1:])]
            details.append(
                {
                    "name": name,
                    "events": len(timestamps),
                    "event_gaps_ms": summarize(gaps),
                    "pages_complete": fetched,
                }
            )
            fingerprints.append(fingerprint)
            all_complete &= fetched
        now = context.clock.perf_counter()
        if fingerprints != last_fingerprint or not all_complete:
            stable_since = now
            last_fingerprint = fingerprints
        if all_complete and now - stable_since >= context.timing.stable_seconds:
            complete = True
            break
        context.clock.sleep(min(context.timing.poll_seconds, max(0, deadline - now)))
    observed = sum(item["events"] for item in details)
    seconds = (end_ms - start_ms) / 1000
    expected = len(names) * seconds * 1000 / wait_time_ms
    context.log(
        "Collection %s: events=%s/%s, throughput=%.3f events/s",
        "complete" if complete else "incomplete",
        observed,
        expected,
        observed / seconds,
    )
    return {
        "collection_complete": complete,
        "datasets": details,
        "observed_events": observed,
        "expected_events": expected,
        "persisted_events_per_second": observed / seconds,
        "throughput_ratio": observed / expected if expected else None,
        "window_start_ms": start_ms,
        "window_end_ms_exclusive": end_ms,
        "collection_policy": "bounded stable snapshot after stopping sources",
    }
