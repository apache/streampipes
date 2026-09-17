<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to you under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

# Runtime benchmark

One opt-in JMH benchmark measures one input message traversing a chain of real
standalone processor runtimes and reaching a real sink runtime. No broker or running
StreamPipes instance is required. The module is excluded from the default reactor.

## Build and run

From the repository root (JDK 25 is the CI toolchain; sources target Java 17):

```sh
mvn -Pbenchmarks -pl streampipes-benchmarks -am -DskipTests package
java -jar streampipes-benchmarks/target/benchmarks.jar -prof gc -rf json \
  -rff streampipes-benchmarks/target/results.json
```

`RuntimeBenchmark.inputToOutput` is the only benchmark. Parameters:

| Parameter | Defaults | Meaning |
| --- | --- | --- |
| `processorCount` | 0, 1, 4, 16 | Number of sequential processors before one terminal sink. Zero is an input-to-sink baseline. |
| `fields` | 8, 64 | Numeric payload fields, plus a four-element list and a root counter. |
| `depth` | 0, 2 | Object nesting depth around the payload fields and list. |

To focus on chain length:

```sh
java -jar streampipes-benchmarks/target/benchmarks.jar -prof gc \
  -p processorCount=0,1,4,16 -p fields=8 -p depth=0
```

Quick wiring check (not suitable for performance conclusions):

```sh
java -jar streampipes-benchmarks/target/benchmarks.jar -f 1 -wi 1 -i 1 \
  -w 100ms -r 100ms -p processorCount=0,1,4,16 -p fields=8 -p depth=0 -foe true
```

Defaults: two forked JVMs, three one-second warmup iterations, five one-second
measurement iterations, one worker thread. Use the same JDK, heap settings, machine,
and parameters for comparisons. Throughput is completed input messages per second;
`gc.alloc.rate.norm` is bytes allocated per completed input across the entire chain.
Use `-bm avgt -tu us` for average input-to-output time. Keep results under `target/`.

## Measurement boundary

A prebuilt JSON byte array enters the first real input collector. Every processor
runs the production admission, JSON decoding, event construction, monitoring, and
callback path. The callback increments a counter and emits through the real output
collector, including direct JSON encoding of the Event fields. Bytes are delivered
synchronously in memory to the next input collector. The terminal sink checks the
counter and retains the final Event, which JMH consumes. Each operation verifies
exactly one result; setup also checks that the complete payload survives unchanged
apart from the counter. Dropped events or skipped processors fail the run.

The transport uses the NATS protocol model with a benchmark-only in-memory
implementation. It does not run a NATS client or broker. With N processors, one
operation includes N encodes and N+1 decodes. Initial payload generation and runtime
startup/shutdown are outside measurement. Teardown checks transport cleanup.

Load management follows `SP_LOAD_MANAGER_ENABLE` (default `false`). Disabled runs
do not initialize the admission singletons. To measure enabled admission overhead,
prefix the Java command with `SP_LOAD_MANAGER_ENABLE=true` and save results separately.
The enabled path uses a very high rate and zero limiter warmup to avoid intentional
waiting, and checks memory reservations at teardown. Synchronous delivery nests
reservations across the chain; this is not a backpressure test.

Use forked runs and one thread: transport and admission state are process-wide.
Results measure runtime CPU/allocation cost, excluding network, broker scheduling,
concurrent stages, delivery guarantees, and business logic beyond an increment.
Broker-backed load tests belong in `streampipes-integration-tests`.
