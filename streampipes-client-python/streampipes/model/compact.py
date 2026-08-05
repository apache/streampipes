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

"""Compact payloads used to create adapters and pipelines."""

from typing import Any

import yaml  # type: ignore[import-untyped]
from pydantic import ConfigDict, Field
from typing_extensions import Self

from streampipes.model.common import BasicModel

__all__ = [
    "CompactAdapter",
    "CompactEventProperty",
    "CompactPipeline",
    "CompactPipelineElement",
    "CreateOptions",
    "OutputConfiguration",
    "UserDefinedOutput",
]


class CompactModel(BasicModel):
    """Base model for forward-compatible compact payloads."""

    model_config = ConfigDict(extra="allow")

    @classmethod
    def from_json(cls, serialized: str) -> Self:
        """Create a compact model from a JSON string."""
        return cls.model_validate_json(serialized)

    @classmethod
    def from_yaml(cls, serialized: str) -> Self:
        """Create a compact model from a YAML string."""
        return cls.model_validate(yaml.safe_load(serialized))

    def to_dict(self, use_source_names: bool = True) -> dict:
        """Serialize a compact payload using backend aliases by default."""
        return self.model_dump(by_alias=use_source_names, exclude_none=True)


class CreateOptions(CompactModel):
    """Actions performed after creating a compact resource."""

    persist: bool = False
    start: bool = False


class CompactEventProperty(CompactModel):
    """Optional metadata for one compact adapter event property."""

    label: str | None = None
    description: str | None = None
    property_scope: str | None = None
    semantic_type: str | None = None
    additional_metadata: dict[str, Any] | None = None


class CompactAdapter(CompactModel):
    """Minimal payload accepted by the compact adapter endpoint."""

    name: str
    app_id: str
    id: str | None = None
    description: str | None = None
    configuration: list[dict[str, Any]] = Field(default_factory=list)
    transformation_config: dict[str, Any] | None = None
    event_schema: dict[str, CompactEventProperty] = Field(
        default_factory=dict,
        alias="schema",
    )
    create_options: CreateOptions = Field(default_factory=CreateOptions)


class UserDefinedOutput(CompactModel):
    """User-defined field in a compact pipeline output."""

    field_name: str
    runtime_type: str
    semantic_type: str | None = None


class OutputConfiguration(CompactModel):
    """Compact output selection for a pipeline element."""

    keep: list[str] | None = None
    user_defined: list[UserDefinedOutput] | None = None


class CompactPipelineElement(CompactModel):
    """Stream, processor, or sink entry in a compact pipeline."""

    type: str
    id: str
    ref: str | None = None
    connected_to: list[str] | None = None
    configuration: list[dict[str, Any]] | None = None
    output: OutputConfiguration | None = None


class CompactPipeline(CompactModel):
    """Minimal payload accepted by the compact pipeline endpoint."""

    name: str
    pipeline_elements: list[CompactPipelineElement]
    id: str | None = None
    description: str | None = None
    create_options: CreateOptions = Field(default_factory=CreateOptions)
