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

"""Measure NATS -> adapter -> NATS latency."""

import asyncio
import json
import time

from pydantic import Field
from streampipes.model.compact import CompactAdapter

from streampipes_performance.cases.base import Case, register
from streampipes_performance.cases.common import create_adapter
from streampipes_performance.cases.latency import (
    BrokerSettings,
    Collector,
    LatencyMeasurement,
    connections,
    output_subject,
    validate_broker_url,
)
from streampipes_performance.cases.payloads import transformation_config
from streampipes_performance.cases.scripts import Script
from streampipes_performance.config import CaseDocumentation, Model
from streampipes_performance.runtime import EvaluationError
from streampipes_performance.settings import resolve_settings


class Parameters(Model):
    number_of_adapters: int = Field(default=1, ge=1, strict=True)
    events_per_resource: int = Field(default=1000, ge=1, strict=True)
    publish_interval_ms: float = Field(default=10, gt=0)
    transformation: Script | None = None


WARMUP_EVENTS = 10


def event_bytes(identifier, index=None):
    return json.dumps({"eventId": identifier, "timestamp": time.time_ns() // 1_000_000, "value": 42.5}).encode()


def nats_adapter(identifier, name, subject, parameters, broker_url):
    return CompactAdapter.model_validate({
        "id": identifier, "name": name,
        "transformationConfig": transformation_config(parameters.transformation),
        "appId": "org.apache.streampipes.connect.iiot.protocol.stream.nats",
        "configuration": [
            {"natsUrls": broker_url}, {"subject": subject},
            {"access-mode": "anonymous-alternative"},
            {"connection-properties": "none-properties-alternative"},
            {"format": "Json", "json_options": "object"},
        ],
        "schema": {
            "eventId": {"propertyScope": "DIMENSION_PROPERTY"},
            "timestamp": {"propertyScope": "HEADER_PROPERTY", "semanticType": "http://schema.org/DateTime",
                          "additionalMetadata": {"originType": "http://www.w3.org/2001/XMLSchema#long"}},
            "value": {"propertyScope": "MEASUREMENT_PROPERTY"},
        },
        "createOptions": {"persist": False, "start": True},
    })


@register("streampipes.adapter_latency")
class AdapterLatency(Case):
    services = ("extensions", "nats")
    documentation = CaseDocumentation(
        description="Measure correlated NATS → adapter → NATS latency for each configured transformation.",
        parameter_descriptions={
            "number_of_adapters": "Number of adapters.",
            "events_per_resource": "Number of measured input events per adapter; setup probes and ten warmup events are excluded.",
            "publish_interval_ms": "Interval between publishing rounds in milliseconds. Events/second per adapter = 1000 / publish_interval_ms.",
            "transformation": "Object with label and script (JavaScript source). Omitted or null disables scripting. Each input must produce one output preserving timestamp and, for latency, eventId."
        },
        metrics={
            "processing_latency_ms": "NATS input to correlated output elapsed time on the benchmark clock: count, min, mean and max (ms).",
            "missing_events": "Expected outputs that were not received; a nonzero count fails the case.",
            "throughput_ratio": "Observed / expected outputs. Target: 1.0."
        },
    )

    Parameters = Parameters

    def __init__(self, parameters, runtime_settings=None):
        super().__init__(parameters, runtime_settings=runtime_settings)
        settings = runtime_settings or resolve_settings()
        self.latency = LatencyMeasurement(
            self.parameters,
            BrokerSettings(nats_url=settings.nats_url, output_nats_url=settings.output_nats_url),
            resource_count=self.parameters.number_of_adapters,
            event_factory=event_bytes,
            expects_output=lambda index: True,
            warmup_events=WARMUP_EVENTS,
        )
        self.adapter_nats_url = validate_broker_url(settings.adapter_nats_url)

    def setup(self, context):
        self.latency.sources = [f"{context.prefix}.input.{i}" for i in range(self.parameters.number_of_adapters)]
        asyncio.run(self.setup_async(context))

    async def setup_async(self, context):
        timeout = context.timing.request_timeout_seconds
        async with connections(self.latency.brokers, timeout) as (publisher, subscriber, errors):
            # Keep broker I/O alive while REST creates resources and schema samples are sent.
            await self.latency.create_resources(context, self.create_resources, publisher, True)
            await self.latency.subscribe_outputs(subscriber, timeout)
            await self.latency.probe_outputs(context, publisher)
            await self.latency.warmup(context, publisher)
            if errors:
                raise EvaluationError("latency_nats_setup_error")
        self.latency.collector = Collector()

    def create_resources(self, context, sources):
        outputs = []
        for index, subject in enumerate(sources):
            name = f"{context.prefix}-a{index}"
            identifier = f"sp:adapterdescription:{name}"
            payload = nats_adapter(identifier, name, subject, self.parameters, self.adapter_nats_url)
            source = create_adapter(
                context, identifier, name, payload, missing_stream_error="latency_adapter_missing_stream",
            )
            outputs.append(output_subject(context, source.stream))
        return outputs

    def run(self, context):
        asyncio.run(self.run_async(context))

    async def run_async(self, context):
        timeout = context.timing.request_timeout_seconds
        async with connections(self.latency.brokers, timeout) as (publisher, subscriber, errors):
            await self.latency.subscribe_outputs(subscriber, timeout)
            await self.latency.publish_events(context, publisher, timeout)
            await self.latency.wait_for_outputs(context)
            if errors:
                raise EvaluationError("latency_nats_measurement_error")
            self.latency.validate_outputs()

    def prepare_results(self, context):
        return self.latency.prepare_results(context)
