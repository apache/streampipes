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

"""NATS latency correlation and complete case lifecycles without external services."""

import asyncio
import json
from types import SimpleNamespace
from unittest.mock import Mock

import pytest


from streampipes_performance.cases.latency import Collector, BrokerSettings, output_subject
from streampipes_performance.cases.streampipes.adapter_latency.case import AdapterLatency
from streampipes_performance.clients.streampipes import StreamEndpoint
from streampipes_performance.config import Cleanup, Timing
from streampipes_performance.runtime import Context, EvaluationError
from streampipes_performance.testing import Broker, Clock, Server


def make_case(case_type, parameters):
    parameters = dict(parameters)
    warmup = parameters.pop("warmup_events", 0)
    interval = parameters.get("publish_interval_ms", 0)
    parameters["publish_interval_ms"] = max(interval, 0.001)
    case = case_type(parameters)
    case.latency.warmup_events = warmup
    case.latency.parameters.publish_interval_ms = interval
    return case


def test_correlation_out_of_order_duplicates_and_invalid_data():
    clock = Mock(side_effect=[1.03, 1.04, 1.05, 1.06, 1.07])
    collector = Collector(clock)
    collector.sent = {'first': 1.0, 'second': 1.01}

    async def receive():
        for payload in [b'{"eventId":"second"}', b'{"eventId":"first"}',
                        b'{"eventId":"first"}', b'{"eventId":"unknown"}', b'[]']:
            await collector.receive(SimpleNamespace(data=payload))

    asyncio.run(receive())
    assert collector.received == pytest.approx({'second': 20, 'first': 40})
    assert collector.duplicates == 1
    assert collector.unmatched == 1
    assert collector.malformed == 1


@pytest.fixture
def environment(monkeypatch):
    import nats

    broker = Broker()
    monkeypatch.setattr(nats, 'connect', broker.connect)
    server = Server()
    streams = {}
    input_streams = {}
    server.streamApi = StreamEndpoint(parent_client=server)
    original_create = server.create_adapter

    def create_adapter(payload):
        config = {key: value for entry in payload.configuration for key, value in entry.items()}
        assert config['format'] == 'Json'
        assert config['json_options'] == 'object'
        assert config['access-mode'] == 'anonymous-alternative'
        assert config['natsUrls'] == 'nats://nats:4222'
        result = original_create(payload)
        stream = 'stream-' + payload.id
        topic = 'adapter-output-' + payload.name
        streams[stream] = (config['subject'], topic)
        broker.routes[config['subject']] = topic
        return result

    server.adapterApi.create_result.side_effect = create_adapter
    def request(method, path, **kwargs):
        if path == 'api/v2/streams':
            if method == 'GET':
                return list(input_streams.values())
            payload = kwargs['json']
            protocol = payload['eventGrounding']['transportProtocols'][0]
            assert protocol['brokerHostname'] == 'nats'
            assert protocol['port'] == 4222
            assert {p['runtimeName'] for p in payload['eventSchema']['eventProperties']} == {
                'eventId', 'timestamp', 'value'
            }
            topic = protocol['topicDefinition']['actualTopicName']
            streams[payload['elementId']] = (topic, topic)
            input_streams[payload['elementId']] = payload
            return None
        if path.startswith('api/v2/streams/'):
            from urllib.parse import unquote
            stream = unquote(path.removeprefix('api/v2/streams/'))
            if method == 'DELETE':
                del input_streams[stream]
                del streams[stream]
                return None
            _, topic = streams[stream]
            return {'eventGrounding': {'transportProtocols': [{
                '@class': 'org.apache.streampipes.model.grounding.NatsTransportProtocol',
                'topicDefinition': {'actualTopicName': topic},
            }]}}
        raise NotImplementedError(f'{method} {path}')

    server.request_json = request
    timing = Timing(poll_seconds=0.001, readiness_timeout_seconds=0.02,
                    drain_timeout_seconds=0.02, stable_seconds=0.001)
    context = Context(server, timing, 'latency-test', clock=Clock(), cleanup_config=Cleanup(strategy="delete"))
    return context, broker, streams


