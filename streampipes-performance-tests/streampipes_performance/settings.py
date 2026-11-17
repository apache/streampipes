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

"""Resolve deployment settings once: CLI > environment > settings file > run config."""

import json
import os
from pathlib import Path
from typing import Literal
from urllib.parse import urlsplit

from pydantic import ConfigDict, Field, SecretStr, field_validator

from streampipes_performance.config import Model

ENVIRONMENT = {
    "url": "STREAMPIPES_URL", "username": "STREAMPIPES_USERNAME", "password": "STREAMPIPES_PASSWORD",
    "nats_url": "NATS_URL", "output_nats_url": "OUTPUT_NATS_URL",
    "adapter_nats_url": "ADAPTER_NATS_URL", "stream_nats_url": "STREAM_NATS_URL",
    "influx_url": "INFLUX_URL", "influx_org": "INFLUX_ORG", "influx_bucket": "INFLUX_BUCKET",
    "influx_token": "INFLUX_TOKEN", "influxdb_metrics_url": "INFLUXDB_METRICS_URL",
    "extensions_url": "EXTENSIONS_URL", "ready_timeout": "PERFORMANCE_READY_TIMEOUT",
    "wait_ready": "PERFORMANCE_WAIT_READY", "output_directory": "PERFORMANCE_OUTPUT_DIRECTORY",
    "log_level": "PERFORMANCE_LOG_LEVEL", "email_to": "PERFORMANCE_EMAIL_TO",
    "email_from": "PERFORMANCE_EMAIL_FROM", "smtp_host": "PERFORMANCE_SMTP_HOST",
    "smtp_port": "PERFORMANCE_SMTP_PORT", "smtp_username": "PERFORMANCE_SMTP_USERNAME",
    "smtp_password": "PERFORMANCE_SMTP_PASSWORD", "smtp_security": "PERFORMANCE_SMTP_SECURITY",
    "smtp_timeout": "PERFORMANCE_SMTP_TIMEOUT",
}
SECRET_FIELDS = ("password", "influx_token", "smtp_password")


class RuntimeSettings(Model):
    model_config = ConfigDict(extra="forbid", allow_inf_nan=False, hide_input_in_errors=True)
    url: str | None = None
    username: str | None = None
    password: SecretStr | None = None
    nats_url: str = "nats://localhost:4222"
    output_nats_url: str | None = None
    adapter_nats_url: str = "nats://nats:4222"
    stream_nats_url: str = "nats://nats:4222"
    influx_url: str = "http://localhost:8086"
    influx_org: str = "sp"
    influx_bucket: str = "sp"
    influx_token: SecretStr | None = None
    influxdb_metrics_url: str | None = None
    extensions_url: str | None = None
    ready_timeout: float = Field(default=600, gt=0)
    wait_ready: bool = True
    output_directory: str | None = None
    log_level: Literal["DEBUG", "INFO", "WARNING", "ERROR"] = "INFO"
    email_to: list[str] = Field(default_factory=list)
    email_from: str | None = None
    smtp_host: str | None = None
    smtp_port: int = Field(default=587, ge=1, le=65535)
    smtp_username: str | None = None
    smtp_password: SecretStr | None = None
    smtp_security: Literal["starttls", "ssl", "none"] = "starttls"
    smtp_timeout: float = Field(default=30, gt=0)

    @field_validator("output_nats_url", "influxdb_metrics_url", "extensions_url", mode="before")
    @classmethod
    def blank_is_none(cls, value):
        return value or None

    @field_validator("email_to", mode="before")
    @classmethod
    def recipient_list(cls, value):
        if isinstance(value, str):
            return [address.strip() for address in value.split(",") if address.strip()]
        return value

    @field_validator("email_to", "email_from")
    @classmethod
    def addresses(cls, value):
        for address in (value if isinstance(value, list) else [value] if value else []):
            if any(c.isspace() for c in address) or address.count("@") != 1 or not all(address.split("@")):
                raise ValueError("Use a plain email address, without a display name or whitespace")
        return value

    @field_validator("url", "influx_url", "influxdb_metrics_url", "extensions_url")
    @classmethod
    def http_url(cls, value, info):
        if value is None:
            return value
        url = urlsplit(value)
        if (url.scheme not in ("http", "https") or not url.hostname or url.username is not None
                or url.password is not None or url.fragment or url.query
                or any(c.isspace() for c in value) or url.port == 0):
            raise ValueError("Expected an HTTP(S) URL without credentials, query or fragment")
        if info.field_name in ("url", "influx_url") and url.path not in ("", "/"):
            raise ValueError("Expected an HTTP(S) origin without a path")
        return value.rstrip("/")

    @field_validator("nats_url", "output_nats_url", "adapter_nats_url", "stream_nats_url")
    @classmethod
    def nats_address(cls, value):
        if value is None:
            return value
        url = urlsplit(value)
        if (url.scheme != "nats" or not url.hostname or url.username is not None or url.password is not None
                or url.path not in ("", "/") or url.query or url.fragment or url.port == 0
                or any(c.isspace() for c in value)):
            raise ValueError("Expected an anonymous nats://host:port broker URL")
        return value

    def require_run_credentials(self):
        if not self.url or not self.username or not self.password or not self.password.get_secret_value():
            raise ValueError("Run requires url, username and password (CLI, environment, settings or run config)")

    def require_email(self):
        if not self.email_to or not self.email_from or not self.smtp_host:
            raise ValueError("Email delivery requires email_to, email_from and smtp_host")
        if self.smtp_username and not self.smtp_password:
            raise ValueError("SMTP authentication requires smtp_password")

    def redacted(self):
        return self.model_dump(mode="json", exclude=set(SECRET_FIELDS))


