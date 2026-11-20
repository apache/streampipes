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

import json

from streampipes_performance.cases import REGISTRY
from script_configs import ADAPTER_PASS_THROUGH
from streampipes_performance.cli import main
from streampipes_performance.report.run_report import load_run, write_report


def write_run(directory):
    scenario = {
        "id": "adapter-latency-m1-r1",
        "type": "streampipes.adapter_latency",
        "documentation": REGISTRY["streampipes.adapter_latency"].documentation.model_dump(),
        "repetition": 1,
        "parameters": {"number_of_adapters": 1, "transformation": ADAPTER_PASS_THROUGH},
    }
    result = {
        "run_id": "20260101T000000Z-abc",
        "scenario": scenario,
        "status": "passed",
        "metrics": {
            "processing_latency_ms": {"count": 1, "min": 1.0, "mean": 1.0, "max": 1.0},
            "event_latencies": [{"event_id": "</script>", "latency_ms": 1.23456, "elapsed_ms": 10.04}],
            "filtered_event_ids": ["x"],
        },
        "errors": [],
        "phase_durations_ms": {"setup": 1.0},
        "cleanup": {"strategy": "delete", "status": "completed", "errors": []},
        "samples": [{"operation": "adapter.create"}],
        "resources": [{"kind": "adapter"}],
    }
    manifest = {
        "run_id": result["run_id"],
        "status": "passed",
        "target": "streampipes",
        "environment": {"server_version": "</script><b>"},
        "planned_scenarios": [scenario, scenario],
        "results": [{"scenario_id": scenario["id"], "file": "a.json", "status": "passed"}],
    }
    (directory / "a.json").write_text(json.dumps(result))
    (directory / "manifest.json").write_text(json.dumps(manifest))


def test_load_run_keeps_only_report_data_and_maps_case_documents(tmp_path):
    write_run(tmp_path)
    data = load_run(tmp_path)
    scenario = data["scenarios"][0]
    assert scenario["scenario"]["documentation"] == REGISTRY["streampipes.adapter_latency"].documentation.model_dump()
    assert data["run"]["planned_scenarios"] == 2
    assert "samples" not in scenario and "resources" not in scenario
    assert scenario["metrics"]["event_latencies"] == [[10.0, 1.235]]
    assert "filtered_event_ids" not in scenario["metrics"]
    assert data["readmes"]["streampipes.adapter_latency"].startswith("# Adapter latency")
    assert scenario["scenario"]["parameters"]["transformation"]["label"] == ADAPTER_PASS_THROUGH["label"]
    assert "presets" not in data


def test_report_embeds_escaped_data(tmp_path):
    write_run(tmp_path)
    html = write_report(tmp_path).read_text()
    embedded = html.split('<script id="report-data" type="application/json">')[1].split("</script>")[0]
    assert json.loads(embedded)["run"]["environment"]["server_version"] == "</script><b>"
    assert "<" not in embedded


def test_report_command(tmp_path, monkeypatch, capsys):
    write_run(tmp_path)
    monkeypatch.setattr("sys.argv", ["performance", "report", str(tmp_path)])
    assert main() == 0
    assert (tmp_path / "report.html").is_file()
    monkeypatch.setattr("sys.argv", ["performance", "report", str(tmp_path / "missing")])
    assert main() == 2


def test_report_preserves_request_examples_and_escapes_source(tmp_path):
    write_run(tmp_path)
    path = tmp_path / "a.json"
    result = json.loads(path.read_text())
    examples = [{"query": "example", "source": 'POST /api/query\n\nJSON body:\n{"filter": "</script>"}'}]
    result["metrics"]["query_requests"] = examples
    path.write_text(json.dumps(result))
    assert load_run(tmp_path)["scenarios"][0]["metrics"]["query_requests"] == examples
    html = write_report(tmp_path).read_text()
    embedded = html.split('<script id="report-data" type="application/json">')[1].split("</script>")[0]
    assert json.loads(embedded)["scenarios"][0]["metrics"]["query_requests"] == examples
    assert "<" not in embedded
    assert "<summary>Query requests</summary>" not in html
    assert "data-query-toggle" in html
    assert 'colspan="7"' in html
    assert "esc(row.source)" in html


def test_report_uses_configured_labels_and_renders_script_objects(tmp_path):
    import subprocess
    from copy import deepcopy

    write_run(tmp_path)
    data = load_run(tmp_path)
    first = data["scenarios"][0]
    script = {"label": "Customer <script> label", "script": "// custom <source>\nout.collect(event);"}
    first["scenario"]["parameters"]["transformation"] = script
    second = deepcopy(first)
    second["scenario"]["parameters"]["transformation"]["label"] = "Different label, same script"
    data["scenarios"].append(second)
    from streampipes_performance.report.run_report import TEMPLATE
    # Evaluate the actual report renderer with a minimal DOM; skip navigation.
    source = TEMPLATE.read_text().split("<script>")[1].split("</script>")[0]
    source = source.rsplit("      route();", 1)[0]
    harness = r'''
const fs = require('node:fs');
const vm = require('node:vm');
const {source, data} = JSON.parse(fs.readFileSync(0, 'utf8'));
const scope = vm.createContext({
  document: {
    getElementById: id => ({textContent: JSON.stringify(data)}),
    addEventListener: () => {},
  },
  window: {addEventListener: () => {}},
});
vm.runInContext(source, scope);
const result = vm.runInContext(`(() => {
  const item = buildCases()[0];
  const labels = item.configs.map(c => c.label);
  const html = item.configs.map(c => configSection(item, c));
  DATA.scenarios = DATA.scenarios.slice(0, 1);
  return {labels, html, singleLabel: buildCases()[0].configs[0].label};
})()`, scope);
process.stdout.write(JSON.stringify(result));
'''
    output = subprocess.run(["node", "-e", harness], input=json.dumps({"source": source, "data": data}),
                            capture_output=True, text=True, check=True, timeout=10)
    result = json.loads(output.stdout)
    assert result["labels"] == [script["label"], "Different label, same script"]
    assert result["singleLabel"] == script["label"]
    assert "Customer &lt;script&gt; label" in result["html"][0]
    assert "// custom &lt;source&gt;\nout.collect(event);" in result["html"][0]
    assert "<script>" not in result["html"][0]
