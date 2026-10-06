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
DEPRECATED - the data lake measures container has been superseded by
[Datasets][streampipes.model.container.Datasets].
"""

import warnings

from streampipes.model.container.datasets import Datasets
from streampipes.model.resource.data_lake_measure import DataLakeMeasure
from streampipes.model.resource.resource import Resource

__all__ = [
    "DataLakeMeasures",
]

DATA_LAKE_MEASURES_DEPRECATION_MESSAGE = (
    "`DataLakeMeasures` is deprecated since 0.99.0 and will be removed in the release following 0.99.0; "
    "please use `Datasets` instead."
)


class DataLakeMeasures(Datasets):
    """DEPRECATED - use [Datasets][streampipes.model.container.Datasets] instead.

    Deprecated since 0.99.0, scheduled for removal in the release following 0.99.0.

    This container is kept for backwards compatibility only and bundles the deprecated
    [DataLakeMeasure][streampipes.model.resource.DataLakeMeasure] resources.
    """

    def __init__(self, resources: list[Resource]):
        warnings.warn(DATA_LAKE_MEASURES_DEPRECATION_MESSAGE, DeprecationWarning, stacklevel=2)
        super().__init__(resources=resources)

    @classmethod
    def _resource_cls(cls) -> type[Resource]:
        """Returns the class of the resource that are bundled.

        Returns
        -------
        type: DataLakeMeasure
            class that describes an individual resource
        """
        return DataLakeMeasure
