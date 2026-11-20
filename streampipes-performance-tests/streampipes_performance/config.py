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

"""Strict JSON configuration and deterministic scenario expansion."""

import itertools
import json
from pathlib import Path
from typing import Any, Literal

from pydantic import BaseModel, ConfigDict, Field, field_validator, model_validator


class Model(BaseModel):
    model_config = ConfigDict(extra="forbid", allow_inf_nan=False)


class Timing(Model):
    warmup_seconds: float = Field(default=15, ge=0)
    duration_seconds: float = Field(default=30, ge=0.001)
    poll_seconds: float = Field(default=0.5, gt=0)
    readiness_timeout_seconds: float = Field(default=60, gt=0)
    drain_timeout_seconds: float = Field(default=30, gt=0)
    stable_seconds: float = Field(default=2, gt=0)
    request_timeout_seconds: float = Field(default=30, gt=0)
    # Storage settle (dataset and log cases, only with INFLUXDB_METRICS_URL set): wait until InfluxDB has
    # flushed its cache and no compaction completed for settle_stable_seconds, at most settle_timeout_seconds.
    settle_timeout_seconds: float = Field(default=900, gt=0)
    settle_stable_seconds: float = Field(default=60, gt=0)

    @model_validator(mode="after")
    def check_drain(self):
        if self.stable_seconds >= self.drain_timeout_seconds:
            raise ValueError("stable_seconds must be less than drain_timeout_seconds")
        if self.settle_stable_seconds >= self.settle_timeout_seconds:
            raise ValueError("settle_stable_seconds must be less than settle_timeout_seconds")
        return self


class Connection(Model):
    url: str | None = None
    username: str | None = None
    password: str | None = None
    url_env: str = "STREAMPIPES_URL"
    username_env: str = "STREAMPIPES_USERNAME"
    password_env: str = "STREAMPIPES_PASSWORD"


class Thresholds(Model):
    max_operation_mean_ms: float | None = Field(default=None, ge=0)
    max_error_rate: float | None = Field(default=None, ge=0, le=1)
    min_throughput_ratio: float | None = Field(default=None, ge=0)


class Cleanup(Model):
    strategy: Literal["delete", "reset"] = "reset"
    reset_timeout_seconds: float = Field(default=600, gt=0)


class Execution(Model):
    timing: Timing = Field(default_factory=Timing)
    cleanup: Cleanup = Field(default_factory=Cleanup)
    repetitions: int = Field(default=1, ge=1)
    thresholds: Thresholds = Field(default_factory=Thresholds)


class ExecutionOverrides(Model):
    timing: Timing | list[Timing] | None = None
    cleanup: Cleanup | list[Cleanup] | None = None
    repetitions: int | None = Field(default=None, ge=1)
    thresholds: Thresholds | None = None


class CaseDocumentation(Model):
    """Documentation snapshot stored alongside measurements for offline viewing."""

    description: str = ""
    parameter_descriptions: dict[str, str] = Field(default_factory=dict)
    metrics: dict[str, str] = Field(default_factory=dict)


class CaseConfig(Model):
    id: str = Field(pattern=r"^[a-zA-Z0-9_-]+$")
    type: str
    parameters: dict[str, Any] = Field(default_factory=dict)
    execution: ExecutionOverrides = Field(default_factory=ExecutionOverrides)


class RunConfigSettings(Model):
    target: str
    connection: Connection = Field(default_factory=Connection)
    output_directory: str = "results/framework"
    execution: Execution = Field(default_factory=Execution)

    @field_validator("target")
    @classmethod
    def registered_target(cls, value):
        from streampipes_performance.targets import target

        target(value)
        return value

    @model_validator(mode="after")
    def default_cleanup_for_target(self):
        from streampipes_performance.targets import target

        if "strategy" not in self.execution.cleanup.model_fields_set:
            self.execution.cleanup.strategy = target(self.target).default_cleanup
        return self


class RunConfig(RunConfigSettings):
    """Complete test run with inline case parameters and execution settings."""

    cases: list[CaseConfig] = Field(min_length=1)


