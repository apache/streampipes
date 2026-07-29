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

"""Adapter API endpoint."""

import json

from streampipes.endpoint.endpoint import APIEndpoint
from streampipes.model.compact import CompactAdapter
from streampipes.model.container import Adapters
from streampipes.model.container.resource_container import ResourceContainer
from streampipes.model.resource import AdapterSummary

__all__ = ["AdapterEndpoint"]


class AdapterEndpoint(APIEndpoint):
    """Read adapter summaries and create compact adapters."""

    @property
    def _container_cls(self) -> type[ResourceContainer]:
        """Return the adapter summary container type."""
        return Adapters

    @property
    def _relative_api_path(self) -> tuple[str, ...]:
        """Return the adapter endpoint's relative REST path."""
        return "api", "v2", "connect", "master", "adapters"

    @property
    def _compact_url(self) -> str:
        """Return the compact adapter creation URL."""
        return f"{self._parent_client.base_api_path}api/v2/connect/compact-adapters"

    def all(self) -> Adapters:
        """Return all accessible adapter summaries."""
        response = self._make_request(
            request_method=self._parent_client.request_session.get,
            url=f"{self.build_url()}/summary",
        )
        return Adapters.from_json(json_string=response.text)

    def get(self, identifier: str, **kwargs) -> AdapterSummary:
        """Return one adapter summary by identifier."""
        for adapter in self.all():
            if adapter.element_id == identifier:
                return adapter
        raise KeyError(f"No adapter summary found for identifier '{identifier}'.")

    def post(self, resource: object) -> None:
        """Create an adapter from its compact representation."""
        if not isinstance(resource, CompactAdapter):
            raise TypeError("Adapter creation requires a CompactAdapter.")

        self._make_request(
            request_method=self._parent_client.request_session.post,
            url=self._compact_url,
            data=json.dumps(resource.to_dict(use_source_names=True)),
            headers={"Content-type": "application/json"},
        )

    def put(self, resource: object, identifier: str | None = None) -> None:
        """Reject adapter updates, which are intentionally unsupported."""
        raise NotImplementedError("Updating adapters is not supported by the Python client.")

    def delete(self, identifier: str) -> None:
        """Delete an adapter."""
        self._make_request(
            request_method=self._parent_client.request_session.delete,
            url=f"{self.build_url()}/{identifier}",
        )

    def start(self, identifier: str) -> None:
        """Start an adapter by identifier."""
        self._make_request(
            request_method=self._parent_client.request_session.post,
            url=f"{self.build_url()}/{identifier}/start",
        )

    def stop(self, identifier: str) -> None:
        """Stop an adapter by identifier."""
        self._make_request(
            request_method=self._parent_client.request_session.post,
            url=f"{self.build_url()}/{identifier}/stop",
        )
