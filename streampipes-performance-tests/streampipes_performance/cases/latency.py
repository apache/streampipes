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

"""Correlated NATS send-to-receive workloads shared by adapter and AI cases."""

import asyncio
import json
import time
from contextlib import AsyncExitStack, asynccontextmanager
from urllib.parse import quote, urlsplit

from pydantic import field_validator

from streampipes_performance.config import Model
from streampipes_performance.metrics import summarize
from streampipes_performance.runtime import EvaluationError


class BrokerSettings(Model):
    nats_url: str = "nats://localhost:4222"
    output_nats_url: str | None = None

    @field_validator("nats_url", "output_nats_url")
    @classmethod
    def broker_url(cls, value):
        return validate_broker_url(value)


def validate_broker_url(value):
    if value is None:
        return value
    url = urlsplit(value)
    if (url.scheme != "nats" or not url.hostname or url.username or url.password
            or url.path not in ("", "/") or url.query or url.fragment):
        raise ValueError("Expected an anonymous nats://host:port broker URL")
    if url.port is not None and not 1 <= url.port <= 65535:
        raise ValueError("Invalid NATS port")
    return value


def output_subject(context, stream):
    metadata = context.call("stream.get", context.client.request_json,
                            "GET", f"api/v2/streams/{quote(stream, safe='')}")
    grounding = metadata.get("eventGrounding", {})
    protocols = grounding.get("transportProtocols", [])
    protocol = protocols[0] if protocols else grounding.get("transportProtocol", {})
    if not protocol.get("@class", "").endswith(".NatsTransportProtocol"):
        raise EvaluationError("latency_requires_nats_output_transport")
    subject = protocol.get("topicDefinition", {}).get("actualTopicName")
    if not isinstance(subject, str) or not subject or any(c.isspace() or c in "*>" for c in subject):
        raise EvaluationError("latency_invalid_output_subject")
    return subject


class Collector:
    """Only the event-loop thread accesses correlation state; first arrivals win."""

    def __init__(self, clock=time.perf_counter):
        self.clock = clock
        self.sent = {}
        self.received = {}
        self.duplicates = 0
        self.unmatched = 0
        self.malformed = 0

    async def receive(self, message):
        received_at = self.clock()
        try:
            event = json.loads(message.data)
            identifier = event.get("eventId") if isinstance(event, dict) else None
            if not isinstance(identifier, str):
                raise ValueError("Missing correlation ID")
        except (ValueError, UnicodeDecodeError):
            self.malformed += 1
            return
        if identifier not in self.sent:
            self.unmatched += 1
        elif identifier in self.received:
            self.duplicates += 1
        else:
            self.received[identifier] = (received_at - self.sent[identifier]) * 1000


@asynccontextmanager
async def connections(parameters, timeout):
    import nats

    clients = []
    errors = []

    async def on_error(error):
        errors.append(type(error).__name__)

    async with AsyncExitStack() as stack:
        for url in (parameters.nats_url, parameters.output_nats_url or parameters.nats_url):
            client = await nats.connect(url, connect_timeout=timeout, allow_reconnect=False,
                                        error_cb=on_error)
            stack.push_async_callback(client.close)
            clients.append(client)
        yield clients[0], clients[1], errors


