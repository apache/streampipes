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

package org.apache.streampipes.integration.sinks;

import org.apache.streampipes.extensions.api.pe.IStreamPipesDataSink;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.staticproperty.StaticProperty;

import java.util.List;

public abstract class SinkTesterBase implements AutoCloseable {

  private IStreamPipesDataSink sink;
  private final DataSinkTestConfigurator configurator;

  protected SinkTesterBase() {
    this.configurator = new DataSinkTestConfigurator();
  }

  public void run() throws Exception {
    startSinkService();

    this.sink = createSink();
    List<SpDataStream> inputStreams = createInputStreams();
    List<StaticProperty> staticProperties = configureStaticProperties(sink, inputStreams);

    DataSinkInvocation invocation = new DataSinkInvocation(sink.declareConfig().getDescription());
    invocation.setStaticProperties(staticProperties);
    invocation.setInputStreams(inputStreams);

    sink.onPipelineStarted(new TestDataSinkParameters(invocation, inputStreams), null);

    List<Event> events = createEvents();
    for (Event event : events) {
      sink.onEvent(event);
    }

    validate(events);
  }

  protected DataSinkTestConfigurator configurator() {
    return configurator;
  }

  protected abstract void startSinkService() throws Exception;

  protected abstract IStreamPipesDataSink createSink() throws Exception;

  protected abstract List<SpDataStream> createInputStreams();

  protected abstract List<StaticProperty> configureStaticProperties(IStreamPipesDataSink sink,
                                                                    List<SpDataStream> inputStreams) throws Exception;

  protected abstract List<Event> createEvents();

  protected abstract void validate(List<Event> events) throws Exception;

  protected void stopSinkService() throws Exception {
  }

  @Override
  public void close() throws Exception {
    Exception failure = null;

    if (sink != null) {
      try {
        sink.onPipelineStopped();
      } catch (Exception e) {
        failure = e;
      } finally {
        sink = null;
      }
    }

    try {
      stopSinkService();
    } catch (Exception e) {
      if (failure != null) {
        failure.addSuppressed(e);
      } else {
        failure = e;
      }
    }

    if (failure != null) {
      throw failure;
    }
  }
}
