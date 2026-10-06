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
DEPRECATED - the data lake measure resource has been superseded by
[DatasetMetadata][streampipes.model.resource.DatasetMetadata].
"""

import warnings
from typing import Any

from pydantic import StrictBool

from streampipes.model.resource.dataset_metadata import DatasetMetadata

__all__ = [
    "DataLakeMeasure",
]

DATA_LAKE_MEASURE_DEPRECATION_MESSAGE = (
    "`DataLakeMeasure` is deprecated since 0.99.0 and will be removed in the release following 0.99.0; "
    "please use `DatasetMetadata` instead."
)


class DataLakeMeasure(DatasetMetadata):
    """DEPRECATED - use [DatasetMetadata][streampipes.model.resource.DatasetMetadata] instead.

    Deprecated since 0.99.0, scheduled for removal in the release following 0.99.0.

    This resource is kept for backwards compatibility only. It behaves like `DatasetMetadata`
    and additionally carries the legacy `pipeline_is_running` field, which StreamPipes no longer returns.
    """

    pipeline_is_running: StrictBool | None = None

    def model_post_init(self, __context: Any) -> None:
        """Emits a deprecation warning whenever an instance is created."""
        warnings.warn(DATA_LAKE_MEASURE_DEPRECATION_MESSAGE, DeprecationWarning, stacklevel=2)
