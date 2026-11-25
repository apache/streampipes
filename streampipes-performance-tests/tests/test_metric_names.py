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

"""Readable saved timing names across benchmark families."""

import pytest

from streampipes_performance.metrics import operation_metrics
from streampipes_performance.reporting import metric_name


@pytest.mark.parametrize("operation,name", [
    ("dataset.query", "Query time"),
    ("adapter.start", "Adapter start time"),
    ("adapter.stop", "Adapter stop time"),
    ("pipeline.start", "Pipeline start time"),
    ("pipeline.stop", "Pipeline stop time"),
    ("dataset.upload", "Dataset upload time"),
])
def test_saved_operation_names(operation, name):
    samples = [{"operation": operation, "phase": "measurement", "kind": "api",
                "duration_ms": 12, "success": True}]
    metrics = operation_metrics(samples)
    assert list(metrics["operations"]) == [name]
    assert samples[0]["metric"] == name
    assert metric_name(f"operations.{name}.mean") == f"{name} · mean (ms)"


def test_cleanup_is_not_pooled_with_measured_start():
    samples = [{"operation": "adapter.stop", "phase": phase, "kind": "api",
                "duration_ms": duration, "success": True}
               for phase, duration in [("measurement", 10), ("cleanup", 1000)]]
    metrics = operation_metrics(samples)
    assert metrics["operations"]["Adapter stop time"]["mean"] == 10
    assert metrics["Adapter stop time (cleanup)"]["mean"] == 1000


def test_registered_operation_label_keeps_its_capitalization(monkeypatch):
    from streampipes_performance.plugins import OPERATION_LABELS

    monkeypatch.setitem(OPERATION_LABELS, "ml", "ML model")
    samples = [{"operation": "ml.create", "phase": "measurement", "kind": "api", "duration_ms": 1, "success": True}]
    assert list(operation_metrics(samples)["operations"]) == ["ML model creation time"]


def test_cleanup_metric_is_saved_without_diagnostic_prefix(tmp_path):
    import json
    from streampipes_performance.reporting import atomic_json

    metrics = operation_metrics([{"operation": "pipeline.delete", "phase": "cleanup", "kind": "api",
                                  "duration_ms": 15, "success": True}])
    path = tmp_path / "result.json"
    atomic_json(path, {"metrics": metrics})
    saved = json.loads(path.read_text())["metrics"]
    assert "diagnostics" not in saved
    assert saved["Pipeline delete time (cleanup)"]["max"] == 15
    assert metric_name("Pipeline delete time (cleanup).max") == "Pipeline delete time (cleanup) · max (ms)"
