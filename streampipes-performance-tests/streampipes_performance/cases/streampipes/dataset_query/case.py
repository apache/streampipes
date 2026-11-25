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

"""Populate flowrate datasets, measure concurrent users."""

from datetime import datetime, timezone
from functools import partial

from pydantic import Field

from streampipes_performance.cases.base import Case, register
from streampipes_performance.cases.common import upload_rows, wait_for_rows, wait_for_storage_settled
from streampipes_performance.cases.queries import execute_asset_queries, query_requests
from streampipes_performance.clients.streampipes import StreamPipesClient
from streampipes_performance.config import CaseDocumentation, Model
from streampipes_performance.runtime import EvaluationError


class Parameters(Model):
    number_of_datasets: int = Field(default=2, ge=1, strict=True)
    rows_per_dataset: int = Field(default=10000, ge=1, strict=True)
    sensor_count: int = Field(default=10, ge=1, strict=True)
    parallel_operations: int = Field(default=1, ge=1, strict=True)


FIELDS = (
    "mass_flow", "density", "volume_flow", "temperature", "sensor_fault_flags", "sensorId", "timestamp",
)
HEADERS = ["timestamp", *[name for name in FIELDS if name != "timestamp"]]
START_MS = 1704067200000  # Fixed UTC origin for reproducible datasets.
INTERVAL_MS = 1000
BATCH_SIZE = 1000
ITERATIONS = 10

def time_range_options(parameters):
    return {
        "limit": None, "order": "ASC",
        "start_date": datetime.fromtimestamp(
            (START_MS + (parameters.rows_per_dataset // 2) * INTERVAL_MS) / 1000, timezone.utc,
        ),
        "end_date": datetime.fromtimestamp(
            (START_MS + parameters.rows_per_dataset * INTERVAL_MS - 1) / 1000, timezone.utc,
        ),
    }


# Add a named options factory here to run another query in every configuration.
# Factories receive the case parameters and return Data Lake get() keyword arguments.
# Explicit None suppresses the Python client's default 1000-row limit.
QUERIES = {
    "latest": lambda p: {"limit": None, "order": "DESC"},
    "projection": lambda p: {"limit": None, "order": "ASC", "columns": ["temperature", "mass_flow"]},
    "time_range": time_range_options,
    "filtered": lambda p: {"limit": None, "order": "ASC", "filter": "[sensorId;=;flowrate00]"},
    "aggregate": lambda p: {
        "limit": None, "order": "ASC", "columns": ["temperature", "mass_flow"],
        "aggregation_function": "MEAN",
    },
}


def dataset_payload(name):
    properties = []
    # Dataset metadata follows the CSV import API: timestampField describes
    # time separately. Including it here makes the backend counter select a
    # field that is never stored, so readiness keeps observing zero rows.
    for field in HEADERS[1:]:
        datatype = {"sensorId": "string", "sensor_fault_flags": "boolean"}.get(
            field, "float"
        )
        properties.append({
            "@class": "org.apache.streampipes.model.schema.EventPropertyPrimitive",
            "runtimeName": field,
            "runtimeType": f"http://www.w3.org/2001/XMLSchema#{datatype}",
            "propertyScope": "DIMENSION_PROPERTY" if field == "sensorId" else "MEASUREMENT_PROPERTY",
        })
    return {
        "@class": "org.apache.streampipes.model.datalake.DataLakeMeasure",
        "measureName": name,
        "timestampField": "s0::timestamp",
        "eventSchema": {"eventProperties": properties},
    }


def rows(start, end, sensor_count=10):
    """Reproducible flowrate values with the simulator's field names and types."""
    for index in range(start, end):
        fault = index % 100 >= 50
        event = {
            "timestamp": START_MS + index * INTERVAL_MS,
            "sensorId": f"flowrate{index % sensor_count:02d}",
            "mass_flow": (index % 100) / 10.0,
            "volume_flow": ((index * 3) % 100) / 10.0,
            "density": 40.0 + (index % 100) / 10.0,
            "temperature": (80.0 if fault else 40.0) + (index % 50) / 5.0,
            "sensor_fault_flags": fault,
        }
        yield [event[name] for name in HEADERS]


@register("streampipes.dataset_query")
class DatasetQuery(Case):
    services = ("influxdb_metrics",)
    documentation = CaseDocumentation(
        description="Populate deterministic machine datasets, then measure latest-row, projection, time-range, filtered and aggregate queries for every configuration.",
        parameter_descriptions={
            "number_of_datasets": "Number of datasets.",
            "rows_per_dataset": "Total rows in each dataset.",
            "sensor_count": "Number of sensorId values cycled through per dataset (default 10). Actual distinct values are min(sensor_count, rows_per_dataset).",
            "parallel_operations": "Maximum concurrent dataset workflows, each with its own HTTP session. All presets and repetitions of a dataset stay on one thread; active users are capped at the dataset count."
        },
        metrics={
            "operations.Query time": "Validated request duration across all named query presets (ms): count, min, mean, max and errors. Per-query summaries are stored in query_workloads."
        },
    )

    Parameters = Parameters

    def __init__(self, parameters, runtime_settings=None):
        super().__init__(parameters, runtime_settings=runtime_settings)
        self.workers = []
        self.results = []

    def query(self, client, name, query_name):
        options = QUERIES[query_name](self.parameters)
        result = client.dataLakeMeasureApi.get(name, **options)
        if result.query_status != "OK":
            raise EvaluationError("dataset_query_status_not_ok")
        count = sum(len(series.rows) for series in result.all_data_series)
        if count == 0:
            raise EvaluationError("dataset_query_empty_result")
        return count

    def setup(self, context):
        p = self.parameters
        # Register creation intent, upload deterministic rows, and verify persistence.
        for index in range(p.number_of_datasets):
            name = f"{context.prefix}-dataset-{index}"
            context.datasets.append(name)
            metadata = context.call("dataset.create", context.client.datasetApi.create, dataset_payload(name))
            identifier = context.track("dataset", metadata["elementId"], name)
            upload_rows(context, name, HEADERS, p.rows_per_dataset, BATCH_SIZE,
                        partial(rows, sensor_count=p.sensor_count), tag_fields=("sensorId",))
            wait_for_rows(context, identifier, name, p.rows_per_dataset)
        # Uploaded rows may still sit in the InfluxDB cache; optionally wait for compacted storage first.
        wait_for_storage_settled(context)
        # Each concurrent user owns a session, tracked immediately for partial setup cleanup.
        if min(p.parallel_operations, p.number_of_datasets) > 1:
            for _ in range(min(p.parallel_operations, p.number_of_datasets)):
                self.workers.append(StreamPipesClient(context.client.client_config))

    def run(self, context):
        clients = self.workers or [context.client]
        self.results = execute_asset_queries(
            context, clients, context.datasets, QUERIES, ITERATIONS,
            lambda client, asset, name, job: self.query(client, asset, name),
        )
        if any(result["errors"] for result in self.results):
            raise EvaluationError("dataset_queries_failed")

    def prepare_results(self, context):
        return {
            "query_workloads": self.results,
            "uploaded_rows_per_dataset": self.parameters.rows_per_dataset,
            "query_requests": query_requests(
                [result["query"] for result in self.results], lambda client, name: self.query(client, context.datasets[0], name),
            ) if self.results and context.datasets else [],
        }

    def cleanup(self, context):
        try:
            return context.cleanup()
        finally:
            for client in self.workers:
                client.request_session.close()
