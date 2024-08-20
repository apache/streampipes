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

package org.apache.streampipes.sdk.builder;

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;

import java.util.ArrayList;
import java.util.List;

public class DataStreamBuilder extends AbstractPipelineElementBuilder<DataStreamBuilder, SpDataStream> {

  private List<EventProperty> eventProperties;
  private EventGrounding eventGrounding;

  protected DataStreamBuilder(String id, String label, String description) {
    super(id, label, description, new SpDataStream());
    this.eventProperties = new ArrayList<>();
    this.eventGrounding = new EventGrounding();
  }

  protected DataStreamBuilder(String id) {
    super(id, new SpDataStream());
    this.eventProperties = new ArrayList<>();
    this.eventGrounding = new EventGrounding();
  }

  /**
   * Creates a new data stream using the builder pattern.
   *
   * @param id          A unique identifier of the new element, e.g., com.mycompany.stream.mynewdatastream
   * @param label       A human-readable name of the element.
   *                    Will later be shown as the element name in the StreamPipes UI.
   * @param description A human-readable description of the element.
   * @return a new instance of {@link DataStreamBuilder}
   */
  public static DataStreamBuilder create(String id, String label, String description) {
    return new DataStreamBuilder(id, label, description);
  }

  /**
   * Creates a new data stream using the builder pattern.
   *
   * @param id A unique identifier of the new element, e.g., com.mycompany.stream.mynewdatastream
   * @return a new instance of {@link DataStreamBuilder}
   */
  public static DataStreamBuilder create(String id) {
    return new DataStreamBuilder(id);
  }

  /**
   * Assigns a new event property to the stream's schema.
   *
   * @param property The event property that should be added.
   *                 Use {@link org.apache.streampipes.sdk.helpers.EpProperties}
   *                 for defining simple property definitions or
   *                 {@link org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder}
   *                 for defining more complex definitions.
   * @return this
   */
  public DataStreamBuilder property(EventProperty property) {
    this.eventProperties.add(property);
    return me();
  }

  /**
   * Assigns a list of new event properties to the stream's schema.
   *
   * @param properties The event properties that should be added.
   * @return this
   */
  public DataStreamBuilder properties(List<EventProperty> properties) {
    this.eventProperties.addAll(properties);
    return me();
  }

  /**
   * Assigns a new {@link org.apache.streampipes.model.grounding.TransportProtocol} to the stream definition.
   *
   * @param protocol The transport protocol of the stream at runtime (e.g., Kafka or MQTT).
   *                 Use {@link org.apache.streampipes.sdk.helpers.Protocols} to use some pre-defined protocols
   *                 (or create a new protocol as described in the developer guide).
   * @return this
   */
  public DataStreamBuilder protocol(TransportProtocol protocol) {
    this.eventGrounding.setTransportProtocol(protocol);
    return this;
  }

  @Override
  protected DataStreamBuilder me() {
    return this;
  }

  @Override
  protected void prepareBuild() {
    this.elementDescription.setEventGrounding(eventGrounding);
    this.elementDescription.setEventSchema(new EventSchema(eventProperties));
  }
}
