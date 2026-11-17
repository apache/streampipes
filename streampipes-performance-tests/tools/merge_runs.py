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

"""Merge scenarios from several result runs into one result directory.

Usage:
    python tools/merge_runs.py OUTPUT_DIR SELECTION [SELECTION ...]

Each SELECTION is one of
    RUN_DIR                      all scenarios of that run
    RUN_DIR:SCENARIO_ID          one scenario
    RUN_DIR:SCENARIO_ID=NEW_ID   one scenario, renamed (for repeated attempts)

Later selections replace earlier ones with the same scenario ID, so list the
original run first and the reruns afterwards. The merged manifest records the
source run of every scenario in `merged_from`; each scenario JSON carries
`source_run_id` and `source_scenario_id`. Build the report afterwards with
`python -m performance report OUTPUT_DIR`. Source directories are not modified.
"""

import csv
import json
import sys
from pathlib import Path


def load_run(directory):
    manifest = json.loads((directory / "manifest.json").read_text())
    planned = {entry["id"]: entry for entry in manifest["planned_scenarios"]}
    return manifest, planned


def main(argv):
    if len(argv) < 3:
        print(__doc__)
        return 2
    output = Path(argv[1])
    if output.exists():
        print(f"{output} already exists")
        return 2
    chosen = {}  # new scenario id -> (run dir, manifest, result entry, planned entry)
    base = None
    for selection in argv[2:]:
        run, _, scenario = selection.partition(":")
        scenario, _, new_id = scenario.partition("=")
        directory = Path(run)
        manifest, planned = load_run(directory)
        base = base or manifest
        for result in manifest["results"]:
            if scenario and result["scenario_id"] != scenario:
                continue
            target = new_id or result["scenario_id"]
            chosen[target] = (directory, manifest, result, planned[result["scenario_id"]])
        if scenario and not any(r["scenario_id"] == scenario for r in manifest["results"]):
            print(f"{scenario} not found in {directory}")
            return 2
    output.mkdir(parents=True)
    results, planned_entries, merged_from, rows, header = [], [], [], [], None
    for new_id, (directory, manifest, result, planned) in chosen.items():
        data = json.loads((directory / result["file"]).read_text())
        source_id = result["scenario_id"]
        data["scenario"]["id"] = new_id
        data.update(run_id=output.name, source_run_id=manifest["run_id"], source_scenario_id=source_id)
        planned = {**planned, "id": new_id}
        file_name = f"{new_id}.json"
        (output / file_name).write_text(json.dumps(data, indent=2))
        results.append({**result, "scenario_id": new_id, "file": file_name})
        planned_entries.append(planned)
        merged_from.append({"run_id": manifest["run_id"], "scenario": source_id, "as": new_id})
        with (directory / "summary.csv").open(newline="") as handle:
            reader = csv.reader(handle)
            header = next(reader)
            rows += [[new_id, *row[1:]] for row in reader if row[0] == source_id]
    status = "passed" if all(r["status"] == "passed" for r in results) else "failed"
    manifest = {**base, "run_id": output.name, "status": status, "planned_scenarios": planned_entries,
                "results": results, "merged_from": merged_from}
    (output / "manifest.json").write_text(json.dumps(manifest, indent=2))
    with (output / "summary.csv").open("w", newline="") as handle:
        writer = csv.writer(handle)
        writer.writerow(header)
        writer.writerows(rows)
    print(f"{output}: {len(results)} scenarios, status {status}")
    for result in results:
        print(f"  {result['scenario_id']}: {result['status']}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))
