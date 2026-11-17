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

"""Adapter ingestion benchmark: parameters, presets, and execution."""

from pydantic import Field
from streampipes.model.compact import CompactAdapter, CompactPipeline

from streampipes_performance.cases.base import Case, register
from streampipes_performance.cases.common import create_adapter, create_pipeline
from streampipes_performance.cases.payloads import transformation_config
from streampipes_performance.cases.scripts import Script
from streampipes_performance.config import CaseDocumentation, Model
from streampipes_performance.measurement import collect_window


class Parameters(Model):
    number_of_adapters: int = Field(default=1, ge=1, strict=True)
    wait_time_ms: int = Field(default=1000, ge=1, strict=True)
    transformation: Script | None = None


SIMULATOR = "org.apache.streampipes.connect.iiot.adapters.simulator.machine"
FIELDS = (
    "mass_flow", "density", "volume_flow", "temperature", "sensor_fault_flags", "sensorId", "timestamp",
)
SINK = "org.apache.streampipes.sinks.internal.jvm.dataset"
PAGE_SIZE = 1000
MAX_PAGES = 1000


def adapter_payload(identifier, name, interval, transformation=None):
    schema = {name: {"propertyScope": "MEASUREMENT_PROPERTY"} for name in FIELDS}
    schema["timestamp"] = {
        "propertyScope": "HEADER_PROPERTY",
        "semanticType": "http://schema.org/DateTime",
        "additionalMetadata": {"originType": "http://www.w3.org/2001/XMLSchema#long"},
    }
    return CompactAdapter.model_validate(
        dict(
            id=identifier,
            name=name,
            appId=SIMULATOR,
            configuration=[
                {"wait-time-ms": str(interval)},
                {"numberOfSensors": "1"},
                {"selected-simulator-option": "flowrate"},
            ],
            transformationConfig=transformation_config(transformation),
            schema=schema,
            createOptions={"persist": False, "start": True},
        )
    )


def pipeline_payload(identifier, name, stream):
    return CompactPipeline.model_validate(
        dict(
            id=identifier,
            name=name,
            pipelineElements=[
                {"type": "stream", "id": stream, "ref": "s0"},
                {
                    "type": "sink",
                    "id": SINK,
                    "ref": "sink",
                    "connectedTo": ["s0"],
                    "configuration": [
                        {"schema_update": "Update schema"},
                        {"ignore_duplicates": False},
                        {"db_measurement": name},
                        {"timestamp_mapping": "s0::timestamp"},
                        {"dimensions_selection": []},
                    ],
                },
            ],
            createOptions={"start": True},
        )
    )


@register("streampipes.adapter_ingestion")
class AdapterIngestion(Case):
    services = ("extensions", "nats")
    documentation = CaseDocumentation(
        description="Create machine simulator adapters and storage pipelines; measure persisted event spacing and event ratio.",
        parameter_descriptions={
            "number_of_adapters": "Number of source adapters; each has one dataset and storage pipeline.",
            "wait_time_ms": "Milliseconds between simulator events. Events/second per adapter = 1000 / wait_time_ms.",
            "transformation": "Object with label and script (JavaScript source). Omitted or null disables scripting. Each input must produce one output preserving timestamp and, for latency, eventId."
        },
        metrics={
            "datasets[].event_gaps_ms": "Distribution of timestamp gaps between consecutive persisted events in each dataset (ms). Ideally wait_time_ms. This is event spacing, not end-to-end processing latency.",
            "throughput_ratio": "Persisted events / expected simulator events in the fixed measurement window. Target: 1.0.",
            "persisted_events_per_second": "Total persisted events divided by the measurement window duration."
        },
    )

    Parameters = Parameters

    def __init__(self, parameters, runtime_settings=None):
        super().__init__(parameters, runtime_settings=runtime_settings)
        self.adapters = []
        self.start_ms = None
        self.end_ms = None

    def setup(self, context):
        # Create running sources and wait for their streams.
        for index in range(self.parameters.number_of_adapters):
            name = f"{context.prefix}-a{index}"
            identifier = f"sp:adapterdescription:{name}"
            payload = adapter_payload(
                identifier, name, self.parameters.wait_time_ms, self.parameters.transformation,
            )
            self.adapters.append(create_adapter(context, identifier, name, payload))
        # Attach one storage pipeline and dataset to each source.
        for adapter in self.adapters:
            identifier = f"{adapter.name}-storage"
            create_pipeline(
                context, identifier, adapter.name, pipeline_payload(identifier, adapter.name, adapter.stream),
            )

    def run(self, context):
        context.log("Warming up for %.1f s", context.timing.warmup_seconds)
        context.clock.sleep(context.timing.warmup_seconds)
        self.start_ms = int(context.clock.time() * 1000)
        context.log("Measuring for %.1f s", context.timing.duration_seconds)
        context.clock.sleep(context.timing.duration_seconds)
        self.end_ms = self.start_ms + round(context.timing.duration_seconds * 1000)

    def prepare_results(self, context):
        if self.end_ms is None:
            return {"collection_complete": False}
        # Stop sources; storage pipelines remain running to drain their inputs.
        for adapter in self.adapters:
            context.lifecycle("adapter", adapter.identifier, False)
        return collect_window(
            context, [adapter.name for adapter in self.adapters], self.start_ms, self.end_ms,
            wait_time_ms=self.parameters.wait_time_ms, page_size=PAGE_SIZE, max_pages=MAX_PAGES,
        )
