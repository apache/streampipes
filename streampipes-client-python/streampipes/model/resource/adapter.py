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

"""Representations of adapters returned by the adapter APIs."""

from typing import Any

from pydantic import ConfigDict, Field

from streampipes.model.resource.resource import Resource

__all__ = ["AdapterDescription", "AdapterSummary"]


class AdapterSummary(Resource):
    """Adapter metadata returned by the summary endpoint."""

    element_id: str
    corresponding_data_stream_element_id: str | None = None
    name: str
    description: str | None = None
    running: bool
    created_at: int
    app_id: str
    included_assets: list[str] | None = None
    icon: str | None = None

    def convert_to_pandas_representation(self) -> dict:
        """Return scalar adapter metadata for tabular inspection."""
        return {
            **self.model_dump(exclude={"included_assets"}),
            "num_included_assets": len(self.included_assets) if self.included_assets is not None else 0,
        }


class AdapterDescription(AdapterSummary):
    """Complete adapter representation returned by the detail endpoint."""

    model_config = ConfigDict(extra="allow")

    class_name: str | None = Field(default=None, alias="@class")
    data_stream: dict[str, Any] | None = None
    config: list[dict[str, Any]] = Field(default_factory=list)
    selected_endpoint_url: str | None = None
