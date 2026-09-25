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
"""
DEPRECATED - the data lake measure endpoint has been superseded by
[DatasetEndpoint][streampipes.endpoint.api.dataset.DatasetEndpoint].
This module only re-exports the query configuration and provides the deprecated endpoint class.
"""

import warnings

from streampipes.endpoint.api.dataset import (
    DatasetEndpoint,
    MeasurementGetQueryConfig,
    StreamPipesQueryValidationError,
)
from streampipes.model.container import DataLakeMeasures
from streampipes.model.container.resource_container import ResourceContainer

__all__ = [
    "DataLakeMeasureEndpoint",
    "MeasurementGetQueryConfig",
    "StreamPipesQueryValidationError",
]

DATA_LAKE_MEASURE_ENDPOINT_DEPRECATION_MESSAGE = (
    "`DataLakeMeasureEndpoint` is deprecated since 0.99.0 and will be removed in the release following 0.99.0; "
    "please use `DatasetEndpoint` (`client.datasetApi`) instead."
)


class DataLakeMeasureEndpoint(DatasetEndpoint):
    """DEPRECATED - use [DatasetEndpoint][streampipes.endpoint.api.dataset.DatasetEndpoint] instead.

    Deprecated since 0.99.0, scheduled for removal in the release following 0.99.0.

    This endpoint is kept for backwards compatibility only. It behaves like `DatasetEndpoint`
    but returns the deprecated [DataLakeMeasures][streampipes.model.container.DataLakeMeasures] container
    from its `all()` method.
    """

    def __init__(self, parent_client: "StreamPipesClient"):  # type: ignore # noqa: F821
        warnings.warn(DATA_LAKE_MEASURE_ENDPOINT_DEPRECATION_MESSAGE, DeprecationWarning, stacklevel=2)
        super().__init__(parent_client=parent_client)

    @property
    def _container_cls(self) -> type[ResourceContainer]:
        """Defines the model container class the endpoint refers to.

        Returns
        -------
        [DataLakeMeasures][streampipes.model.container.DataLakeMeasures]
        """
        return DataLakeMeasures
