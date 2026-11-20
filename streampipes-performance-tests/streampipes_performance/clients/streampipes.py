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

"""Performance API extensions over the unmodified Apache Python client."""

import logging
from dataclasses import dataclass
from functools import partial
from typing import Any
from urllib.parse import quote

from streampipes.client import StreamPipesClient as ApacheClient
from streampipes.client.config import StreamPipesClientConfig as ApacheConfig
from streampipes.endpoint.api.adapter import AdapterEndpoint as ApacheAdapter
from streampipes.endpoint.api.data_lake_measure import DataLakeMeasureEndpoint as ApacheMeasurements
from streampipes.endpoint.api.pipeline import PipelineEndpoint as ApachePipeline
from streampipes.endpoint.endpoint import APIEndpoint, Endpoint
from streampipes.model.compact import CompactAdapter, CompactPipeline
from streampipes.model.resource import AdapterDescription


@dataclass
class StreamPipesClientConfig(ApacheConfig):
    """Connection settings with a bounded default request timeout."""

    request_timeout: float | None = 30


class StreamPipesClient(ApacheClient):
    """Authenticated Apache client with locally owned performance endpoints."""

    def __init__(self, client_config, logging_level=logging.WARNING):
        timeout = client_config.request_timeout
        if timeout is not None and timeout <= 0:
            raise ValueError("request_timeout must be positive")
        self._transport_configured = False
        super().__init__(client_config, logging_level=logging_level)
        self.adapterApi = AdapterEndpoint(parent_client=self)
        self.pipelineApi = PipelineEndpoint(parent_client=self)
        self.datasetApi = DatasetEndpoint(parent_client=self)
        self.streamApi = StreamEndpoint(parent_client=self)
        self._measurement_api = MeasurementEndpoint(parent_client=self)

    @property
    def dataLakeMeasureApi(self):
        """Return the local endpoint in place of the deprecated Apache property."""
        return self._measurement_api

    def refresh_headers(self):
        """Set the timeout before the constructor's first server request."""
        if not self._transport_configured:
            self.request_session.request = partial(
                self.request_session.request, timeout=self.client_config.request_timeout
            )
            self._transport_configured = True
        return super().refresh_headers()

    def request_json(self, method: str, path: str, **kwargs: Any) -> Any:
        """Request a relative API path using this client's authenticated transport.

        Parameters
        ----------
        method : str
            HTTP verb.
        path : str
            API path relative to the backend, beginning with ``api/``.
        **kwargs : Any
            Requests arguments, including an optional timeout override.

        Returns
        -------
        Any
            Decoded JSON, or None for an empty response.
        """
        if not path.startswith("api/") or ".." in path.split("/"):
            raise ValueError("Expected a relative api/ path")
        response = APIEndpoint._make_request(
            request_method=partial(self.request_session.request, method),
            url=f"{self.base_api_path}{path}",
            **kwargs,
        )
        return response.json() if response.content else None


class AdapterEndpoint(ApacheAdapter):
    """Lifecycle operations with result bodies and encoded identifiers."""

    def get(self, identifier: str, **kwargs) -> AdapterDescription:
        """Return one complete adapter description by identifier."""
        response = self._make_request(
            request_method=self._parent_client.request_session.get,
            url=f"{self.build_url()}/{quote(identifier, safe='')}",
        )
        return AdapterDescription.model_validate(response.json())

    def delete(self, identifier: str) -> None:
        """Delete an adapter."""
        self._make_request(
            request_method=self._parent_client.request_session.delete,
            url=f"{self.build_url()}/{quote(identifier, safe='')}",
        )

    def start(self, identifier: str) -> None:
        """Start an adapter by identifier."""
        self._make_request(
            request_method=self._parent_client.request_session.post,
            url=f"{self.build_url()}/{quote(identifier, safe='')}/start",
        )

    def stop(self, identifier: str, force_stop: bool = False) -> None:
        """Stop an adapter by identifier."""
        self._make_request(
            request_method=self._parent_client.request_session.post,
            url=f"{self.build_url()}/{quote(identifier, safe='')}/stop",
            **({"params": {"forceStop": "true"}} if force_stop else {}),
        )

    def start_result(self, identifier: str) -> dict[str, Any]:
        """Start a resource and return its operation result body."""
        return self._parent_client.request_json(
            "POST", f"api/v2/connect/master/adapters/{quote(identifier, safe='')}/start"
        )

    def stop_result(self, identifier: str, force_stop: bool = False) -> dict[str, Any]:
        """Stop a resource and return its operation result body."""
        return self._parent_client.request_json(
            "POST",
            f"api/v2/connect/master/adapters/{quote(identifier, safe='')}/stop",
            params={"forceStop": str(force_stop).lower()},
        )

    def create_result(self, resource: CompactAdapter) -> dict[str, Any]:
        """Create a compact resource and return the server response."""
        return self._parent_client.request_json(
            "POST",
            "api/v2/connect/compact-adapters",
            json=resource.to_dict(use_source_names=True),
        )

    def delete_result(self, identifier: str) -> Any:
        """Delete a resource and return the server response."""
        return self._parent_client.request_json(
            "DELETE", f"api/v2/connect/master/adapters/{quote(identifier, safe='')}"
        )


