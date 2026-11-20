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

"""Discover supplied configurations and describe resolved workloads offline."""

import json
import sys
from pathlib import Path

from streampipes_performance.cases import REGISTRY


def config_directories():
    from streampipes_performance.plugins import CONFIG_DIRECTORIES, load

    load()
    return [Path.cwd() / "run_configs", *CONFIG_DIRECTORIES,
            Path(__file__).resolve().parents[1] / "run_configs",
            Path(sys.prefix) / "share/streampipes-performance/run_configs"]


def configurations():
    found = {}
    for directory in config_directories():
        for path in sorted(directory.glob("*.json")):
            found.setdefault(path.stem, path)
    return found


def config_path(value):
    path = Path(value)
    if path.is_file() or path.suffix or len(path.parts) > 1:
        return path.resolve()
    available = configurations()
    if value not in available:
        raise ValueError(f"Unknown configuration {value!r}; use configs list or provide a JSON path")
    return available[value].resolve()


def case_type(name):
    aliases = {kind.split(".")[1].replace("_", "-"): kind for kind in REGISTRY}
    name = aliases.get(name, name)
    if name not in REGISTRY:
        raise ValueError("Unknown test; use tests list")
    return name


def describe_test(name):
    kind = case_type(name)
    case = REGISTRY[kind]
    from streampipes_performance.targets import TARGETS
    targets = [name for name, target in TARGETS.items() if target.supports(case)]
    print(f"{kind}\n{case.documentation.description}\nTargets: {', '.join(targets)}\nParameters:")
    for field, model in case.Parameters.model_fields.items():
        default = "required" if model.is_required() else f"default: {model.default}"
        print(f"  {field} ({default})\n    {case.documentation.parameter_descriptions[field]}")
    from types import SimpleNamespace
    from streampipes_performance.readiness import required_services
    from streampipes_performance.settings import RuntimeSettings
    print("Readiness services: " + ", ".join(sorted(required_services([SimpleNamespace(type=kind)], RuntimeSettings()))))
    print("Metrics:")
    for field, description in case.documentation.metrics.items():
        print(f"  {field}: {description}")


def execution_plan(config, scenarios, settings, path):
    from streampipes_performance.readiness import required_services

    cases = []
    for case in config.cases:
        matching = [s for s in scenarios if s.id.rsplit("-m", 1)[0] == case.id]
        module = sys.modules[REGISTRY[case.type].__module__]
        queries = list(getattr(module, "QUERIES", {}))
        if "queries" in REGISTRY[case.type].Parameters.model_fields:
            # Cases with a query selection list only the selected presets.
            queries = sorted({query for s in matching for query in
                              (s.parameters.get("queries") or queries)})
        parameters = {}
        for scenario in matching:
            for key, value in scenario.parameters.items():
                if value not in parameters.setdefault(key, []):
                    parameters[key].append(value)
        cases.append({"id": case.id, "type": case.type, "description": REGISTRY[case.type].documentation.description,
                      "parameters": parameters, "scenarios": len(matching), "queries": queries,
                      "query_repetitions": getattr(module, "ITERATIONS", None),
                      "cleanup": sorted({s.cleanup.strategy for s in matching})})
    return {"configuration": str(path), "target": config.target, "cases": cases,
            "scenario_count": len(scenarios), "required_services": sorted(required_services(scenarios, settings)),
            "settings": settings.redacted(), "scenarios": [s.model_dump() for s in scenarios]}


def print_plan(plan):
    print(f"Configuration: {plan['configuration']}\nTarget: {plan['target']}\nScenarios: {plan['scenario_count']}")
    for case in plan["cases"]:
        print(f"\n{case['id']} ({case['type']}): {case['scenarios']} scenarios\n  {case['description']}")
        for key, values in case["parameters"].items():
            display = [value.get("label", value) if isinstance(value, dict) else value for value in values]
            print(f"  {key}: {json.dumps(display)}")
        if case["queries"]:
            print(f"  Queries: {', '.join(case['queries'])}; repetitions: {case['query_repetitions']}")
        if case["type"] == "streampipes.dataset_query":
            print("  Query result row limit: none")
        print(f"  Cleanup: {', '.join(case['cleanup'])}")
    settings = plan["settings"]
    print(f"\nRequired services: {', '.join(plan['required_services'])}")
    print(f"Backend: {settings['url'] or 'not configured'}")
    print(f"Output: {settings['output_directory']}")
    print(f"Readiness: {'enabled' if settings['wait_ready'] else 'disabled'}")
    print(f"Email: {', '.join(settings['email_to']) if settings['email_to'] else 'disabled'}")