class LatencyMeasurement:
    """Own NATS connections and correlation state using supplied event rules.

    The factory receives the context and input subjects and returns output
    subjects. Resource creation runs in a worker thread while optional schema
    samples continue to be published on the event loop.
    """

    def __init__(self, parameters, brokers, *, resource_count, event_factory, expects_output, warmup_events):
        self.parameters = parameters
        self.resource_count = resource_count
        self.brokers = brokers
        self.event_factory = event_factory
        self.expects_output = expects_output
        self.warmup_events = warmup_events
        self.expected_ids = set()
        self.collector = Collector()
        self.sources = []
        self.outputs = []
        self.measurement_started = False

    async def create_resources(self, context, factory, publisher, requires_schema_samples):
        stop = asyncio.Event()
        timeout = context.timing.request_timeout_seconds
        seeder = None
        if requires_schema_samples:
            seeder = asyncio.create_task(self.seed_schema(publisher, stop, timeout))
        try:
            # Keep NATS I/O alive while the synchronous REST client creates resources.
            self.outputs = await asyncio.to_thread(factory, context, self.sources)
        finally:
            stop.set()
            if seeder is not None:
                await seeder

    async def seed_schema(self, publisher, stop, timeout):
        # Compact creation listens for sample events to infer the schema.
        while not stop.is_set():
            for subject in self.sources:
                await publisher.publish(subject, self.event_factory("schema-sample"))
            await publisher.flush(timeout=timeout)
            try:
                await asyncio.wait_for(stop.wait(), 0.1)
            except asyncio.TimeoutError:
                pass

    async def subscribe_outputs(self, subscriber, timeout):
        for subject in self.outputs:
            await subscriber.subscribe(subject, cb=self.collector.receive)
        await subscriber.flush(timeout=timeout)

    async def probe_outputs(self, context, publisher):
        # Retry distinct probe IDs: Core NATS does not retain events before subscription.
        deadline = time.perf_counter() + context.timing.readiness_timeout_seconds
        for index, source in enumerate(self.sources):
            attempt = 0
            while True:
                identifier = f"probe-{index}-{attempt}"
                self.collector.sent[identifier] = time.perf_counter()
                await publisher.publish(source, self.event_factory(identifier))
                await publisher.flush(timeout=context.timing.request_timeout_seconds)
                await asyncio.sleep(min(context.timing.poll_seconds, max(0, deadline - time.perf_counter())))
                if any(key.startswith(f"probe-{index}-") for key in self.collector.received):
                    break
                if time.perf_counter() >= deadline:
                    raise EvaluationError("latency_readiness_timeout: no correlated output event")
                attempt += 1

    async def warmup(self, context, publisher):
        for index in range(self.warmup_events):
            for resource, source in enumerate(self.sources):
                identifier = f"warmup-{resource}-{index}"
                self.collector.sent[identifier] = time.perf_counter()
                await publisher.publish(source, self.event_factory(identifier))
            await asyncio.sleep(self.parameters.publish_interval_ms / 1000)
        await publisher.flush(timeout=context.timing.request_timeout_seconds)
        warmup_ids = {key for key in self.collector.sent if key.startswith("warmup-")}
        deadline = time.perf_counter() + context.timing.drain_timeout_seconds
        while not warmup_ids.issubset(self.collector.received):
            if time.perf_counter() >= deadline:
                raise EvaluationError("latency_warmup_timeout")
            await asyncio.sleep(0.01)

    async def publish_events(self, context, publisher, timeout):
        self.measurement_started = True
        self.measurement_start = time.perf_counter()
        context.log("Measuring %d events per resource across %d resources", self.parameters.events_per_resource,
                    self.resource_count)
        for index in range(self.parameters.events_per_resource):
            for resource, source in enumerate(self.sources):
                identifier = f"{context.prefix}-{resource}-{index}"
                expected = self.expects_output(index)
                payload = self.event_factory(identifier, index)
                if expected:
                    self.expected_ids.add(identifier)
                self.collector.sent[identifier] = time.perf_counter()
                await publisher.publish(source, payload)
            # Yield even with interval=0 so receiving cannot be starved by the publisher.
            await asyncio.sleep(self.parameters.publish_interval_ms / 1000)
        await publisher.flush(timeout=timeout)

    async def wait_for_outputs(self, context):
        deadline = time.perf_counter() + context.timing.drain_timeout_seconds
        while not self.expected_ids.issubset(self.collector.received) and time.perf_counter() < deadline:
            await asyncio.sleep(min(0.01, max(0, deadline - time.perf_counter())))
        # A short quiet window also detects duplicate outputs arriving after the final event.
        await asyncio.sleep(context.timing.stable_seconds)

    def validate_outputs(self):
        if not self.expected_ids.issubset(self.collector.received):
            raise EvaluationError("latency_missing_events")
        if set(self.collector.received) - self.expected_ids:
            raise EvaluationError("latency_unexpected_output_events")
        if self.collector.duplicates or self.collector.malformed:
            raise EvaluationError("latency_invalid_output_events")

    def prepare_results(self, context):
        collector = self.collector
        input_count = self.resource_count * self.parameters.events_per_resource
        expected = self.resource_count * sum(
            self.expects_output(index) for index in range(self.parameters.events_per_resource)
        )
        sent = collector.sent if self.measurement_started else {}
        received = set(collector.received) if self.measurement_started else set()
        samples = [
            {
                "event_id": identifier,
                "latency_ms": collector.received.get(identifier),
                "elapsed_ms": (sent[identifier] - self.measurement_start) * 1000,
            }
            for identifier in sent if identifier in self.expected_ids
        ]
        filtered_ids = [identifier for identifier in sent if identifier not in self.expected_ids]
        return {
            "expected_events": expected,
            "sent_events": len(sent),
            "observed_events": len(received),
            "missing_events": len(self.expected_ids - received),
            "filtered_events": len(filtered_ids),
            "filtered_event_ids": filtered_ids,
            "unexpected_events": len(received - self.expected_ids),
            "duplicate_events": collector.duplicates,
            "unmatched_events": collector.unmatched,
            "malformed_events": collector.malformed,
            "throughput_ratio": len(received) / expected,
            "collection_complete": (
                self.measurement_started and len(sent) == input_count and received == self.expected_ids
            ),
            "processing_latency_ms": summarize(
                sample["latency_ms"] for sample in samples if sample["latency_ms"] is not None
            ),
            "event_latencies": samples,
        }
