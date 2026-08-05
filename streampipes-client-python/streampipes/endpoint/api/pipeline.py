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

"""Pipeline API endpoint."""

import json

from streampipes.endpoint.endpoint import APIEndpoint
from streampipes.model.compact import CompactPipeline
from streampipes.model.container import Pipelines
from streampipes.model.container.resource_container import ResourceContainer
from streampipes.model.resource import PipelineSummary

__all__ = ["PipelineEndpoint"]


class PipelineEndpoint(APIEndpoint):
    """Read pipeline summaries and create compact pipelines."""

    @property
    def _container_cls(self) -> type[ResourceContainer]:
        """Return the pipeline summary container type."""
        return Pipelines

    @property
    def _relative_api_path(self) -> tuple[str, ...]:
        """Return the pipeline endpoint's relative REST path."""
        return "api", "v2", "pipelines"

    @property
    def _compact_url(self) -> str:
        """Return the compact pipeline creation URL."""
        return f"{self._parent_client.base_api_path}api/v2/compact-pipelines"

    def all(self) -> Pipelines:
        """Return all accessible pipeline summaries."""
        response = self._make_request(
            request_method=self._parent_client.request_session.get,
            url=f"{self.build_url()}/summary",
        )
        return Pipelines.from_json(json_string=response.text)

    def get(self, identifier: str, **kwargs) -> PipelineSummary:
        """Return one pipeline summary by identifier."""
        for pipeline in self.all():
            if pipeline.element_id == identifier:
                return pipeline
        raise KeyError(f"No pipeline summary found for identifier '{identifier}'.")

    def post(self, resource: object) -> None:
        """Create a pipeline from its compact representation."""
        if not isinstance(resource, CompactPipeline):
            raise TypeError("Pipeline creation requires a CompactPipeline.")

        self._make_request(
            request_method=self._parent_client.request_session.post,
            url=self._compact_url,
            data=json.dumps(resource.to_dict(use_source_names=True)),
            headers={"Content-type": "application/json"},
        )

    def put(self, resource: object, identifier: str | None = None) -> None:
        """Reject pipeline updates, which are intentionally unsupported."""
        raise NotImplementedError("Updating pipelines is not supported by the Python client.")

    def delete(self, identifier: str) -> None:
        """Delete a pipeline."""
        self._make_request(
            request_method=self._parent_client.request_session.delete,
            url=f"{self.build_url()}/{identifier}",
        )

    def start(self, identifier: str) -> None:
        """Start a pipeline by identifier."""
        self._make_request(
            request_method=self._parent_client.request_session.get,
            url=f"{self.build_url()}/{identifier}/start",
        )

    def stop(self, identifier: str) -> None:
        """Stop a pipeline by identifier."""
        self._make_request(
            request_method=self._parent_client.request_session.get,
            url=f"{self.build_url()}/{identifier}/stop",
        )
