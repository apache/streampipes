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

import json
from copy import deepcopy
from unittest import TestCase
from unittest.mock import MagicMock, patch

from pydantic import ValidationError
from requests import HTTPError
from streampipes_client.client import StreamPipesClient
from streampipes_client.client.client_config import StreamPipesClientConfig
from streampipes_client.client.credential_provider import StreamPipesApiKeyCredentials
from streampipes_client.endpoint.endpoint import _error_code_to_message
from streampipes_client.model.container.resource_container import (
    StreamPipesDataModelError,
    StreamPipesResourceContainerJSONError,
)


class TestStreamPipesEndpoints(TestCase):
    def setUp(self) -> None:
        # set example responses from endpoints
        self.data_lake_measure_all = [
            {
                "elementId": "urn:streampipes.apache.org:spi:datalakemeasure:xLSfXZ",
                "measureName": "test",
                "timestampField": "s0::timestamp",
                "eventSchema": {
                    "elementId": "urn:streampipes.apache.org:spi:eventschema:UDMHXn",
                    "eventProperties": [
                        {
                            "elementId": "urn:streampipes.apache.org:spi:eventpropertyprimitive:utvSWg",
                            "label": "Density",
                            "description": "Denotes the current density of the fluid",
                            "runtimeName": "density",
                            "required": False,
                            "domainProperties": ["http://schema.org/Number"],
                            "eventPropertyQualities": [],
                            "requiresEventPropertyQualities": [],
                            "propertyScope": "MEASUREMENT_PROPERTY",
                            "index": 5,
                            "runtimeId": None,
                            "runtimeType": "http://www.w3.org/2001/XMLSchema#float",
                            "measurementUnit": None,
                            "valueSpecification": None,
                        },
                        {
                            "elementId": "urn:streampipes.apache.org:spi:eventpropertyprimitive:OgBuiz",
                            "label": "Temperature",
                            "description": "Denotes the current temperature in degrees celsius",
                            "runtimeName": "temperature",
                            "required": False,
                            "domainProperties": ["http://schema.org/Number"],
                            "eventPropertyQualities": [],
                            "requiresEventPropertyQualities": [],
                            "propertyScope": "MEASUREMENT_PROPERTY",
                            "index": 4,
                            "runtimeId": None,
                            "runtimeType": "http://www.w3.org/2001/XMLSchema#float",
                            "measurementUnit": "http://codes.wmo.int/common/unit/degC",
                            "valueSpecification": {
                                "elementId": "urn:streampipes.apache.org:spi:quantitativevalue:sotOEB",
                                "minValue": 0,
                                "maxValue": 100,
                                "step": 0.1,
                            },
                        },
                    ],
                },
                "pipelineId": None,
                "pipelineName": None,
                "pipelineIsRunning": False,
                "schemaVersion": "1.1",
            }
        ]

        self.data_stream_all = [
            {
                "elementId": "urn:streampipes.apache.org:eventstream:uPDKLI",
                "name": "Test",
                "description": "",
                "iconUrl": None,
                "appId": None,
                "includesAssets": False,
                "includesLocales": False,
                "includedAssets": [],
                "includedLocales": [],
                "applicationLinks": [],
                "internallyManaged": True,
                "connectedTo": None,
                "eventGrounding": {
                    "elementId": "urn:streampipes.apache.org:spi:eventgrounding:TwGIQA",
                    "transportProtocols": [
                        {
                            "elementId": "urn:streampipes.apache.org:spi:natstransportprotocol:VJkHmZ",
                            "brokerHostname": "nats",
                            "topicDefinition": {
                                "elementId": "urn:streampipes.apache.org:spi:simpletopicdefinition:QzCiFI",
                                "actualTopicName": "org.apache.streampipes.connect.fc22b8f6-698a-4127-aa71-e11854dc57c5",
                            },
                            "port": 4222,
                        }
                    ],
                    "transportFormats": [
                        {
                            "elementId": "urn:streampipes.apache.org:spi:transportformat:CMGsLP",
                            "rdfType": ["http://sepa.event-processing.org/sepa#json"],
                        }
                    ],
                },
                "eventSchema": {
                    "elementId": "urn:streampipes.apache.org:spi:eventschema:rARlLX",
                    "eventProperties": [
                        {
                            "elementId": "urn:streampipes.apache.org:spi:eventpropertyprimitive:yogPNV",
                            "label": "Density",
                            "description": "Denotes the current density of the fluid",
                            "runtimeName": "density",
                            "required": False,
                            "domainProperties": ["http://schema.org/Number"],
                            "eventPropertyQualities": [],
                            "requiresEventPropertyQualities": [],
                            "propertyScope": "MEASUREMENT_PROPERTY",
                            "index": 5,
                            "runtimeId": None,
                            "runtimeType": "http://www.w3.org/2001/XMLSchema#float",
                            "measurementUnit": None,
                            "valueSpecification": None,
                        },
                        {
                            "elementId": "urn:streampipes.apache.org:spi:eventpropertyprimitive:GjZgFg",
                            "label": "Temperature",
                            "description": "Denotes the current temperature in degrees celsius",
                            "runtimeName": "temperature",
                            "required": False,
                            "domainProperties": ["http://schema.org/Number"],
                            "eventPropertyQualities": [],
                            "requiresEventPropertyQualities": [],
                            "propertyScope": "MEASUREMENT_PROPERTY",
                            "index": 4,
                            "runtimeId": None,
                            "runtimeType": "http://www.w3.org/2001/XMLSchema#float",
                            "measurementUnit": "http://codes.wmo.int/common/unit/degC",
                            "valueSpecification": {
                                "elementId": "urn:streampipes.apache.org:spi:quantitativevalue:ZQSJfk",
                                "minValue": 0,
                                "maxValue": 100,
                                "step": 0.1,
                            },
                        },
                    ],
                },
                "measurementCapability": None,
                "measurementObject": None,
                "index": 0,
                "correspondingAdapterId": "urn:streampipes.apache.org:spi:org.apache.streampipes.connect.iiot.adapters.simulator.machine:11934d37-135b-4ef6-b5f1-4f520cc81a43",
                "category": None,
                "uri": "urn:streampipes.apache.org:eventstream:uPDKLI",
                "dom": None,
            }
        ]

        self.data_stream_all_json = json.dumps(self.data_stream_all)

        self.data_lake_measure_all_json = json.dumps(self.data_lake_measure_all)
        self.data_lake_measure_all_json_error = json.dumps(self.data_lake_measure_all[0])
        self.dlm_all_manipulated = deepcopy(self.data_lake_measure_all)
        self.dlm_all_manipulated[0]["measureName"] = False
        self.data_lake_measure_all_json_validation = json.dumps(self.dlm_all_manipulated)

    @patch("streampipes_client.client.client.Session", autospec=True)
    def test_endpoint_data_stream_happy_path(self, http_session: MagicMock):
        http_session_mock = MagicMock()
        http_session_mock.get.return_value.text = self.data_stream_all_json
        http_session.return_value = http_session_mock

        client = StreamPipesClient(
            client_config=StreamPipesClientConfig(
                credential_provider=StreamPipesApiKeyCredentials(username="user", api_key="key"),
                host_address="localhost",
            )
        )

        result = client.dataStreamApi.all()
        result_pd = result.to_pandas()

        self.assertEqual(
            1,
            len(result),
        )
        self.assertEqual(
            "Test",
            result[0].name,
        )
        self.assertEqual(
            self.data_stream_all_json,
            result.to_json(),
        )
        self.assertEqual(
            self.data_stream_all,
            result.to_dicts(use_source_names=True),
        )
        self.assertEqual(
            1,
            len(result_pd),
        )
        self.assertListEqual(
            [
                "element_id",
                "name",
                "description",
                "icon_url",
                "app_id",
                "includes_assets",
                "includes_locales",
                "included_assets",
                "included_locales",
                "application_links",
                "internally_managed",
                "connected_to",
                "event_grounding",
                "event_schema",
                "measurement_capability",
                "measurement_object",
                "index",
                "corresponding_adapter_id",
                "category",
                "uri",
                "dom",
            ],
            list(result_pd.columns),
        )

    @patch("streampipes_client.client.client.Session", autospec=True)
    def test_endpoint_data_lake_measure_happy_path(self, http_session: MagicMock):
        http_session_mock = MagicMock()
        http_session_mock.get.return_value.text = self.data_lake_measure_all_json
        http_session.return_value = http_session_mock

        client = StreamPipesClient(
            client_config=StreamPipesClientConfig(
                credential_provider=StreamPipesApiKeyCredentials(username="user", api_key="key"),
                host_address="localhost",
            )
        )

        result = client.dataLakeMeasureApi.all()
        result_pd = result.to_pandas()

        self.assertEqual(
            1,
            len(result),
        )
        self.assertEqual(
            "test",
            result[0].measure_name,
        )
        self.assertEqual(
            self.data_lake_measure_all_json,
            result.to_json(),
        )
        self.assertEqual(
            self.data_lake_measure_all,
            result.to_dicts(use_source_names=True),
        )
        self.assertEqual(
            1,
            len(result_pd),
        )
        self.assertListEqual(
            [
                "measure_name",
                "timestamp_field",
                "pipeline_id",
                "pipeline_name",
                "pipeline_is_running",
                "num_event_properties",
            ],
            list(result_pd.columns),
        )
        self.assertEqual(2, result_pd["num_event_properties"][0])

    @patch("streampipes_client.client.client.Session", autospec=True)
    def test_endpoint_data_lake_measure_bad_return_code(self, http_session: MagicMock):
        response_mock = MagicMock()
        response_mock.status_code = 405
        response_mock.text = "Test error"
        response_mock.url = "localhost"

        http_session_mock = MagicMock()
        http_session_mock.get.return_value.status_code = 405
        http_session_mock.get.return_value.raise_for_status.side_effect = HTTPError(response=response_mock)
        http_session.return_value = http_session_mock

        client = StreamPipesClient(
            client_config=StreamPipesClientConfig(
                credential_provider=StreamPipesApiKeyCredentials(username="user", api_key="key"),
                host_address="localhost",
            )
        )

        with self.assertRaises(HTTPError) as http_error:
            client.dataLakeMeasureApi.all()
        self.assertMultiLineEqual(
            _error_code_to_message[405] + "url: localhost\nstatus code: 405",
            http_error.exception.args[0],
        )

    @patch("streampipes_client.client.client.Session", autospec=True)
    def test_endpoint_data_lake_measure_json_error(self, http_session: MagicMock):
        http_session_mock = MagicMock()
        http_session_mock.get.return_value.text = self.data_lake_measure_all_json_error
        http_session.return_value = http_session_mock

        client = StreamPipesClient(
            client_config=StreamPipesClientConfig(
                credential_provider=StreamPipesApiKeyCredentials(username="user", api_key="key"),
                host_address="localhost",
            )
        )

        with self.assertRaises(StreamPipesResourceContainerJSONError):
            client.dataLakeMeasureApi.all()

    @patch("streampipes_client.client.client.Session", autospec=True)
    def test_endpoint_data_lake_measure_validation_error(self, http_session: MagicMock):
        http_session_mock = MagicMock()
        http_session_mock.get.return_value.text = self.data_lake_measure_all_json_validation
        http_session.return_value = http_session_mock

        client = StreamPipesClient(
            client_config=StreamPipesClientConfig(
                credential_provider=StreamPipesApiKeyCredentials(username="user", api_key="key"),
                host_address="localhost",
            )
        )

        with self.assertRaises(StreamPipesDataModelError) as err:
            client.dataLakeMeasureApi.all()

        self.assertTrue(isinstance(err.exception.validation_error, ValidationError))