def test_complete_case_resources_topics_and_metrics(environment):
    context, broker, _ = environment
    case = make_case(AdapterLatency, {'number_of_adapters': 2, 'events_per_resource': 3, 'warmup_events': 1, 'publish_interval_ms': 0})
    case.setup(context)
    assert all(client.closed for client in broker.connections)
    assert len(case.latency.outputs) == 2
    assert all(topic.startswith('adapter-output-') for topic in case.latency.outputs)
    case.run(context)
    metrics = case.prepare_results(context)
    assert metrics['collection_complete']
    assert metrics['expected_events'] == metrics['observed_events'] == metrics['sent_events'] == 6
    assert metrics['missing_events'] == metrics['duplicate_events'] == 0
    assert metrics['processing_latency_ms']['count'] == 6
    assert all(sample['event_id'].startswith('latency-test-') for sample in metrics['event_latencies'])
    assert all(sample['latency_ms'] >= 0 for sample in metrics['event_latencies'])
    elapsed = [sample['elapsed_ms'] for sample in metrics['event_latencies']]
    assert elapsed == sorted(elapsed)
    assert all(value >= 0 for value in elapsed)
    assert case.cleanup(context)
    assert context.client.empty()
    assert context.client.streamApi.all() == []
    assert all(client.closed for client in broker.connections)


@pytest.mark.parametrize('failure', ['drop', 'duplicate'])
def test_delivery_failure_retains_partial_metrics_and_closes_connections(environment, failure):
    context, broker, _ = environment
    case = make_case(AdapterLatency,{'events_per_resource': 2, 'warmup_events': 0, 'publish_interval_ms': 0})
    case.setup(context)
    setattr(broker, failure, True)
    with pytest.raises(EvaluationError, match='latency_(missing|invalid_output)_events'):
        case.run(context)
    metrics = case.prepare_results(context)
    assert metrics['sent_events'] == 2
    assert metrics['missing_events'] == (2 if failure == 'drop' else 0)
    assert metrics['duplicate_events'] == (2 if failure == 'duplicate' else 0)
    assert all(client.closed for client in broker.connections)
    assert case.cleanup(context)


def test_resource_creation_failure_closes_connections_and_cleans_resources(environment, monkeypatch):
    context, broker, _ = environment
    case = make_case(AdapterLatency, {"number_of_adapters": 2, "warmup_events": 0})
    api, method = context.client.adapterApi, "create_result"
    create = getattr(api, method)
    created = 0

    def create_then_fail(payload):
        nonlocal created
        result = create(payload)
        created += 1
        if created == 2:
            raise TimeoutError("creation response lost")
        return result

    monkeypatch.setattr(api, method, create_then_fail)
    with pytest.raises(TimeoutError):
        case.setup(context)

    assert created == 2
    assert not case.prepare_results(context)["collection_complete"]
    assert all(client.closed for client in broker.connections)
    assert case.cleanup(context)
    assert context.client.empty()
    assert context.client.streamApi.all() == []


def test_readiness_timeout_cleans_resources(environment):
    context, broker, _ = environment
    broker.drop = True
    case = make_case(AdapterLatency, {'warmup_events': 0})
    with pytest.raises(EvaluationError, match='latency_readiness_timeout'):
        case.setup(context)
    assert not case.prepare_results(context)['collection_complete']
    assert all(client.closed for client in broker.connections)
    assert case.cleanup(context)
    assert context.client.streamApi.all() == []


def test_non_nats_output_fails_clearly(environment):
    context, _, _ = environment
    context.client.request_json = Mock(return_value={'eventGrounding': {'transportProtocol': {
        '@class': 'org.apache.streampipes.model.grounding.KafkaTransportProtocol'}}})
    with pytest.raises(EvaluationError, match='latency_requires_nats_output_transport'):
        output_subject(context, 'stream')


