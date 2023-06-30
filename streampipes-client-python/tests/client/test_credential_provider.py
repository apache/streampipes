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

    @staticmethod
    def _clear_envs():

        if StreamPipesApiKeyCredentials._ENV_KEY_API in os.environ.keys():
            del os.environ[StreamPipesApiKeyCredentials._ENV_KEY_API]

        if StreamPipesApiKeyCredentials._ENV_KEY_USERNAME in os.environ.keys():
            del os.environ[StreamPipesApiKeyCredentials._ENV_KEY_USERNAME]

    def test_api_key_credentials(self):
        credentials = StreamPipesApiKeyCredentials(username="test-user", api_key="test-key")
        result = credentials.make_headers()

        expected = {"X-API-User": "test-user", "X-API-Key": "test-key"}

        self.assertDictEqual(expected, result)

        result_extended = credentials.make_headers(http_headers={"test": "test"})
        expected_extended = {**expected, "test": "test"}

        self.assertDictEqual(expected_extended, result_extended)

    def test_pass_credentials(self):
        credentials = StreamPipesApiKeyCredentials(username="username", api_key="api-key")

        self.assertEqual("username", credentials.username)
        self.assertEqual("api-key", credentials.api_key)

    def test_pass_credentials_envs_set(self):

        os.environ[StreamPipesApiKeyCredentials._ENV_KEY_API] = "another-api-key"
        os.environ[StreamPipesApiKeyCredentials._ENV_KEY_USERNAME] = "another-user-name"

        credentials = StreamPipesApiKeyCredentials(username="username", api_key="api-key")

        self.assertEqual("username", credentials.username)
        self.assertEqual("api-key", credentials.api_key)

    def test_pass_username(self):

        self._clear_envs()
        os.environ[StreamPipesApiKeyCredentials._ENV_KEY_API] = "another-api-key"

        credentials = StreamPipesApiKeyCredentials(username="username")

        self.assertEqual("username", credentials.username)
        self.assertEqual("another-api-key", credentials.api_key)

    def test_pass_username_api_key_not_set(self):

        self._clear_envs()

        with self.assertRaises(AttributeError):
            StreamPipesApiKeyCredentials(username="username")

    def test_pass_api_key(self):

        self._clear_envs()
        os.environ[StreamPipesApiKeyCredentials._ENV_KEY_USERNAME] = "another-username"

        credentials = StreamPipesApiKeyCredentials(api_key="api-key")

        self.assertEqual("another-username", credentials.username)
        self.assertEqual("api-key", credentials.api_key)

    def test_pass_api_key_username_not_set(self):

        self._clear_envs()

        with self.assertRaises(AttributeError):
            StreamPipesApiKeyCredentials(api_key="api-key")

    def test_nothing_set(self):

        self._clear_envs()

        with self.assertRaises(AttributeError):
            StreamPipesApiKeyCredentials()

    def test_all_from_envs(self):

        os.environ[StreamPipesApiKeyCredentials._ENV_KEY_API] = "another-api-key"
        os.environ[StreamPipesApiKeyCredentials._ENV_KEY_USERNAME] = "another-username"

        credentials = StreamPipesApiKeyCredentials()

        self.assertEqual("another-username", credentials.username)
        self.assertEqual("another-api-key", credentials.api_key)
