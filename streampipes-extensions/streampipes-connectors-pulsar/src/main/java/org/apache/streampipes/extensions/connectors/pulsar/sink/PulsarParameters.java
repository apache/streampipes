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
package org.apache.streampipes.extensions.connectors.pulsar.sink;

import static org.apache.streampipes.extensions.connectors.pulsar.sink.PulsarPublisherSink.PULSAR_HOST_KEY;
import static org.apache.streampipes.extensions.connectors.pulsar.sink.PulsarPublisherSink.PULSAR_PORT_KEY;
import static org.apache.streampipes.extensions.connectors.pulsar.sink.PulsarPublisherSink.TOPIC_KEY;

import org.apache.streampipes.extensions.api.pe.param.IDataSinkParameters;

public class PulsarParameters {
  private final String pulsarHost;
  private final Integer pulsarPort;
  private final String topic;

  public PulsarParameters(IDataSinkParameters parameters) {
    var extractor = parameters.extractor();

    this.pulsarHost = extractor.singleValueParameter(PULSAR_HOST_KEY, String.class);
    this.pulsarPort = extractor.singleValueParameter(PULSAR_PORT_KEY, Integer.class);
    this.topic = extractor.singleValueParameter(TOPIC_KEY, String.class);
  }

  public String getPulsarHost() {
    return pulsarHost;
  }

  public Integer getPulsarPort() {
    return pulsarPort;
  }

  public String getTopic() {
    return topic;
  }
}
