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
import os
from unittest import TestCase

from streampipes.client.credential_provider import StreamPipesApiKeyCredentials


class TestStreamPipesApiKeyCredentials(TestCase):
    def test_api_key_credentials(self):
        credentials = StreamPipesApiKeyCredentials(username="test-user", api_key="test-key")
        result = credentials.make_headers()

        expected = {"X-API-User": "test-user", "X-API-Key": "test-key"}

        self.assertDictEqual(expected, result)

        result_extended = credentials.make_headers(http_headers={"test": "test"})
        expected_extended = {**expected, "test": "test"}

        self.assertDictEqual(expected_extended, result_extended)

    def test_api_key_from_env(self):
        os.environ["USER"] = "user"
        os.environ["KEY"] = "api-key"
        credentials = StreamPipesApiKeyCredentials.from_env(username_env="USER", api_key_env="KEY")

        self.assertEqual("user", credentials.username)
        self.assertEqual("api-key", credentials.api_key)

    def test_api_key_from_env_not_set(self):
        with self.assertRaises(KeyError):
            StreamPipesApiKeyCredentials.from_env(username_env="test", api_key_env="key")
