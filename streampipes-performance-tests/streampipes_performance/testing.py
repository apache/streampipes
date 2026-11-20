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

"""In-memory fakes for testing cases and plugins without a StreamPipes deployment.

``Server`` implements the client endpoints used by the built-in cases; plugins
subclass it to add their own APIs (typically by overriding ``request_json``).
``Clock`` advances time only when a case sleeps. ``Broker`` replaces ``nats.connect``
and routes published events to the subjects registered in ``routes``.
"""

import json
from types import SimpleNamespace
from unittest.mock import Mock

from requests import HTTPError, Response


class Clock:
    def __init__(self):
        self.now = 0

    def time(self):
        return 1700000000 + self.now

    def perf_counter(self):
        return self.now

    def sleep(self, seconds):
        self.now += seconds


class Server:
    server_version = "test-version"

    def __init__(self):
        self.adapters, self.pipelines, self.datasets = {}, {}, {}
        self.adapterApi = Mock()
        self.pipelineApi = Mock()
        self.datasetApi = Mock()
        self.dataLakeMeasureApi = Mock()
        for api, records in [
            (self.adapterApi, self.adapters),
            (self.pipelineApi, self.pipelines),
            (self.datasetApi, self.datasets),
        ]:
            api.all.side_effect = lambda records=records: list(records.values())
            api.get.side_effect = lambda identifier, records=records: records[identifier]
            api.get_details.side_effect = lambda identifier, records=records: records[identifier]
            api.start_result.side_effect = lambda identifier, records=records: self.state(
                records, identifier, True
            )
            api.stop_result.side_effect = lambda identifier, force_stop=False, records=records: self.state(
                records, identifier, False
            )
            api.delete_result.side_effect = lambda identifier, records=records: records.pop(identifier, None)
            api.delete.side_effect = lambda identifier, records=records: records.pop(identifier, None)
        self.adapterApi.create_result.side_effect = self.create_adapter
        self.pipelineApi.create_result.side_effect = self.create_pipeline
        self.datasetApi.get_by_name.side_effect = self.lookup
        self.dataLakeMeasureApi.get.side_effect = self.query

    def state(self, records, identifier, running):
        if identifier not in records:
            response = Response()
            response.status_code = 404
            raise HTTPError(response=response)
        records[identifier]["running"] = running
        return {"success": True}

    def create_adapter(self, payload):
        assert payload.create_options.persist is False
        self.adapters[payload.id] = {
            "elementId": payload.id,
            "name": payload.name,
            "running": True,
            "correspondingDataStreamElementId": "stream-" + payload.id,
        }
        return {"success": True}

    def create_pipeline(self, payload):
        sink = payload.to_dict(use_source_names=True)["pipelineElements"][-1]
        assert sink["id"] == "org.apache.streampipes.sinks.internal.jvm.dataset"
        self.pipelines[payload.id] = {
            "elementId": payload.id,
            "pipelineId": payload.id,
            "name": payload.name,
            "running": True,
        }
        self.add_dataset(payload.name)
        return {"success": True}

    def add_dataset(self, name):
        self.datasets[name] = {"elementId": name, "measureName": name}

    def lookup(self, name):
        return self.datasets[name]

    def query(self, name, **kwargs):
        start = int(kwargs["start_date"].timestamp() * 1000)
        return SimpleNamespace(
            query_status="OK",
            headers=["time"],
            all_data_series=[SimpleNamespace(rows=[[start], [start + 1000]])],
        )

    def request_json(self, method, path, **kwargs):
        raise NotImplementedError(f"{method} {path}")

    def empty(self):
        return not any([self.adapters, self.pipelines, self.datasets])


class Broker:
    def __init__(self):
        self.routes = {}
        self.connections = []
        self.published = []
        self.filtered_sources = set()
        self.drop = False
        self.duplicate = False

    async def connect(self, url, **kwargs):
        broker = self

        class Connection:
            def __init__(self):
                self.callbacks = {}
                self.closed = False

            async def publish(self, subject, payload):
                broker.published.append((subject, json.loads(payload)))
                target = broker.routes.get(subject)
                if subject in broker.filtered_sources and json.loads(payload)["value"] < 0:
                    return
                if broker.drop:
                    return
                for connection in broker.connections:
                    if not connection.closed and target in connection.callbacks:
                        await connection.callbacks[target](SimpleNamespace(data=payload))
                        if broker.duplicate:
                            await connection.callbacks[target](SimpleNamespace(data=payload))

            async def subscribe(self, subject, cb):
                self.callbacks[subject] = cb

            async def flush(self, timeout):
                pass

            async def close(self):
                self.closed = True

        client = Connection()
        broker.connections.append(client)
        return client