def _layer(values, base):
    """Resolve file references in their own layer, without reading shadowed lower-layer secrets."""
    values = dict(values)
    for name in SECRET_FIELDS:
        file_key = name + "_file"
        if file_key in values:
            if name in values:
                raise ValueError(f"Specify only one of {name} and {file_key} in the same source")
            values[name] = ("file", base / values.pop(file_key))
    if values.get("output_directory"):
        values["output_directory"] = str((base / values["output_directory"]).resolve())
    return values


def resolve_settings(connection=None, config_path=None, output_directory=None, settings_path=None,
                     overrides=None, environ=None, secret_fields=SECRET_FIELDS):
    env = os.environ if environ is None else environ
    values = {}
    if connection is not None:
        values.update({key: getattr(connection, key) for key in ("url", "username", "password")
                       if getattr(connection, key, None) is not None})
    if output_directory is not None:
        base = Path(config_path).resolve().parent if config_path else Path.cwd()
        values["output_directory"] = str((base / output_directory).resolve())
    settings_path = settings_path or env.get("PERFORMANCE_SETTINGS")
    if settings_path:
        path = Path(settings_path).resolve()
        values.update(_layer(json.loads(path.read_text()), path.parent))
    env_names = dict(ENVIRONMENT)
    if connection is not None:
        for name in ("url", "username", "password"):
            env_names[name] = getattr(connection, name + "_env")
    layer = {key: env[name] for key, name in env_names.items() if name in env}
    for key in SECRET_FIELDS:
        name = env_names[key] + "_FILE"
        if name in env:
            layer.pop(key, None)
            layer[key + "_file"] = env[name]
    values.update(_layer(layer, Path.cwd()))
    values.update(_layer({k: v for k, v in (overrides or {}).items() if v is not None}, Path.cwd()))
    for key in SECRET_FIELDS:
        if key not in secret_fields:
            values.pop(key, None)
            continue
        value = values.get(key)
        if isinstance(value, tuple):
            values[key] = value[1].read_text().rstrip("\r\n")
    return RuntimeSettings.model_validate(values)
