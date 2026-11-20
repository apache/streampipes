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

"""Self-contained HTML report for one result directory."""

import json
import re
import sys
from pathlib import Path

TEMPLATE = Path(__file__).with_name("run_report.html")
PLACEHOLDER = '<script id="report-data" type="application/json">null</script>'
# Case READMEs start with an HTML comment (the license header) that the report must not show.
LEADING_COMMENT = re.compile(r"\A\s*<!--.*?-->\s*", re.S)


def case_documents(types):
    """READMEs of the registered cases, keyed for the report."""
    from streampipes_performance.cases import REGISTRY

    readmes = {}
    for case_type in types:
        if case_type not in REGISTRY:
            continue
        module = sys.modules[REGISTRY[case_type].__module__]
        readme = Path(module.__file__).with_name("README.md")
        if readme.is_file():
            readmes[case_type] = LEADING_COMMENT.sub("", readme.read_text())
    return readmes


def products():
    """Display names of case-type prefixes, taken from the registered targets."""
    from streampipes_performance.targets import TARGETS

    return {name: target.display_name for name, target in TARGETS.items()}


def compact(result):
    """Keep what the report displays; raw API samples and resources stay in the scenario file."""
    result = {k: v for k, v in result.items() if k not in ("samples", "resources")}
    metrics = {k: v for k, v in result["metrics"].items() if k != "filtered_event_ids"}
    if "event_latencies" in metrics:
        metrics["event_latencies"] = [
            [round(e["elapsed_ms"], 1), round(e["latency_ms"], 3)] for e in metrics["event_latencies"]
        ]
    result["metrics"] = metrics
    return result


def load_run(directory):
    directory = Path(directory)
    manifest = json.loads((directory / "manifest.json").read_text())
    planned = manifest.pop("planned_scenarios", [])
    files = [entry["file"] for entry in manifest.pop("results", [])]
    scenarios = [compact(json.loads((directory / name).read_text())) for name in files]
    readmes = case_documents({s["scenario"]["type"] for s in scenarios})
    return {
        "run": {**manifest, "planned_scenarios": len(planned)},
        "scenarios": scenarios,
        "readmes": readmes,
        "products": products(),
    }


def write_report(directory, output=None):
    """Write report.html into the result directory and return its path."""
    output = Path(output) if output else Path(directory) / "report.html"
    # "<" is escaped so result content can never close the embedding script element.
    data = json.dumps(load_run(directory), allow_nan=False).replace("<", "\\u003c")
    template = TEMPLATE.read_text()
    if PLACEHOLDER not in template:
        raise ValueError(f"{TEMPLATE.name} has no report data placeholder")
    output.write_text(
        template.replace(PLACEHOLDER, f'<script id="report-data" type="application/json">{data}</script>')
    )
    return output
