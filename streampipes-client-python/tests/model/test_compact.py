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

from unittest import TestCase

import yaml  # type: ignore[import-untyped]
from pydantic import ValidationError

from streampipes.model.compact import (
    CompactAdapter,
    CompactEventProperty,
    CompactPipeline,
    CompactPipelineElement,
    CreateOptions,
    OutputConfiguration,
    UserDefinedOutput,
)


class TestCompactModels(TestCase):
    adapter_json = """{
        "id": "adapter-1",
        "name": "Copied adapter",
        "appId": "org.example.adapter",
        "configuration": [
            {"wait-time-ms": "1000"},
            {"nested": [{"value": null}]}
        ],
        "transformationConfig": {"scriptActive": true},
        "schema": {
            "temperature": {
                "label": "Temperature",
                "propertyScope": "MEASUREMENT_PROPERTY"
            }
        },
        "createOptions": {"persist": true, "start": false}
    }"""

    adapter_yaml = """
id: adapter-1
name: Copied adapter
appId: org.example.adapter
configuration:
  - wait-time-ms: "1000"
  - nested:
      - value: null
transformationConfig:
  scriptActive: true
schema:
  temperature:
    label: Temperature
    propertyScope: MEASUREMENT_PROPERTY
createOptions:
  persist: true
  start: false
"""

    pipeline_json = """{
        "id": "pipeline-1",
        "name": "Copied pipeline",
        "pipelineElements": [
            {
                "type": "stream",
                "id": "stream-1",
                "ref": "source"
            },
            {
                "type": "sink",
                "id": "org.example.sink",
                "ref": "sink",
                "connectedTo": ["source"],
                "configuration": [{"subject": "events"}]
            }
        ],
        "createOptions": {"persist": false, "start": true}
    }"""

    pipeline_yaml = """
id: pipeline-1
name: Copied pipeline
pipelineElements:
  - type: stream
    id: stream-1
    ref: source
  - type: sink
    id: org.example.sink
    ref: sink
    connectedTo:
      - source
    configuration:
      - subject: events
createOptions:
  persist: false
  start: true
"""

    def test_adapter_configuration_and_aliases(self):
        adapter = CompactAdapter(
            name="Adapter",
            app_id="org.example.adapter",
            configuration=[
                {"wait-time-ms": "1000"},
                {"nested": [{"value": None}]},
            ],
            transformation_config={
                "scriptActive": True,
                "script": "return event;",
            },
            event_schema={
                "temperature": CompactEventProperty(
                    label="Temperature",
                    property_scope="MEASUREMENT_PROPERTY",
                    additional_metadata={"unit": "C"},
                )
            },
        )

        payload = adapter.to_dict()

        self.assertEqual(payload["appId"], "org.example.adapter")
        self.assertListEqual(payload["configuration"], adapter.configuration)
        self.assertDictEqual(
            payload["transformationConfig"],
            adapter.transformation_config,
        )
        self.assertDictEqual(
            payload["createOptions"],
            {"persist": False, "start": False},
        )
        self.assertEqual(
            payload["schema"]["temperature"]["propertyScope"],
            "MEASUREMENT_PROPERTY",
        )

    def test_pipeline_output_and_aliases(self):
        pipeline = CompactPipeline(
            name="Pipeline",
            pipeline_elements=[
                CompactPipelineElement(
                    type="processor",
                    id="org.example.processor",
                    connected_to=["stream-1"],
                    configuration=[{"threshold": 10}],
                    output=OutputConfiguration(
                        keep=["s0::value"],
                        user_defined=[
                            UserDefinedOutput(
                                field_name="result",
                                runtime_type=("http://www.w3.org/2001/XMLSchema#double"),
                            )
                        ],
                    ),
                )
            ],
        )

        payload = pipeline.to_dict()

        self.assertEqual(
            payload["pipelineElements"][0]["connectedTo"],
            ["stream-1"],
        )
        self.assertEqual(
            payload["pipelineElements"][0]["output"]["userDefined"][0]["fieldName"],
            "result",
        )
        self.assertDictEqual(
            payload["createOptions"],
            {"persist": False, "start": False},
        )

    def test_manual_clone_does_not_mutate_original(self):
        adapter = CompactAdapter(
            id="adapter-1",
            name="Adapter",
            app_id="org.example.adapter",
            create_options=CreateOptions(persist=True, start=True),
        )

        clone = adapter.model_copy(
            deep=True,
            update={
                "id": None,
                "name": "Adapter copy",
                "create_options": CreateOptions(),
            },
        )

        self.assertEqual(adapter.id, "adapter-1")
        self.assertTrue(adapter.create_options.start)
        self.assertIsNone(clone.id)
        self.assertEqual(clone.name, "Adapter copy")
        self.assertFalse(clone.create_options.persist)
        self.assertFalse(clone.create_options.start)

    def test_adapter_can_be_created_from_copied_json_or_yaml(self):
        for parser, serialized in (
            (CompactAdapter.from_json, self.adapter_json),
            (CompactAdapter.from_yaml, self.adapter_yaml),
        ):
            with self.subTest(parser=parser.__name__):
                adapter = parser(serialized)

                self.assertIsInstance(adapter, CompactAdapter)
                self.assertEqual(adapter.app_id, "org.example.adapter")
                self.assertIsNone(adapter.configuration[1]["nested"][0]["value"])
                self.assertEqual(
                    adapter.event_schema["temperature"].label,
                    "Temperature",
                )
                self.assertTrue(adapter.create_options.persist)
                self.assertFalse(adapter.create_options.start)

    def test_pipeline_can_be_created_from_copied_json_or_yaml(self):
        for parser, serialized in (
            (CompactPipeline.from_json, self.pipeline_json),
            (CompactPipeline.from_yaml, self.pipeline_yaml),
        ):
            with self.subTest(parser=parser.__name__):
                pipeline = parser(serialized)

                self.assertIsInstance(pipeline, CompactPipeline)
                self.assertEqual(
                    pipeline.pipeline_elements[1].connected_to,
                    ["source"],
                )
                self.assertEqual(
                    pipeline.pipeline_elements[1].configuration,
                    [{"subject": "events"}],
                )
                self.assertFalse(pipeline.create_options.persist)
                self.assertTrue(pipeline.create_options.start)

    def test_invalid_serialized_compact_models_are_rejected(self):
        with self.subTest(format="json"):
            with self.assertRaises(ValidationError):
                CompactAdapter.from_json('{"name": "Missing app ID"}')

        with self.subTest(format="yaml"):
            with self.assertRaises(yaml.YAMLError):
                CompactPipeline.from_yaml("pipelineElements: [")
