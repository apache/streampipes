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

"""Direct writes, schema encoding and transparent upload fallback."""

import gzip
from http.client import IncompleteRead
from types import SimpleNamespace
from urllib.error import HTTPError, URLError
from unittest.mock import Mock

import pytest

from streampipes_performance.cases.common import upload_rows
from streampipes_performance.cases.influx import InfluxConfig, InfluxUploader, line_protocol
from streampipes_performance.cases.streampipes.dataset_query.case import HEADERS, rows
from streampipes_performance.config import Timing
from streampipes_performance.runtime import Context, EvaluationError


@pytest.fixture
def context(monkeypatch):
    for key in ('INFLUX_TOKEN', 'INFLUX_URL', 'INFLUX_ORG', 'INFLUX_BUCKET'):
        monkeypatch.delenv(key, raising=False)
    client = SimpleNamespace(datasetApi=Mock())
    return Context(client, Timing(), 'test-influx')


def test_line_protocol_preserves_dimensions_and_types():
    assert line_protocol('test data', ['timestamp', 'assetId', 'title', 'good', 'value', 'fault'],
                         [[1704067200000, 'a,b=c d', 'say "hi"\\end', 1, 1.5, False]],
                         ('assetId',)) == (
        b'test\\ data,assetId=a\\,b\\=c\\ d title="say \\"hi\\"\\\\end",good=1i,value=1.5,fault=false 1704067200000\n'
    )
    payload = line_protocol('flowrate', HEADERS, list(rows(0, 1)), ('sensorId',))
    assert b',sensorId=flowrate00 ' in payload
    assert b'sensor_fault_flags=false' in payload
    assert b'mass_flow=0.0' in payload
    assert payload.endswith(b' 1704067200000\n')


def test_direct_write_request(monkeypatch):
    monkeypatch.setenv('INFLUX_TOKEN', 'test-token')
    response = Mock()
    response.__enter__ = Mock(return_value=response)
    response.__exit__ = Mock(return_value=False)
    send = Mock(return_value=response)
    monkeypatch.setattr('streampipes_performance.cases.influx.urlopen', send)
    config = InfluxConfig(url='http://influxdb:8086', org='test org', bucket='data')
    uploader = InfluxUploader(config, ('sensorId',))
    batch = list(rows(0, 2))
    uploader.upload('data', HEADERS, batch)
    request = send.call_args.args[0]
    assert request.full_url == 'http://influxdb:8086/api/v2/write?org=test+org&bucket=data&precision=ms'
    assert request.method == 'POST'
    assert request.get_header('Authorization') == 'Token test-token'
    assert request.get_header('Content-encoding') == 'gzip'
    assert gzip.decompress(request.data) == line_protocol('data', HEADERS, batch, ('sensorId',))
    assert send.call_args.kwargs['timeout'] == 60


@pytest.mark.parametrize('error', [
    HTTPError('http://influxdb:8086', 401, 'private message', {}, None),
    URLError('private message'), TimeoutError('private message'), IncompleteRead(b'', 1),
])
def test_write_errors_are_sanitized(monkeypatch, error):
    monkeypatch.setenv('INFLUX_TOKEN', 'test-token')
    monkeypatch.setattr('streampipes_performance.cases.influx.urlopen', Mock(side_effect=error))
    with pytest.raises(EvaluationError) as result:
        InfluxUploader(InfluxConfig(), ('sensorId',)).upload('data', HEADERS, list(rows(0, 1)))
    assert 'private message' not in str(result.value)
    assert 'test-token' not in str(result.value)


@pytest.mark.parametrize('token', [None, '', '  '])
def test_missing_token_uses_streampipes(context, monkeypatch, token):
    if token is not None:
        monkeypatch.setenv('INFLUX_TOKEN', token)
    direct = Mock()
    monkeypatch.setattr('streampipes_performance.cases.common.InfluxUploader', direct)
    upload_rows(context, 'data', HEADERS, 3, 2, rows, tag_fields=('sensorId',))
    direct.assert_not_called()
    assert [call.args[2] for call in context.client.datasetApi.upload.call_args_list] == [
        list(rows(0, 2)), list(rows(2, 3)),
    ]


def test_configured_token_prefers_influx(context, monkeypatch):
    monkeypatch.setenv('INFLUX_TOKEN', 'test-token')
    monkeypatch.setenv('INFLUX_URL', 'http://influxdb:8086')
    monkeypatch.setenv('INFLUX_ORG', 'test-org')
    monkeypatch.setenv('INFLUX_BUCKET', 'test-bucket')
    direct = Mock()
    factory = Mock(return_value=direct)
    monkeypatch.setattr('streampipes_performance.cases.common.InfluxUploader', factory)
    upload_rows(context, 'data', HEADERS, 3, 2, rows, tag_fields=('sensorId',))
    config, tags = factory.call_args.args
    assert (config.url, config.org, config.bucket, tags) == ('http://influxdb:8086', 'test-org', 'test-bucket', ('sensorId',))
    assert direct.upload.call_count == 2
    context.client.datasetApi.upload.assert_not_called()


def test_failed_batch_falls_back_without_replaying_successful_batches(context, monkeypatch):
    monkeypatch.setenv('INFLUX_TOKEN', 'test-token')
    direct = Mock()
    direct.upload.side_effect = [None, EvaluationError('influx_upload_http_error: status=500')]
    monkeypatch.setattr('streampipes_performance.cases.common.InfluxUploader', Mock(return_value=direct))
    upload_rows(context, 'data', HEADERS, 6, 2, rows, tag_fields=('sensorId',))
    assert direct.upload.call_count == 2
    assert [call.args[2] for call in context.client.datasetApi.upload.call_args_list] == [
        list(rows(2, 4)), list(rows(4, 6)),
    ]
    assert all(sample['success'] for sample in context.samples)


def test_failure_of_both_uploaders_propagates(context, monkeypatch):
    monkeypatch.setenv('INFLUX_TOKEN', 'test-token')
    direct = Mock()
    direct.upload.side_effect = EvaluationError('influx unavailable')
    monkeypatch.setattr('streampipes_performance.cases.common.InfluxUploader', Mock(return_value=direct))
    context.client.datasetApi.upload.side_effect = RuntimeError('streampipes unavailable')
    with pytest.raises(RuntimeError, match='streampipes unavailable'):
        upload_rows(context, 'data', HEADERS, 2, 2, rows, tag_fields=('sensorId',))
    assert context.samples[-1]['success'] is False


def test_invalid_influx_config_falls_back(context, monkeypatch):
    monkeypatch.setenv('INFLUX_TOKEN', 'test-token')
    monkeypatch.setenv('INFLUX_URL', 'invalid')
    upload_rows(context, 'data', HEADERS, 2, 2, rows, tag_fields=('sensorId',))
    assert context.client.datasetApi.upload.call_count == 1
