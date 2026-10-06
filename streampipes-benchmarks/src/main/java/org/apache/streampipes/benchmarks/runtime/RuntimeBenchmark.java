/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */


package org.apache.streampipes.benchmarks.runtime;

import org.apache.streampipes.benchmarks.BenchmarkPayloads;
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.dataformat.JsonDataFormatDefinition;
import org.apache.streampipes.extensions.api.limiter.SpRateLimiter;
import org.apache.streampipes.extensions.api.memorymanager.SpMemoryManager;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataProcessor;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataSink;
import org.apache.streampipes.extensions.api.pe.config.IDataProcessorConfiguration;
import org.apache.streampipes.extensions.api.pe.config.IDataSinkConfiguration;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataProcessorParameters;
import org.apache.streampipes.extensions.api.pe.param.IDataSinkParameters;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.extensions.management.monitoring.ExtensionsLogger;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.grounding.NatsTransportProtocol;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.wrapper.context.SpEventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.context.SpEventSinkRuntimeContext;
import org.apache.streampipes.wrapper.params.generator.DataProcessorParameterGenerator;
import org.apache.streampipes.wrapper.params.generator.DataSinkParameterGenerator;
import org.apache.streampipes.wrapper.standalone.runtime.StandaloneEventProcessorRuntime;
import org.apache.streampipes.wrapper.standalone.runtime.StandaloneEventSinkRuntime;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.ThreadParams;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
@Threads(1)
@State(Scope.Benchmark)
public class RuntimeBenchmark {

  @Param({"0", "1", "4", "16"})
  public int processorCount;

  @Param({"8", "64"})
  public int fields;

  @Param({"0", "2"})
  public int depth;

  private final List<Runnable> cleanup = new ArrayList<>();
  private final ResultSink sink = new ResultSink();
  private byte[] payload;
  private long initialFreeMemory;
  private boolean loadManagementEnabled;

  @Setup
  public void setup(ThreadParams threads) {
    if (threads.getThreadCount() != 1 || processorCount < 0 || fields < 0 || depth < 0) {
      throw new IllegalArgumentException("Use one thread and nonnegative processorCount, fields, and depth");
    }
    InMemoryTransport.register();
    loadManagementEnabled = Environments.getEnvironment().getLoadManagerEnable().getValueOrDefault();
    if (loadManagementEnabled) {
      SpRateLimiter.INSTANCE.reset();
      SpRateLimiter.INSTANCE.createRateLimiter(1.0e15, 0, TimeUnit.SECONDS);
      initialFreeMemory = SpMemoryManager.INSTANCE.getFreeMemory();
    }
    var values = BenchmarkPayloads.create(fields, depth);
    values.put("counter", 0);
    payload = new JsonDataFormatDefinition().fromMap(values);

    // Start downstream first so every output already has a connected recipient.
    var sinkInvocation = new DataSinkInvocation();
    sinkInvocation.setElementId("benchmark-sink");
    sinkInvocation.setInputStreams(List.of(stream(processorCount)));
    var sinkRuntime = new StandaloneEventSinkRuntime();
    cleanup.add(sinkRuntime::stopRuntime);
    sinkRuntime.startRuntime(sinkInvocation, sink,
        new DataSinkParameterGenerator().makeParameters(sinkInvocation),
        new SpEventSinkRuntimeContext("benchmark", null, null, new ExtensionsLogger("benchmark-sink")));
    for (int i = processorCount - 1; i >= 0; i--) {
      var invocation = new DataProcessorInvocation();
      invocation.setElementId("benchmark-processor-" + i);
      invocation.setInputStreams(List.of(stream(i)));
      invocation.setOutputStream(stream(i + 1));
      invocation.setOutputStrategies(List.of());
      var runtime = new StandaloneEventProcessorRuntime();
      cleanup.add(runtime::stopRuntime);
      runtime.startRuntime(invocation, new IncrementProcessor(),
          new DataProcessorParameterGenerator().makeParameters(invocation),
          new SpEventProcessorRuntimeContext("benchmark", null, null,
              new ExtensionsLogger(invocation.getElementId())));
    }
    // Validate all payload fields once outside measurement, including nested objects and lists.
    var expected = new JsonDataFormatDefinition().toMap(payload);
    expected.put("counter", processorCount);
    if (!inputToOutput().getRaw().equals(expected)) {
      throw new AssertionError("Runtime changed the payload unexpectedly");
    }
  }

  private SpDataStream stream(int index) {
    var stream = new SpDataStream();
    stream.setEventGrounding(new EventGrounding(
        new NatsTransportProtocol("benchmark.invalid", 4222, "benchmark-" + index)));
    stream.setEventSchema(new EventSchema(List.of()));
    return stream;
  }

  @Benchmark
  public Event inputToOutput() {
    long previous = sink.received;
    InMemoryTransport.deliver("benchmark-0", payload);
    if (sink.received != previous + 1) {
      throw new AssertionError("Expected exactly one output per input");
    }
    return sink.lastEvent;
  }

  @TearDown
  public void teardown() {
    try {
      for (int i = cleanup.size() - 1; i >= 0; i--) {
        cleanup.get(i).run();
      }
      InMemoryTransport.assertDisconnected();
    } finally {
      if (loadManagementEnabled) {
        SpRateLimiter.INSTANCE.reset();
        SpMemoryManager.shutdown();
        if (SpMemoryManager.INSTANCE.getFreeMemory() != initialFreeMemory) {
          throw new AssertionError("Runtime leaked memory reservations");
        }
      }
    }
  }

  private static class IncrementProcessor implements IStreamPipesDataProcessor {
    @Override
    public IDataProcessorConfiguration declareConfig() {
      throw new UnsupportedOperationException("Benchmark invocations are constructed directly");
    }

    @Override
    public void onPipelineStarted(IDataProcessorParameters params, SpOutputCollector collector,
                                  EventProcessorRuntimeContext context) {
    }

    @Override
    public void onEvent(Event event, SpOutputCollector collector) {
      var counter = event.getFieldBySelector("s0::counter").getAsPrimitive();
      counter.setValue(counter.getAsInt() + 1);
      collector.collect(event);
    }

    @Override
    public void onPipelineStopped() {
    }
  }

  private class ResultSink implements IStreamPipesDataSink {
    private long received;
    private Event lastEvent;

    @Override
    public IDataSinkConfiguration declareConfig() {
      throw new UnsupportedOperationException("Benchmark invocations are constructed directly");
    }

    @Override
    public void onPipelineStarted(IDataSinkParameters params, EventSinkRuntimeContext context) {
    }

    @Override
    public void onEvent(Event event) {
      if (event.getFieldBySelector("s0::counter").getAsPrimitive().getAsInt() != processorCount) {
        throw new AssertionError("Event did not traverse every processor exactly once");
      }
      lastEvent = event;
      received++;
    }

    @Override
    public void onPipelineStopped() {
    }
  }
}
