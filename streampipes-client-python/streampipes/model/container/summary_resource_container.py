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

"""Container support for resource summary responses."""

import json
from collections.abc import Iterator

from pydantic import ValidationError
from typing_extensions import Self

from streampipes.model.container.resource_container import (
    ResourceContainer,
    StreamPipesDataModelError,
    StreamPipesResourceContainerJSONError,
)
from streampipes.model.resource.resource import Resource

__all__ = ["SummaryResourceContainer"]


class SummaryResourceContainer(ResourceContainer):
    """Container for ``resources`` and ``totalCount`` summary responses."""

    def __init__(self, resources: list[Resource], total_count: int):
        super().__init__(resources=resources)
        self.total_count = total_count

    @classmethod
    def from_json(cls, json_string: str) -> Self:
        """Parse a backend resource-summary response."""
        parsed_json = json.loads(json_string)
        if (
            not isinstance(parsed_json, dict)
            or not isinstance(parsed_json.get("resources"), list)
            or not isinstance(parsed_json.get("totalCount"), int)
        ):
            raise StreamPipesResourceContainerJSONError(container_name=str(cls), json_string=json_string)

        try:
            resources = [cls._resource_cls().model_validate(item) for item in parsed_json["resources"]]
        except ValidationError as validation_error:
            raise StreamPipesDataModelError(validation_error=validation_error) from validation_error

        return cls(resources=resources, total_count=parsed_json["totalCount"])

    def __iter__(self) -> Iterator[Resource]:
        """Iterate over contained summary resources."""
        return iter(self._resources)
