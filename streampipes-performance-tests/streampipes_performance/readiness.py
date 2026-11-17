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

"""Case-aware readiness checks shared by local and container runs."""

import logging
import socket
import time
from urllib.parse import urlsplit

import requests


class NotReady(Exception):
    pass


def required_services(scenarios, settings):
    from streampipes_performance.cases import REGISTRY

    services = {"backend"}
    for kind in {scenario.type for scenario in scenarios}:
        services.update(REGISTRY[kind].services)
    if not settings.influxdb_metrics_url:
        # Storage settling is optional: without a metrics URL the cases query immediately.
        services.discard("influxdb_metrics")
    return services


def check_ready(session, timeout, settings, services):
    api = settings.url.rstrip("/") + "/streampipes-backend/api/v2"
    stage = "backend setup"
    try:
        response = session.get(f"{api}/setup/configured", timeout=timeout())
        response.raise_for_status()
        if response.json().get("configured") is not True:
            raise NotReady(stage)
        stage = "admin login"
        response = session.post(f"{api}/auth/login", json={
            "username": settings.username, "password": settings.password.get_secret_value(),
        }, timeout=timeout())
        response.raise_for_status()
        token = response.json().get("accessToken")
        if not isinstance(token, str) or not token:
            raise NotReady(stage)
        if "extensions" in services:
            stage = "IIoT extensions"
            response = session.get(settings.extensions_url.rstrip("/") + "/health",
                                   headers={"Authorization": f"Bearer {token}"}, timeout=timeout())
            response.raise_for_status()
            health = response.json()
            if not isinstance(health.get("adapterInstanceStates"), dict) or not isinstance(
                    health.get("runningPipelineElementInstanceIds"), list):
                raise NotReady(stage)
        if "influxdb_metrics" in services:
            stage = "InfluxDB metrics"
            response = session.get(settings.influxdb_metrics_url, timeout=timeout())
            response.raise_for_status()
        if "nats" in services:
            stage = "NATS"
            for address in dict.fromkeys((settings.nats_url, settings.output_nats_url or settings.nats_url)):
                broker = urlsplit(address)
                with socket.create_connection((broker.hostname, broker.port or 4222), timeout=timeout()) as connection:
                    with connection.makefile("rb") as stream:
                        if not stream.readline(65536).startswith(b"INFO "):
                            raise NotReady(stage)
    except (requests.RequestException, OSError, ValueError, AttributeError):
        raise NotReady(stage) from None


def wait_ready(settings, scenarios):
    services = required_services(scenarios, settings)
    if "extensions" in services and not settings.extensions_url:
        raise ValueError("Selected cases need --extensions-url for readiness; use --no-wait-ready to skip checks")
    deadline = time.monotonic() + settings.ready_timeout

    def timeout():
        remaining = deadline - time.monotonic()
        if remaining <= 0:
            raise TimeoutError("Software readiness deadline exceeded")
        return min(5, remaining)

    last_stage = None
    with requests.Session() as session:
        while time.monotonic() < deadline:
            try:
                check_ready(session, timeout, settings, services)
                return
            except NotReady as error:
                last_stage = str(error)
                logging.getLogger(__name__).info("Waiting for %s...", last_stage)
                time.sleep(max(0, min(5, deadline - time.monotonic())))
    raise TimeoutError(f"Software was not ready within {settings.ready_timeout:g}s (last check: {last_stage})")
