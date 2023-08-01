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
General and abstract implementation for a resource container.

A resource container is a collection of resources returned by the StreamPipes API.
It is capable of parsing the response content directly into a list of queried resources.
Furthermore, the resource container makes them accessible in a pythonic manner.
"""

from __future__ import annotations

import json
from abc import ABC, abstractmethod
from typing import Dict, List, Type

import pandas as pd
from pydantic import ValidationError

__all__ = [
    "ResourceContainer",
]

from streampipes.model.resource.resource import Resource


class StreamPipesDataModelError(Exception):
    """A custom exception to be raised when a validation error occurs
    during the parsing of StreamPipes API responses.

    Parameters
    ----------
    validation_error: ValidationError
        The validation error thrown by Pydantic during parsing.
    """

    def __init__(
        self,
        validation_error: ValidationError,
    ):
        self.validation_error = validation_error
        super().__init__(self._generate_error_message())

    def _generate_error_message(self) -> str:
        """Generate a specific error message for this exception.
        Error message contains information derived from the specific `ValidationError`.

        Returns
        -------
        The error description (`str`)
        """
        return (
            f"\nOops, there seems to be a problem with our internal StreamPipes data model.\n"
            f"This should not occur, but unfortunately did.\n"
            f"Therefore, it would be great if you could report this problem as an issue at "
            f"https://github.com/apache/streampipes.\n"
            f"Please don't forget to include the following information:\n\n"
            f"Affected Model class: {str(self.validation_error.model)}\n"
            f"Validation error log: {self.validation_error.json()}"
        )


class StreamPipesResourceContainerJSONError(Exception):
    """A custom exception to be raised when the returned JSON string
    does not suit to the structure of resource container.

    Parameters
    ----------
    container_name: str
        The class name of the resource container where the invalid data structure was detected.
    json_string: str
        The JSON string that has been tried to parse.
    """

    def __init__(
        self,
        container_name: str,  # noqa: F821
        json_string: str,
    ):
        self.container_name = container_name
        self.json_string = json_string
        super().__init__(self._generate_error_message())

    def _generate_error_message(self) -> str:
        """Generate a specific error message for this exception.
        Error message contains information about the related container model and the causing JSON string.

        Returns
        -------
        The error description (`str`)
        """
        return (
            f"\nOops, there seems to be a problem when parsing the response of the StreamPipes API."
            f"This should not occur, but unfortunately did.\n"
            f"Therefore, it would be great if you could report this problem as an issue at "
            f"https://github.com/apache/streampipes.\n"
            f"Please don't forget to include the following information:\n\n"
            f"Affected container class: {str(self.container_name)}\n"
            f"JSON string: {self.json_string}"
        )


class ResourceContainer(ABC):
    """General and abstract implementation for a resource container.

    A resource container is a collection of resources returned by the StreamPipes API.
    It is capable of parsing the response content directly into a list of queried resources.
    Furthermore, the resource container makes them accessible in a pythonic manner.

    Parameters
    ----------
    resources: List[Resource]
        A list of resources to be contained in the `ResourceContainer`.

    """

    def __init__(self, resources: List[Resource]):
        self._resources = resources

    def __getitem__(self, position: int) -> Resource:
        return self._resources[position]

    def __len__(self) -> int:
        return len(self._resources)

    def __repr__(self):
        new_line = "\n"
        return f"{self.__class__.__name__}(resources=[{new_line.join([r.__repr__() for r in self._resources])}])"

    @classmethod
    @abstractmethod
    def _resource_cls(cls) -> Type[Resource]:
        """Returns the class of the resource that are bundled.

        Returns
        -------
        cls: Resource
            class that defines the resource type contained by the container
        """
        raise NotImplementedError  # pragma: no cover

    @classmethod
    def from_json(cls, json_string: str) -> ResourceContainer:
        """Creates a `ResourceContainer` from the given JSON string.

        Parameters
        ----------
        json_string: str
            The JSON string returned from the StreamPipes API.

        Returns
        -------
        container: ResourceContainer
            instance of the container derived from the JSON definition

        Raises
        ------
        StreamPipesDataModelError
            If a resource cannot be mapped to the corresponding Python data model.
        StreamPipesResourceContainerJSONError
            If JSON response cannot be parsed to a `ResourceContainer`.
        """

        # deserialize JSON string
        parsed_json = json.loads(json_string)

        # the ResourceContainer expects a list of items
        # raise an exception if the response does not be a list
        if not isinstance(parsed_json, list):
            raise StreamPipesResourceContainerJSONError(container_name=str(cls), json_string=json_string)

        try:
            resource_container = cls(resources=[cls._resource_cls().parse_obj(item) for item in parsed_json])
        except ValidationError as ve:
            raise StreamPipesDataModelError(validation_error=ve)

        return resource_container

    def to_dicts(self, use_source_names: bool = False) -> List[Dict]:
        """Returns the contained resources as list of dictionaries.

        Parameters
        ----------
        use_source_names: bool
            Determines whether the field names are named in Python style (=`False`) or
            as originally named by StreamPipes (=`True`).

        Returns
        -------
        dictionary_list: List[Dict[str, Any]]
            List of resources in dictionary representation.
            If `use_source_names` equals `True` the keys are named as in the StreamPipes backend.
        """
        return [resource.to_dict(use_source_names=use_source_names) for resource in self._resources]

    def to_json(self) -> str:
        """Returns the resource container in the StreamPipes JSON representation.

        Returns
        -------
         json_string: str
            JSON representation of the resource container where key names are equal to
            keys used in the StreamPipes backend
        """

        return json.dumps(self.to_dicts(use_source_names=True))

    def to_pandas(self) -> pd.DataFrame:
        """Returns the resource container in representation of a Pandas Dataframe.

        Returns
        -------
        resource_container_df: pd.DataFrame
            Representation of the resource container as pandas DataFrame
        """
        return pd.DataFrame.from_records(
            # ResourceContainer is iterable itself via __get_item__
            # (ignore mypy's expectation of __iter__ here)
            data=[resource_item.convert_to_pandas_representation() for resource_item in self]  # type: ignore
        )
