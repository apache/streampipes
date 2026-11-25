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

"""Metric calculations, with missing observations kept distinct from zero."""

import math
from collections import defaultdict


def summarize(values):
    values = [float(v) for v in values if math.isfinite(v)]

    return dict(
        count=len(values),
        min=min(values) if values else None,
        mean=sum(values) / len(values) if values else None,
        max=max(values) if values else None,
    )


def operation_name(operation):
    """Stable, readable names shared by every case and its raw samples."""
    if operation == "dataset.query" or operation.startswith("dataset.query."):
        return "Query time"
    from streampipes_performance.plugins import OPERATION_LABELS

    words = operation.replace("_", " ").split(".")
    label = OPERATION_LABELS.get(words[0])
    words = ["creation" if word == "create" else word for word in words]
    if label:
        return " ".join(part for part in (label, " ".join(words[1:]).lower(), "time") if part)
    return (" ".join(words) + " time").capitalize()


def operation_metrics(samples):
    groups = defaultdict(list)
    diagnostics = defaultdict(list)
    measured = [s for s in samples if s["phase"] in ("setup", "measurement") and s["kind"] == "api"]
    for sample in samples:
        # Explicit names also let consumers associate raw timings with summaries.
        sample["metric"] = (
            operation_name(sample["operation"])
            if sample["phase"] in ("setup", "measurement") and sample["kind"] == "api"
            else None
        )
        if sample["metric"] is None:
            name = operation_name(sample["operation"])
            qualifier = " readiness" if sample["kind"] == "wait" else ""
            diagnostics[f'{name} ({sample["phase"]}{qualifier})'].append(sample)
    for sample in measured:
        groups[sample["metric"]].append(sample)
    return {
        "operations": {
            name: {
                **summarize(s["duration_ms"] for s in group),
                "errors": sum(not s["success"] for s in group),
            }
            for name, group in groups.items()
        },
        **{
            name: {**summarize(s["duration_ms"] for s in group),
                   "errors": sum(not s["success"] for s in group)}
            for name, group in diagnostics.items()
        },
        "error_rate": sum(not s["success"] for s in measured) / len(measured) if measured else None,
    }


def threshold_failures(thresholds, metrics):
    failures = []
    if thresholds.max_operation_mean_ms is not None:
        eligible = {
            name: stats
            for name, stats in metrics.get("operations", {}).items()
            if stats["mean"] is not None
        }
        if not eligible:
            failures.append("operation mean unavailable")
        for name, stats in eligible.items():
            if stats["mean"] > thresholds.max_operation_mean_ms:
                failures.append(f"{name}: mean exceeds {thresholds.max_operation_mean_ms} ms")
    for key, bound, compare in [
        ("error_rate", thresholds.max_error_rate, lambda value, limit: value > limit),
        ("throughput_ratio", thresholds.min_throughput_ratio, lambda value, limit: value < limit),
    ]:
        if bound is not None and (metrics.get(key) is None or compare(metrics[key], bound)):
            failures.append(f"{key}: unavailable or threshold violated ({bound})")
    return failures