@pytest.mark.parametrize('parameters', [
    {'events_per_resource': 0}, {'publish_interval_ms': -1}, {'warmup_events': -1},
    {'nats_url': 'nats://user:password@localhost:4222'}, {'adapter_nats_url': 'http://localhost:4222'},
])
def test_invalid_parameters(parameters):
    with pytest.raises(ValueError):
        if "nats_url" in parameters:
            BrokerSettings(**parameters)
        else:
            AdapterLatency(parameters)


def test_publisher_failure_keeps_received_events_and_closes_clients(environment):
    context, broker, _ = environment
    case = make_case(AdapterLatency,{'events_per_resource': 3, 'warmup_events': 0, 'publish_interval_ms': 0})
    case.setup(context)
    connect = broker.connect

    async def failing_connect(*args, **kwargs):
        client = await connect(*args, **kwargs)
        publish = client.publish

        async def fail_second(subject, payload):
            if json.loads(payload)['eventId'].endswith('-1'):
                raise OSError('publish failed')
            await publish(subject, payload)

        client.publish = fail_second
        return client

    import nats
    from unittest.mock import patch
    with patch.object(nats, 'connect', failing_connect), pytest.raises(OSError):
        case.run(context)
    metrics = case.prepare_results(context)
    assert metrics['sent_events'] == 2
    assert metrics['observed_events'] == 1
    assert metrics['missing_events'] == 1
    assert not metrics['collection_complete']
    assert all(client.closed for client in broker.connections)
    assert case.cleanup(context)


def test_output_connection_failure_closes_publisher(environment, monkeypatch):
    import nats
    from streampipes_performance.cases.latency import connections

    _, broker, _ = environment

    async def fail_output(url, **kwargs):
        if broker.connections:
            raise OSError('output broker unreachable')
        return await broker.connect(url, **kwargs)

    monkeypatch.setattr(nats, 'connect', fail_output)

    async def connect():
        async with connections(BrokerSettings(), 1):
            pytest.fail('both connections must succeed')

    with pytest.raises(OSError):
        asyncio.run(connect())
    assert len(broker.connections) == 1
    assert broker.connections[0].closed


def test_comparison_configs_forward_adapter_scripts_and_keep_workload_equal():
    from pathlib import Path
    from streampipes_performance.config import load
    from streampipes_performance.cases.streampipes.adapter_latency.case import nats_adapter

    root = Path(__file__).parents[1]
    for run_config_name, expected_count in [('adapter-latency', 6)]:
        _, scenarios = load(root / f'run_configs/{run_config_name}.json')
        assert len(scenarios) == expected_count
        workloads = []
        for scenario in scenarios:
            parameters = dict(scenario.parameters)
            parameters.pop('code', None)
            transformation = parameters.pop('transformation', None)
            workloads.append(parameters)
            adapter_parameters = AdapterLatency.Parameters(**parameters, transformation=transformation)
            payload = nats_adapter('id', 'name', 'subject', adapter_parameters, 'nats://nats:4222')
            configured = payload.model_dump(by_alias=True)['transformationConfig']
            if transformation:
                assert configured['script'] == transformation['script']
                assert configured['scriptActive'] is True
            else:
                assert configured == {'scriptActive': False, 'script': ''}
        count_key = "number_of_adapters"
        for count in {parameters[count_key] for parameters in workloads}:
            group = [parameters for parameters in workloads if parameters[count_key] == count]
            assert len(group) == 3
            assert all(parameters == group[0] for parameters in group)


@pytest.mark.parametrize("variable", ["NATS_URL", "OUTPUT_NATS_URL", "ADAPTER_NATS_URL"])
def test_broker_environment_rejects_invalid_urls(monkeypatch, variable):
    monkeypatch.setenv(variable, "http://invalid-broker")
    with pytest.raises(ValueError):
        AdapterLatency({})