class PipelineEndpoint(ApachePipeline):
    """Lifecycle operations with result bodies and encoded identifiers."""

    def delete(self, identifier: str) -> None:
        """Delete a pipeline."""
        self._make_request(
            request_method=self._parent_client.request_session.delete,
            url=f"{self.build_url()}/{quote(identifier, safe='')}",
        )

    def start(self, identifier: str) -> None:
        """Start a pipeline by identifier."""
        self._make_request(
            request_method=self._parent_client.request_session.get,
            url=f"{self.build_url()}/{quote(identifier, safe='')}/start",
        )

    def stop(self, identifier: str, force_stop: bool = False) -> None:
        """Stop a pipeline by identifier."""
        self._make_request(
            request_method=self._parent_client.request_session.get,
            url=f"{self.build_url()}/{quote(identifier, safe='')}/stop",
            **({"params": {"forceStop": "true"}} if force_stop else {}),
        )

    def start_result(self, identifier: str) -> dict[str, Any]:
        """Start a resource and return its operation result body."""
        return self._parent_client.request_json("GET", f"api/v2/pipelines/{quote(identifier, safe='')}/start")

    def stop_result(self, identifier: str, force_stop: bool = False) -> dict[str, Any]:
        """Stop a resource and return its operation result body."""
        return self._parent_client.request_json(
            "GET",
            f"api/v2/pipelines/{quote(identifier, safe='')}/stop",
            params={"forceStop": str(force_stop).lower()},
        )

    def create_result(self, resource: CompactPipeline) -> dict[str, Any]:
        """Create a compact resource and return the server response."""
        return self._parent_client.request_json(
            "POST",
            "api/v2/compact-pipelines",
            json=resource.to_dict(use_source_names=True),
        )

    def delete_result(self, identifier: str) -> Any:
        """Delete a resource and return the server response."""
        return self._parent_client.request_json("DELETE", f"api/v2/pipelines/{quote(identifier, safe='')}")

    def get_details(self, identifier: str) -> dict[str, Any]:
        """Retrieve pipeline details directly without listing summaries."""
        return self._parent_client.request_json("GET", f"api/v2/pipelines/{quote(identifier, safe='')}")


class StreamEndpoint(Endpoint):
    """Manage explicitly registered benchmark input streams."""

    def create(self, payload):
        return self._parent_client.request_json("POST", "api/v2/streams", json=payload)

    def all(self):
        return self._parent_client.request_json("GET", "api/v2/streams")

    def delete_result(self, identifier):
        return self._parent_client.request_json("DELETE", f"api/v2/streams/{quote(identifier, safe='')}")


class DatasetEndpoint(Endpoint):
    """List dataset metadata and manage dataset storage."""

    def create(self, payload: dict[str, Any]) -> dict[str, Any]:
        """Create dataset metadata; the server assigns its identifier."""
        return self._parent_client.request_json("POST", "api/v4/datalake/measure", json=payload)

    def count(self, identifier: str) -> int:
        """Return the persisted event count across the complete dataset."""
        return self._parent_client.request_json(
            "GET", f"api/v4/datalake/measure/{quote(identifier, safe='')}/count"
        )

    def upload(self, name: str, headers: list[str], rows: list[list[Any]]) -> Any:
        """Append one bounded batch, retaining application-level error responses."""
        return self._parent_client.request_json(
            "POST",
            f"api/v4/datalake/measurements/{quote(name, safe='')}",
            params={"ignoreSchemaMismatch": "false"},
            json={"allDataSeries": [{"headers": headers, "rows": rows, "total": len(rows)}]},
        )

    def all(self) -> list[dict[str, Any]]:
        """Return accessible dataset summaries."""
        return self._parent_client.request_json("GET", "api/v4/datalake/measure/summary")["resources"]

    def get_by_name(self, name: str) -> dict[str, Any]:
        """Return metadata by measurement name; missing datasets raise HTTPError."""
        return self._parent_client.request_json(
            "GET", f"api/v4/datalake/measure/byName/{quote(name, safe='')}"
        )

    def delete(self, identifier: str) -> Any:
        """Delete dataset metadata and stored data."""
        return self._parent_client.request_json(
            "DELETE", f"api/v4/datalake/measure/{quote(identifier, safe='')}"
        )


class MeasurementEndpoint(ApacheMeasurements):
    """Encode measurement identifiers while reusing query parsing and paging."""

    def get(self, identifier: str, **kwargs: Any):
        """Read a measurement using a URL-safe identifier."""
        return super().get(quote(identifier, safe=""), **kwargs)

    def storeDataToMeasurement(self, identifier: str, df, ignore_schema_mismatch=False, batch_size=10000):
        """Write measurement data using a URL-safe identifier."""
        return super().storeDataToMeasurement(
            quote(identifier, safe=""), df, ignore_schema_mismatch, batch_size
        )
