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

"""Adapter start stop benchmark: parameters and execution."""

from pydantic import Field
from streampipes.model.compact import CompactAdapter

from streampipes_performance.cases.base import Case, register
from streampipes_performance.cases.common import create_adapter
from streampipes_performance.cases.payloads import transformation_config
from streampipes_performance.config import CaseDocumentation, Model


class Parameters(Model):
    number_of_adapters: int = Field(default=1, ge=1, strict=True)
    wait_time_ms: int = Field(default=1000, ge=1, strict=True)
    cycles: int = Field(default=3, ge=1, strict=True)


SIMULATOR = "org.apache.streampipes.connect.iiot.adapters.simulator.machine"
FIELDS = (
    "mass_flow", "density", "volume_flow", "temperature", "sensor_fault_flags", "sensorId", "timestamp",
)
INTERVAL_SECONDS = 1
FORCE_STOP = False


def adapter_payload(identifier, name, interval):
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
            transformationConfig=transformation_config(None),
            schema=schema,
            createOptions={"persist": False, "start": True},
        )
    )


@register("streampipes.adapter_start_stop")
class AdapterLifecycle(Case):
    services = ("extensions", "nats")
    documentation = CaseDocumentation(
        description="Create running simulator adapters, then repeatedly stop and start all adapters.",
        parameter_descriptions={
            "number_of_adapters": "Number of adapters.",
            "wait_time_ms": "Simulator event interval in ms; events/second = 1000 / wait_time_ms.",
            "cycles": "Number of complete stop/start cycles. Each cycle stops all adapters, waits one second, starts all adapters, and waits one second.",
        },
        metrics={
            "operations.Adapter start time": "Start API request duration, including client processing, in ms.",
            "operations.Adapter stop time": "Stop API request duration, including client processing, in ms.",
            "Adapter start time (measurement readiness)": "Start request through observed running state, in ms.",
            "Adapter stop time (measurement readiness)": "Stop request through observed stopped state, in ms."
        },
    )

    Parameters = Parameters

    def __init__(self, parameters, runtime_settings=None):
        super().__init__(parameters, runtime_settings=runtime_settings)
        self.adapters = []

    def setup(self, context):
        # Create running sources and wait for their streams.
        for index in range(self.parameters.number_of_adapters):
            name = f"{context.prefix}-a{index}"
            identifier = f"sp:adapterdescription:{name}"
            payload = adapter_payload(
                identifier, name, self.parameters.wait_time_ms,
            )
            self.adapters.append(create_adapter(context, identifier, name, payload))

    def run(self, context):
        identifiers = [adapter.identifier for adapter in self.adapters]
        for cycle in range(1, self.parameters.cycles + 1):
            context.log("adapter start/stop cycle %d/%d", cycle, self.parameters.cycles)
            for running in (False, True):
                for identifier in identifiers:
                    context.lifecycle("adapter", identifier, running, FORCE_STOP)
                context.clock.sleep(INTERVAL_SECONDS)
