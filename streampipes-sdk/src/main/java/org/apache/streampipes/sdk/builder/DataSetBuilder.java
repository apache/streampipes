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

import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.grounding.TransportFormat;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataSetBuilder extends AbstractPipelineElementBuilder<DataSetBuilder, SpDataSet> {

  protected EventGrounding supportedGrounding;
  private List<EventProperty> eventProperties;

  protected DataSetBuilder(String id, String label, String description) {
    super(id, label, description, new SpDataSet());
    this.eventProperties = new ArrayList<>();
    this.supportedGrounding = new EventGrounding();
  }

  /**
   * Creates a new data set using the builder pattern.
   *
   * @param id          A unique identifier of the new element, e.g., com.mycompany.set.mynewdataset
   * @param label       A human-readable name of the element.
   *                    Will later be shown as the element name in the StreamPipes UI.
   * @param description A human-readable description of the element.
   * @return a new instance of {@link DataSetBuilder}
   */
  public static DataSetBuilder create(String id, String label, String description) {
    return new DataSetBuilder(id, label, description);
  }

  public DataSetBuilder property(EventProperty property) {
    this.eventProperties.add(property);
    return me();
  }

  public DataSetBuilder supportedProtocol(TransportProtocol protocol) {
    this.supportedGrounding.setTransportProtocol(protocol);
    return this;
  }

  public DataSetBuilder supportedFormat(TransportFormat format) {
    this.supportedGrounding.setTransportFormats(Collections.singletonList(format));
    return this;
  }

  @Override
  protected DataSetBuilder me() {
    return this;
  }

  @Override
  protected void prepareBuild() {
    this.elementDescription.setSupportedGrounding(supportedGrounding);
    this.elementDescription.setEventSchema(new EventSchema(eventProperties));
  }
}
