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

"""Shared direct InfluxDB v2 writes for dataset and log setup."""

import gzip
import os
from http.client import HTTPException
from urllib.error import HTTPError, URLError
from urllib.parse import urlencode, urlsplit
from urllib.request import Request, urlopen

from pydantic import Field, SecretStr, field_validator

from streampipes_performance.config import Model
from streampipes_performance.runtime import EvaluationError


class InfluxConfig(Model):
    url: str = "http://localhost:8086"
    org: str = Field(default="sp", min_length=1)
    bucket: str = Field(default="sp", min_length=1)
    token: SecretStr | None = None
    token_env: str = Field(default="INFLUX_TOKEN", min_length=1)
    timeout_seconds: float = Field(default=60, gt=0)
    gzip_writes: bool = True

    @classmethod
    def from_environment(cls):
        return cls(
            url=os.environ.get("INFLUX_URL", "http://localhost:8086"),
            org=os.environ.get("INFLUX_ORG", "sp"),
            bucket=os.environ.get("INFLUX_BUCKET", "sp"),
        )

    @field_validator("url")
    @classmethod
    def check_url(cls, value):
        url = urlsplit(value)
        if (
            url.scheme not in ("http", "https")
            or not url.hostname
            or url.username
            or url.password
            or url.query
            or url.fragment
            or url.path not in ("", "/")
            or url.port == 0
            or any(char.isspace() for char in value)
        ):
            raise ValueError("influx.url must be an HTTP(S) origin without credentials or a path")
        return value.rstrip("/")


class InfluxUploader:
    def __init__(self, config, tag_fields=()):
        self.config = config
        self.tag_fields = frozenset(tag_fields)
        self.url = config.url + "/api/v2/write?" + urlencode(
            {"org": config.org, "bucket": config.bucket, "precision": "ms"}
        )
        self.token = config.token.get_secret_value() if config.token is not None else os.environ.get(config.token_env)
        if not self.token:
            raise ValueError(f"InfluxDB token is missing. Set {config.token_env}.")

    def upload(self, name, headers, batch):
        body = line_protocol(name, headers, batch, self.tag_fields)
        request_headers = {
            "Authorization": f"Token {self.token}",
            "Content-Type": "text/plain; charset=utf-8",
        }
        if self.config.gzip_writes:
            body = gzip.compress(body, mtime=0)
            request_headers["Content-Encoding"] = "gzip"
        request = Request(self.url, data=body, headers=request_headers, method="POST")
        try:
            with urlopen(request, timeout=self.config.timeout_seconds) as response:
                response.read()
        except HTTPError as error:
            raise EvaluationError(f"influx_upload_http_error: status={error.code}") from error
        except URLError as error:
            # Do not include arbitrary server/proxy messages in reports.
            reason = error.reason
            raise EvaluationError(
                f"influx_connection_failed: reason={type(reason).__name__}, "
                f"errno={getattr(reason, 'errno', None)}; "
                "check influx.url and publish the InfluxDB port when running in Docker"
            ) from error
        except (OSError, HTTPException) as error:
            raise EvaluationError(f"influx_connection_failed: reason={type(error).__name__}") from error


def escape_key(value):
    return str(value).replace("\\", "\\\\").replace(",", "\\,").replace("=", "\\=").replace(" ", "\\ ")


def field_value(value):
    if isinstance(value, bool):
        return str(value).lower()
    if isinstance(value, int):
        return f"{value}i"
    if isinstance(value, float):
        return repr(value)
    return '"' + str(value).replace("\\", "\\\\").replace('"', '\\"') + '"'


def line_protocol(name, headers, batch, tag_fields=()):
    """Encode schema dimensions as tags and preserve primitive field types."""
    measurement = name.replace(",", "\\,").replace(" ", "\\ ")
    lines = []
    for row in batch:
        event = dict(zip(headers, row))
        timestamp = event.pop("timestamp")
        tags = "".join(
            f",{escape_key(key)}={escape_key(value)}"
            for key, value in event.items() if key in tag_fields
        )
        fields = ",".join(
            f"{escape_key(key)}={field_value(value)}"
            for key, value in event.items() if key not in tag_fields
        )
        lines.append(f"{measurement}{tags} {fields} {timestamp}\n")
    return "".join(lines).encode("utf-8")
