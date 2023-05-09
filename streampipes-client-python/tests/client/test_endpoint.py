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
from typing import Dict, List
from unittest import TestCase
from unittest.mock import MagicMock, call, patch

from pydantic import ValidationError
from requests import HTTPError
from streampipes.client import StreamPipesClient
from streampipes.client.config import StreamPipesClientConfig
from streampipes.client.credential_provider import StreamPipesApiKeyCredentials
from streampipes.endpoint.endpoint import MessagingEndpoint, _error_code_to_message
from streampipes.endpoint.exceptions import MessagingEndpointNotConfiguredError
from streampipes.functions.broker import NatsConsumer
from streampipes.model.container.resource_container import (
    StreamPipesDataModelError,
    StreamPipesResourceContainerJSONError,
)
from streampipes.model.resource import DataStream


class TestStreamPipesEndpoints(TestCase):
    def setUp(self) -> None:
        # set example responses from endpoints
        self.data_lake_measure_all = [
            {
                "elementId": "urn:streampipes.apache.org:spi:datalakemeasure:xLSfXZ",
                "measureName": "test",
                "timestampField": "s0::timestamp",
                "eventSchema": {
                    "eventProperties": [
                        {
                            "@class": "org.apache.streampipes.model.schema.EventPropertyPrimitive",
                            "elementId": "urn:streampipes.apache.org:spi:eventpropertyprimitive:utvSWg",
                            "label": "Density",
                            "description": "Denotes the current density of the fluid",
                            "runtimeName": "density",
                            "required": False,
                            "domainProperties": ["http://schema.org/Number"],
                            "propertyScope": "MEASUREMENT_PROPERTY",
                            "index": 5,
                            "runtimeId": None,
                            "runtimeType": "http://www.w3.org/2001/XMLSchema#float",
                            "measurementUnit": None,
                            "valueSpecification": None,
                        },
                        {
                            "@class": "org.apache.streampipes.model.schema.EventPropertyPrimitive",
                            "elementId": "urn:streampipes.apache.org:spi:eventpropertyprimitive:OgBuiz",
                            "label": "Temperature",
                            "description": "Denotes the current temperature in degrees celsius",
                            "runtimeName": "temperature",
                            "required": False,
                            "domainProperties": ["http://schema.org/Number"],
                            "propertyScope": "MEASUREMENT_PROPERTY",
                            "index": 4,
                            "runtimeId": None,
                            "runtimeType": "http://www.w3.org/2001/XMLSchema#float",
                            "measurementUnit": "http://codes.wmo.int/common/unit/degC",
                            "valueSpecification": {
                                "@class": "org.apache.streampipes.model.schema.QuantitativeValue",
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

        self.data_stream_all: List[Dict] = [
            {
                "@class": "org.apache.streampipes.model.SpDataStream",
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
                    "transportProtocols": [
                        {
                            "@class": "org.apache.streampipes.model.grounding.NatsTransportProtocol",
                            "elementId": "urn:streampipes.apache.org:spi:natstransportprotocol:VJkHmZ",
                            "brokerHostname": "nats",
                            "topicDefinition": {
                                "@class": "org.apache.streampipes.model.grounding.SimpleTopicDefinition",
                                "actualTopicName": "org.apache.streampipes.connect."
                                                   "fc22b8f6-698a-4127-aa71-e11854dc57c5",
                            },
                            "port": 4222,
                        }
                    ],
                    "transportFormats": [
                        {
                            "rdfType": ["http://sepa.event-processing.org/sepa#json"],
                        }
                    ],
                },
                "eventSchema": {
                    "eventProperties": [
                        {
                            "@class": "org.apache.streampipes.model.schema.EventPropertyPrimitive",
                            "elementId": "urn:streampipes.apache.org:spi:eventpropertyprimitive:yogPNV",
                            "label": "Density",
                            "description": "Denotes the current density of the fluid",
                            "runtimeName": "density",
                            "required": False,
                            "domainProperties": ["http://schema.org/Number"],
                            "propertyScope": "MEASUREMENT_PROPERTY",
                            "index": 5,
                            "runtimeId": None,
                            "runtimeType": "http://www.w3.org/2001/XMLSchema#float",
                            "measurementUnit": None,
                            "valueSpecification": None,
                        },
                        {
                            "@class": "org.apache.streampipes.model.schema.EventPropertyPrimitive",
                            "elementId": "urn:streampipes.apache.org:spi:eventpropertyprimitive:GjZgFg",
                            "label": "Temperature",
                            "description": "Denotes the current temperature in degrees celsius",
                            "runtimeName": "temperature",
                            "required": False,
                            "domainProperties": ["http://schema.org/Number"],
                            "propertyScope": "MEASUREMENT_PROPERTY",
                            "index": 4,
                            "runtimeId": None,
                            "runtimeType": "http://www.w3.org/2001/XMLSchema#float",
                            "measurementUnit": "http://codes.wmo.int/common/unit/degC",
                            "valueSpecification": {
                                "@class": "org.apache.streampipes.model.schema.QuantitativeValue",
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
                "correspondingAdapterId": "urn:streampipes.apache.org:spi:org.apache.streampipes.connect."
                                          "iiot.adapters.simulator.machine:11934d37-135b-4ef6-b5f1-4f520cc81a43",
                "category": None,
                "uri": "urn:streampipes.apache.org:eventstream:uPDKLI",
                "dom": None,
                "_rev": "1-c01cd6db1ebf6a3e23564951b836ea2b",
            }
        ]

        self.data_stream_all_json = json.dumps(self.data_stream_all)
        self.data_stream_get = self.data_stream_all[0]

        self.data_lake_measure_all_json = json.dumps(self.data_lake_measure_all)
        self.data_lake_measure_all_json_error = json.dumps(self.data_lake_measure_all[0])
        self.dlm_all_manipulated = deepcopy(self.data_lake_measure_all)
        self.dlm_all_manipulated[0]["measureName"] = False
        self.data_lake_measure_all_json_validation = json.dumps(self.dlm_all_manipulated)

    @patch("streampipes.client.client.Session", autospec=True)
    @patch("streampipes.client.client.StreamPipesClient._get_server_version", autospec=True)
    def test_endpoint_get(self, server_version: MagicMock, http_session: MagicMock):
        http_session_mock = MagicMock()
        http_session_mock.get.return_value.json.return_value = self.data_stream_get
        http_session.return_value = http_session_mock

        server_version.return_value = {"backendVersion": "0.x.y"}

        client = StreamPipesClient(
            client_config=StreamPipesClientConfig(
                credential_provider=StreamPipesApiKeyCredentials(username="user", api_key="key"),
                host_address="localhost",
            )
        )

        result = client.dataStreamApi.get(identifier="urn:streampipes.apache.org:eventstream:uPDKLI")

        http_session.assert_has_calls(
            calls=[
                call().get(
                    url="https://localhost:80/streampipes-backend/api/v2/streams/urn:streampipes."
                        "apache.org:eventstream:uPDKLI"
                )
            ],
            any_order=True,
        )
        self.assertTrue(isinstance(result, DataStream))
        self.assertEqual(result.to_dict(use_source_names=True), self.data_stream_get)

    @patch("streampipes.client.client.Session", autospec=True)
    @patch("streampipes.client.client.StreamPipesClient._get_server_version", autospec=True)
    def test_endpoint_post(self, server_version: MagicMock, http_session: MagicMock):
        http_session_mock = MagicMock()
        http_session.return_value = http_session_mock

        server_version.return_value = {"backendVersion": "0.x.y"}

        client = StreamPipesClient(
            client_config=StreamPipesClientConfig(
                credential_provider=StreamPipesApiKeyCredentials(username="user", api_key="key"),
                host_address="localhost",
            )
        )

        client.dataStreamApi.post(DataStream(**self.data_stream_get))

        http_session_mock.post.assert_called_with(
            url="https://localhost:80/streampipes-backend/api/v2/streams/",
            data=json.dumps(self.data_stream_get),
            headers={"Content-type": "application/json"},
        )

    @patch("streampipes.client.client.Session", autospec=True)
    @patch("streampipes.client.client.StreamPipesClient._get_server_version", autospec=True)
    def test_endpoint_data_stream_happy_path(self, server_version: MagicMock, http_session: MagicMock):

        server_version.return_value = {"backendVersion": "0.x.y"}

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

        self.maxDiff = None

        self.assertEqual(
            1,
            len(result),
        )
        self.assertEqual(
            "Test",
            result[0].name,  # type: ignore
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
                "internally_managed",
                "measurement_object",
                "index",
                "corresponding_adapter_id",
                "uri",
                "dom",
                "rev",
                "num_transport_protocols",
                "num_measurement_capability",
                "num_application_links",
                "num_included_assets",
                "num_connected_to",
                "num_category",
                "num_event_properties",
                "num_included_locales",
            ],
            list(result_pd.columns),
        )

    @patch("streampipes.client.client.Session", autospec=True)
    @patch("streampipes.client.client.StreamPipesClient._get_server_version", autospec=True)
    def test_endpoint_data_lake_measure_happy_path(self, server_version: MagicMock, http_session: MagicMock):

        server_version.return_value = {"backendVersion": "0.x.y"}

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
            result[0].measure_name,  # type: ignore
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

    @patch("streampipes.client.client.Session", autospec=True)
    @patch("streampipes.client.client.StreamPipesClient._get_server_version", autospec=True)
    def test_endpoint_data_lake_measure_bad_return_code(self, server_version: MagicMock, http_session: MagicMock):

        server_version.return_value = {"backendVersion": "0.x.y"}

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

    @patch("streampipes.client.client.Session", autospec=True)
    @patch("streampipes.client.client.StreamPipesClient._get_server_version", autospec=True)
    def test_endpoint_data_lake_measure_json_error(self, server_version: MagicMock, http_session: MagicMock):

        server_version.return_value = {"backendVersion": "0.x.y"}

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

    @patch("streampipes.client.client.Session", autospec=True)
    @patch("streampipes.client.client.StreamPipesClient._get_server_version", autospec=True)
    def test_endpoint_data_lake_measure_validation_error(self, server_version: MagicMock, http_session: MagicMock):

        server_version.return_value = {"backendVersion": "0.x.y"}

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


class TestMessagingEndpoint(TestCase):

    @patch("streampipes.client.client.StreamPipesClient._get_server_version", autospec=True)
    def test_messaging_endpoint_happy_path(self, _: MagicMock):

        client = StreamPipesClient(
            client_config=StreamPipesClientConfig(
                credential_provider=StreamPipesApiKeyCredentials(username="user", api_key="key"),
                host_address="localhost",
            )
        )


        demo_endpoint = MessagingEndpoint(parent_client=client)

        demo_endpoint.configure(broker=NatsConsumer())

        self.assertTrue(isinstance(demo_endpoint.broker, NatsConsumer))

    @patch("streampipes.client.client.StreamPipesClient._get_server_version", autospec=True)
    def test_messaging_endpoint_missing_configure(self, _:MagicMock):
        client = StreamPipesClient(
            client_config=StreamPipesClientConfig(
                credential_provider=StreamPipesApiKeyCredentials(username="user", api_key="key"),
                host_address="localhost",
            )
        )

        demo_endpoint = MessagingEndpoint(parent_client=client)

        with self.assertRaises(MessagingEndpointNotConfiguredError):
            demo_endpoint.broker
