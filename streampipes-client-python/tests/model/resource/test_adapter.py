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

from streampipes.model.container import Adapters
from streampipes.model.resource import AdapterSummary


class TestAdapterSummary(TestCase):
    def setUp(self):
        self.adapter_summary = {
            "elementId": "adapter-1",
            "correspondingDataStreamElementId": "stream-1",
            "name": "Adapter",
            "description": "Description",
            "running": True,
            "createdAt": 123,
            "appId": "org.example.adapter",
            "includedAssets": ["icon.png"],
            "icon": "icon.png",
        }

    def test_aliases_and_pandas_representation(self):
        adapter = AdapterSummary.model_validate(self.adapter_summary)

        self.assertDictEqual(adapter.to_dict(), self.adapter_summary)
        self.assertDictEqual(
            adapter.convert_to_pandas_representation(),
            {
                "element_id": "adapter-1",
                "corresponding_data_stream_element_id": "stream-1",
                "name": "Adapter",
                "description": "Description",
                "running": True,
                "created_at": 123,
                "app_id": "org.example.adapter",
                "icon": "icon.png",
                "num_included_assets": 1,
            },
        )

    def test_summary_container(self):
        adapters = Adapters.from_json(
            '{"resources": ['
            '{"elementId": "adapter-1", "name": "Adapter", "running": false, '
            '"createdAt": 123, "appId": "org.example.adapter"}'
            '], "totalCount": 5}'
        )

        self.assertEqual(len(adapters), 1)
        self.assertEqual(adapters.total_count, 5)
        self.assertIsInstance(adapters[0], AdapterSummary)
