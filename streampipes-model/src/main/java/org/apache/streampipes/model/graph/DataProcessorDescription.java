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

package org.apache.streampipes.model.graph;

import io.fogsy.empire.annotations.RdfProperty;
import io.fogsy.empire.annotations.RdfsClass;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.SpDataStreamRelay;
import org.apache.streampipes.model.base.ConsumableStreamPipesEntity;
import org.apache.streampipes.model.output.OutputStrategy;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.util.Cloner;
import org.apache.streampipes.vocabulary.StreamPipes;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@RdfsClass(StreamPipes.DATA_PROCESSOR_DESCRIPTION)
@Entity
public class DataProcessorDescription extends ConsumableStreamPipesEntity {

  private static final long serialVersionUID = 3995767921861518597L;

  @OneToMany(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.HAS_OUTPUT_STRATEGY)
  private List<OutputStrategy> outputStrategies;

  @OneToOne(fetch = FetchType.EAGER,
          cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @RdfProperty(StreamPipes.HAS_EVENT_RELAY)
  private List<SpDataStreamRelay> outputStreamRelays;

  private String pathName;

  @OneToMany(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.HAS_EPA_TYPE)
  private List<String> category;

  public DataProcessorDescription(DataProcessorDescription other) {
    super(other);
    this.outputStrategies = new Cloner().strategies(other.getOutputStrategies());
    this.pathName = other.getPathName();
    this.category = new Cloner().epaTypes(other.getCategory());
    this.outputStreamRelays = new Cloner().relays(other.getOutputStreamRelays());
  }

  public DataProcessorDescription() {
    super();
    this.outputStrategies = new ArrayList<>();
    this.category = new ArrayList<>();
    this.outputStreamRelays = new ArrayList<>();
  }

  public DataProcessorDescription(String uri, String name, String description, String iconUrl,
                                  List<SpDataStream> spDataStreams, List<StaticProperty> staticProperties,
                                  List<OutputStrategy> outputStrategies, List<SpDataStreamRelay> outputStreamRelays) {
    super(uri, name, description, iconUrl);
    this.pathName = uri;
    this.spDataStreams = spDataStreams;
    this.staticProperties = staticProperties;
    this.outputStrategies = outputStrategies;
    this.outputStreamRelays = outputStreamRelays;
  }

  public DataProcessorDescription(String pathName, String name, String description, String iconUrl) {
    super(pathName, name, description, iconUrl);
    this.pathName = pathName;
    spDataStreams = new ArrayList<>();
    staticProperties = new ArrayList<>();
    outputStreamRelays = new ArrayList<>();
  }

  public DataProcessorDescription(String pathName, String name, String description) {
    super(pathName, name, description, "");
    this.pathName = pathName;
    spDataStreams = new ArrayList<>();
    staticProperties = new ArrayList<>();
    outputStreamRelays = new ArrayList<>();
  }


  public List<String> getCategory() {
    return category;
  }

  public void setCategory(List<String> category) {
    this.category = category;
  }

  public String getPathName() {
    return pathName;
  }

  public void setPathName(String pathName) {
    this.pathName = pathName;
  }

  public List<OutputStrategy> getOutputStrategies() {
    return outputStrategies;
  }

  public void setOutputStrategies(List<OutputStrategy> outputStrategies) {
    this.outputStrategies = outputStrategies;
  }

  public List<SpDataStreamRelay> getOutputStreamRelays() {
    return outputStreamRelays;
  }

  public void setOutputStreamRelays(List<SpDataStreamRelay> outputStreamRelays) {
    this.outputStreamRelays = outputStreamRelays;
  }
}
