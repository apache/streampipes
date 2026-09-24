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

"""Typed options for data lake queries."""

from __future__ import annotations

from decimal import Decimal
from enum import Enum
from typing import Literal

from pydantic import (
    BaseModel,
    ConfigDict,
    Field,
    StrictBool,
    StrictFloat,
    StrictInt,
    StrictStr,
    model_validator,
)

__all__ = ["AggregationFunction", "Column", "FilterCondition", "FilterGroup"]


class AggregationFunction(str, Enum):
    """Aggregation functions supported by the StreamPipes REST API."""

    MEAN = "MEAN"
    MEDIAN = "MEDIAN"
    MIN = "MIN"
    MAX = "MAX"
    COUNT = "COUNT"
    FIRST = "FIRST"
    LAST = "LAST"
    MODE = "MODE"
    STDDEV = "STDDEV"
    SUM = "SUM"
    SPREAD = "SPREAD"


class Column(BaseModel):
    """Select a field, optionally aggregating it and naming the result.

    Parameters
    ----------
    name: str
        Field name to select.
    alias: str | None
        Output column name. Requires an aggregation function.
    aggregation: AggregationFunction | None
        Function applied to this field. Omit for a plain field selection.
    """

    model_config = ConfigDict(extra="forbid", frozen=True)

    name: str = Field(pattern=r"^[0-9a-zA-Z_]+$")
    alias: str | None = Field(default=None, pattern=r"^[0-9a-zA-Z_]+$")
    aggregation: AggregationFunction | None = None

    @model_validator(mode="after")
    def _validate_alias(self) -> Column:
        """Reject aliases without aggregation, which the REST API cannot represent."""
        if self.alias is not None and self.aggregation is None:
            raise ValueError("A column alias requires an aggregation function.")
        return self

    def to_query_string(self) -> str:
        """Serialize the column using the REST column selection syntax."""
        if self.aggregation is None:
            return self.name
        parts = [self.name, self.aggregation.value]
        if self.alias is not None:
            parts.append(self.alias)
        return "[" + ";".join(parts) + "]"


class FilterCondition(BaseModel):
    """Compare a field with a typed value.

    Parameters
    ----------
    field: str
        Field name to filter.
    operator: str
        Comparison operator: =, !=, <, <=, > or >=.
    value: str | int | float | bool
        Comparison value. Serialized as `condition` in the REST API.
        The server may interpret numeric or boolean strings as those types;
        quote such strings explicitly (for example, "'123'") to compare as text.
    """

    model_config = ConfigDict(extra="forbid", frozen=True, populate_by_name=True, allow_inf_nan=False)

    type: Literal["condition"] = "condition"
    field: str = Field(min_length=1)
    operator: Literal["=", "!=", "<", "<=", ">", ">="]
    value: StrictStr | StrictInt | StrictFloat | StrictBool = Field(alias="condition")

    def to_query_string(self) -> str:
        """Serialize a condition using the legacy REST filter syntax.

        Raises
        ------
        ValueError
            If the field or value contains delimiters that the legacy syntax
            cannot represent. Use `filter_expression` for these conditions.
        """
        if isinstance(self.value, bool):
            value = str(self.value).lower()
        elif isinstance(self.value, float):
            value = format(Decimal(str(self.value)), "f")
        else:
            value = str(self.value)
        if any(char in part for part in (self.field, value) for char in ",;[]"):
            raise ValueError("Filter fields and values containing , ; [ or ] require filter_expression.")
        if not value:
            value = "''"
        return f"[{self.field};{self.operator};{value}]"


class FilterGroup(BaseModel):
    """Combine filter conditions and nested groups.

    Parameters
    ----------
    operator: str
        Logical operator: AND or OR.
    children: list[FilterCondition | FilterGroup]
        One or more conditions or nested groups.
    """

    model_config = ConfigDict(extra="forbid", frozen=True)

    type: Literal["group"] = "group"
    operator: Literal["AND", "OR"]
    children: list[FilterCondition | FilterGroup] = Field(min_length=1)

    @classmethod
    def all_of(cls, *children: FilterCondition | FilterGroup) -> FilterGroup:
        """Combine conditions or groups using AND."""
        return cls(operator="AND", children=list(children))

    @classmethod
    def any_of(cls, *children: FilterCondition | FilterGroup) -> FilterGroup:
        """Combine conditions or groups using OR."""
        return cls(operator="OR", children=list(children))

    def to_query_string(self) -> str:
        """Serialize the nested filter as JSON for the REST API."""
        return self.model_dump_json(by_alias=True)
