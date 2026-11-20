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

"""Environment-only target selection for CLI runs."""

import pytest

from streampipes_performance.cli import connection_url


@pytest.mark.parametrize("value", [None, "", "localhost:8030", "ftp://host", "http://host/path",
                                   "http://user:secret@host", "http://host?x=1", "http://host#fragment",
                                   "http://host:invalid", "http://host:65536", "http://host:0", "http://bad host"])
def test_invalid_url(value, monkeypatch):
    if value is None:
        monkeypatch.delenv("STREAMPIPES_URL", raising=False)
    else:
        monkeypatch.setenv("STREAMPIPES_URL", value)
    with pytest.raises(ValueError, match="STREAMPIPES_URL"):
        connection_url()


@pytest.mark.parametrize("value,host,port", [
    ("http://localhost:8030", "localhost", 8030),
    ("https://perf.example/", "perf.example", None),
    ("http://[::1]:8080", "::1", 8080),
])
def test_valid_url(value, host, port, monkeypatch):
    monkeypatch.setenv("STREAMPIPES_URL", value)
    url = connection_url()
    assert url.hostname == host
    assert url.port == port


def test_custom_url_variable_does_not_fall_back(monkeypatch):
    monkeypatch.setenv("STREAMPIPES_URL", "http://localhost:8030")
    monkeypatch.delenv("CUSTOM_STREAMPIPES_URL", raising=False)
    with pytest.raises(ValueError, match="CUSTOM_STREAMPIPES_URL must be set"):
        connection_url("CUSTOM_STREAMPIPES_URL")
    monkeypatch.setenv("CUSTOM_STREAMPIPES_URL", "invalid")
    with pytest.raises(ValueError, match="CUSTOM_STREAMPIPES_URL must be an HTTP"):
        connection_url("CUSTOM_STREAMPIPES_URL")