class Scenario(Model):
    id: str
    type: str
    documentation: CaseDocumentation = Field(default_factory=CaseDocumentation)
    repetition: int
    timing: Timing
    cleanup: Cleanup = Field(default_factory=Cleanup)
    parameters: dict[str, Any]
    thresholds: Thresholds


def expand_values(parameters: dict[str, Any]):
    """Expand top-level alternatives; nested objects remain atomic values."""
    dimensions = []
    for name, value in parameters.items():
        if isinstance(value, dict) and set(value) == {"value"}:
            options = [value["value"]]
        elif isinstance(value, dict) and set(value) == {"values"}:
            options = value["values"]
            if not isinstance(options, list):
                raise ValueError(f"{name}: values must be a list")
        else:
            options = value if isinstance(value, list) else [value]
        if not options:
            raise ValueError(f"{name}: alternatives must not be empty")
        dimensions.append(options)
    for values in itertools.product(*dimensions):
        yield dict(zip(parameters, values))


def expand(run_config: RunConfig) -> list[Scenario]:
    from streampipes_performance.cases import REGISTRY
    from streampipes_performance.targets import target

    run_target = target(run_config.target)
    result = []
    seen = set()
    for case in run_config.cases:
        if case.id in seen:
            raise ValueError(f"Duplicate case ID: {case.id}")
        seen.add(case.id)
        if case.type not in REGISTRY:
            raise ValueError(f"Unknown case type: {case.type}")
        case_class = REGISTRY[case.type]
        if not run_target.supports(case_class):
            raise ValueError(f"{case.type} is unavailable on {run_config.target}")
        settings = run_config.execution.model_dump()
        overrides = case.execution.model_dump(exclude_none=True)
        if case.execution.cleanup is not None:
            default = run_target.default_cleanup
            cleanups = case.execution.cleanup
            if isinstance(cleanups, list):
                overrides["cleanup"] = [
                    {**cleanup.model_dump(), "strategy": cleanup.strategy if "strategy" in cleanup.model_fields_set else default}
                    for cleanup in cleanups
                ]
            elif "strategy" not in cleanups.model_fields_set:
                overrides["cleanup"]["strategy"] = default
        settings.update(overrides)
        parameter_options = list(expand_values(case.parameters))
        execution_options = list(expand_values(settings))
        for index, (parameters, execution) in enumerate(
            itertools.product(parameter_options, execution_options), 1
        ):
            params = case_class.Parameters.model_validate(parameters)
            resolved = Execution.model_validate(execution)
            if resolved.cleanup.strategy == "reset" and not run_target.supports_reset:
                raise run_target.reset_unsupported()
            for repetition in range(1, resolved.repetitions + 1):
                result.append(
                    Scenario(
                        id=f"{case.id}-m{index}-r{repetition}",
                        type=case.type,
                        documentation=case_class.documentation.model_copy(deep=True),
                        repetition=repetition,
                        timing=resolved.timing,
                        cleanup=resolved.cleanup,
                        parameters=params.model_dump(),
                        thresholds=resolved.thresholds,
                    )
                )
    return result


def load(path: Path, case_ids=None, overrides=None) -> tuple[RunConfig, list[Scenario]]:
    path = path.resolve()
    run_config = RunConfig.model_validate(json.loads(path.read_text()))
    if case_ids:
        unknown = set(case_ids) - {case.id for case in run_config.cases}
        if unknown:
            raise ValueError(f"Unknown case IDs: {', '.join(sorted(unknown))}")
        run_config.cases = [case for case in run_config.cases if case.id in case_ids]
    selected = {case.id: case for case in run_config.cases}
    for override in overrides or []:
        key, separator, raw = override.partition("=")
        case_id, dot, parameter = key.rpartition(".")
        if not separator or not dot or case_id not in selected:
            raise ValueError("Overrides must use a selected CASE_ID.PARAMETER=JSON")
        try:
            selected[case_id].parameters[parameter] = json.loads(raw)
        except json.JSONDecodeError:
            raise ValueError(f"Override for {key} must be valid JSON") from None
    return run_config, expand(run_config)
