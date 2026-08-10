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

from streampipes.model.container import Pipelines
from streampipes.model.resource import PipelineSummary


class TestPipelineSummary(TestCase):
    def setUp(self):
        self.pipeline_summary = {
            "elementId": "pipeline-1",
            "name": "Pipeline",
            "description": "Description",
            "createdAt": 123,
            "running": False,
            "healthStatus": "OK",
            "pipelineNotifications": ["warning"],
            "valid": True,
        }

    def test_aliases_and_pandas_representation(self):
        pipeline = PipelineSummary.model_validate(self.pipeline_summary)

        self.assertDictEqual(pipeline.to_dict(), self.pipeline_summary)
        self.assertDictEqual(
            pipeline.convert_to_pandas_representation(),
            {
                "element_id": "pipeline-1",
                "name": "Pipeline",
                "description": "Description",
                "created_at": 123,
                "running": False,
                "health_status": "OK",
                "valid": True,
                "num_pipeline_notifications": 1,
            },
        )

    def test_summary_container(self):
        pipelines = Pipelines.from_json(
            '{"resources": ['
            '{"elementId": "pipeline-1", "name": "Pipeline", "createdAt": 123, '
            '"running": false, "valid": true}'
            '], "totalCount": 3}'
        )

        self.assertEqual(len(pipelines), 1)
        self.assertEqual(pipelines.total_count, 3)
        self.assertIsInstance(pipelines[0], PipelineSummary)
