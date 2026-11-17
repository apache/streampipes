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

"""Asset workflows with a fixed HTTP session and thread per concurrent user."""

from concurrent.futures import ThreadPoolExecutor

from streampipes_performance.metrics import summarize


def execute_asset_queries(context, clients, assets, query_names, iterations, query):
    """Run every preset and repetition of an asset on its assigned worker.

    query(client, asset, query_name, job_index) returns the validated row count.
    Per-query throughput uses the common elapsed time of the complete workflow.
    """
    clients = clients[:len(assets)]
    query_names = tuple(query_names)
    sample_start = len(context.samples)

    def worker(index, client):
        totals = {name: [0, 0, 0] for name in query_names}
        for asset_index in range(index, len(assets), len(clients)):
            asset = assets[asset_index]
            for iteration in range(iterations):
                job = iteration * len(assets) + asset_index
                for name in query_names:
                    try:
                        rows = context.call(f"dataset.query.{name}", query, client, asset, name, job)
                        totals[name][0] += 1
                        totals[name][1] += rows
                    except Exception:
                        totals[name][2] += 1
        return totals

    context.log("Asset workflows: assets=%d, concurrency=%d, repetitions=%d",
                len(assets), len(clients), iterations)
    start = context.clock.perf_counter()
    if len(clients) == 1:
        outcomes = [worker(0, clients[0])]
    else:
        with ThreadPoolExecutor(max_workers=len(clients)) as executor:
            futures = [executor.submit(worker, index, client) for index, client in enumerate(clients)]
            outcomes = [future.result() for future in futures]
    elapsed = context.clock.perf_counter() - start
    samples = context.samples[sample_start:]
    results = []
    for name in query_names:
        completed, returned_rows, errors = (
            sum(outcome[name][index] for outcome in outcomes) for index in range(3)
        )
        results.append({
            "mode": "sequential" if len(clients) == 1 else "parallel_many",
            "query": name, "concurrency": len(clients),
            "requests": iterations * len(assets), "completed": completed, "errors": errors,
            "returned_rows": returned_rows, "duration_seconds": elapsed,
            "duration_scope": "asset_workflows",
            "queries_per_second": completed / elapsed if elapsed else None,
            "query_time_ms": summarize(
                sample["duration_ms"] for sample in samples
                if sample["operation"] == f"dataset.query.{name}"
            ),
        })
    return results


def query_requests(query_names, query):
    """Describe the first request of each preset using its real request builder.

    query(client, name) uses a recording transport: no HTTP request is sent.
    Client queries are shown as Python client calls; raw API queries as HTTP
    requests with their JSON body and query parameters.
    """
    import json
    from pprint import pformat

    class Captured(Exception):
        pass

    class RequestRecorder:
        def __init__(self):
            self.dataLakeMeasureApi = self
            self.source = None

        def request_json(self, method, path, **kwargs):
            lines = [f"{method} /{path}"]
            if "params" in kwargs:
                lines.extend(["", "Query parameters:", json.dumps(kwargs["params"], indent=2)])
            if "json" in kwargs:
                lines.extend(["", "JSON body:", json.dumps(kwargs["json"], indent=2)])
            self.source = "\n".join(lines)
            raise Captured()

        def get(self, identifier, **kwargs):
            options = "\n".join(f"    {key}={pformat(value)}," for key, value in kwargs.items())
            self.source = f"client.dataLakeMeasureApi.get(\n    {identifier!r},\n{options}\n)"
            raise Captured()

    requests = []
    for name in query_names:
        recorder = RequestRecorder()
        try:
            query(recorder, name)
        except Captured:
            requests.append({"query": name, "source": recorder.source})
    return requests
