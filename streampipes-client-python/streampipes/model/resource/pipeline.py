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

"""Summary representation of a pipeline."""

from streampipes.model.resource.resource import Resource

__all__ = ["PipelineSummary"]


class PipelineSummary(Resource):
    """Pipeline metadata returned by the summary endpoint."""

    element_id: str
    name: str
    description: str | None = None
    created_at: int
    running: bool
    health_status: str | None = None
    pipeline_notifications: list[str] | None = None
    valid: bool

    def convert_to_pandas_representation(self) -> dict:
        """Return scalar pipeline metadata for tabular inspection."""
        return {
            **self.model_dump(exclude={"pipeline_notifications"}),
            "num_pipeline_notifications": (
                len(self.pipeline_notifications) if self.pipeline_notifications is not None else 0
            ),
        }
