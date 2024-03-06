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
from unittest.mock import MagicMock, call, patch

from streampipes.client import StreamPipesClient
from streampipes.client.config import StreamPipesClientConfig
from streampipes.client.credential_provider import StreamPipesApiKeyCredentials


class TestVersions(TestCase):

    @patch("streampipes.client.client.Session", autospec=True)
    def test_get_development_version(self, http_session: MagicMock) -> None:
        http_session_mock = MagicMock()
        http_session_mock.get.return_value.json.return_value = {"backendVersion": None}
        http_session.return_value = http_session_mock

        client = StreamPipesClient(
            client_config=StreamPipesClientConfig(
                credential_provider=StreamPipesApiKeyCredentials(username="user", api_key="key"),
                host_address="localhost",
            )
        )

        result = client.versionApi.get("")

        http_session.assert_has_calls(
            [call().get(url="https://localhost:80/streampipes-backend/api/v2/info/versions")], any_order=True
        )

        self.assertEqual("development", result.backend_version)
