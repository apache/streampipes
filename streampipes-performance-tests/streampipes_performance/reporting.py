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

"""Atomic versioned JSON reports and flat CSV summaries."""

import csv
import json
from pathlib import Path


def metric_name(path):
    """Human-readable CSV names; the separate key preserves import semantics."""
    parts = path.split(".")
    if parts[0] in ("operations", "diagnostics"):
        unit = "samples" if parts[-1] in ("count", "errors") else "ms"
        return f"{' '.join(parts[1:-1])} · {parts[-1]} ({unit})"
    if " time" in parts[0]:
        unit = "samples" if parts[-1] in ("count", "errors") else "ms"
        return f"{parts[0]} · {parts[-1]} ({unit})"
    if parts[0] == "phase_durations_ms":
        return f"{parts[-1].capitalize()} time (ms)"
    labels = {"processing_latency_ms": "Event latency", "event_gaps_ms": "Event gap",
              "duration_seconds": "Duration", "queries_per_second": "Query throughput"}
    words = []
    for part in parts:
        if part in ("query_workloads", "event_latencies"):
            words.append("Query workload" if part == "query_workloads" else "Event")
        elif part.isdigit():
            words.append(str(int(part) + 1))
        else:
            words.append(labels.get(part, part.replace("_", " ")))
    label = " · ".join(words)
    return label[0].upper() + label[1:]


def atomic_json(path: Path, value):
    temporary = path.with_suffix(path.suffix + ".tmp")
    temporary.write_text(json.dumps(value, indent=2, allow_nan=False) + "\n")
    temporary.replace(path)


def write_summary(directory, results):
    with (directory / "summary.csv").open("w", newline="") as output:
        fields = [
            "scenario_id",
            "case_type",
            "status",
            "parameters",
            "repetition",
            "cleanup_strategy",
            "metric",
            "metric_key",
            "value",
        ]
        writer = csv.DictWriter(output, fieldnames=fields)
        writer.writeheader()

        def flatten(value, prefix=""):
            for key, item in value.items():
                name = f"{prefix}.{key}" if prefix else key
                if isinstance(item, dict):
                    yield from flatten(item, name)
                elif isinstance(item, list):
                    for index, element in enumerate(item):
                        if isinstance(element, dict):
                            yield from flatten(element, f"{name}.{index}")
                elif isinstance(item, (float, int)) or item is None:
                    yield name, item

        for result in results:
            for name, value in flatten(
                {**result["metrics"], "phase_durations_ms": result["phase_durations_ms"]}
            ):
                writer.writerow(
                    dict(
                        scenario_id=result["scenario"]["id"],
                        case_type=result["scenario"]["type"],
                        status=result["status"],
                        parameters=json.dumps(result["scenario"]["parameters"], sort_keys=True),
                        repetition=result["scenario"]["repetition"],
                        cleanup_strategy=result["cleanup"]["strategy"],
                        metric=metric_name(name),
                        metric_key=name,
                        value=value,
                    )
                )
