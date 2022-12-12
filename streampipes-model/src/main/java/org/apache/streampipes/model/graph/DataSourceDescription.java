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

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.shared.annotation.TsModel;
import org.apache.streampipes.model.util.Cloner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * class that represents Semantic Event Producers.
 */


/**
 * @deprecated  As of release 0.90.0, replaced by {@link DataSourceDescription}
 */
@TsModel
@Deprecated(since = "0.90.0", forRemoval = true)
public class DataSourceDescription extends NamedStreamPipesEntity {

  private static final long serialVersionUID = 5607030219013954697L;

  private List<SpDataStream> spDataStreams;

  private String correspondingSourceId;

  public DataSourceDescription() {
    super();
    spDataStreams = new ArrayList<>();
  }

  public DataSourceDescription(DataSourceDescription other) {
    super(other);
    this.spDataStreams = new Cloner().seq(other.getSpDataStreams());
    this.spDataStreams.forEach(e -> e.setCategory(Arrays.asList(this.getElementId())));
  }

  public DataSourceDescription(String uri, String name, String description, String iconUrl,
                               List<SpDataStream> spDataStreams) {
    super(uri, name, description, iconUrl);
    this.spDataStreams = spDataStreams;
    this.setAppId(uri);
  }

  public DataSourceDescription(String uri, String name2, String description2, String iconUrl) {
    this(uri, name2, description2, iconUrl, new ArrayList<>());
  }

  public DataSourceDescription(String uri, String name, String description) {
    this(uri, name, description, "", new ArrayList<>());
  }

  public List<SpDataStream> getSpDataStreams() {
    return spDataStreams;
  }

  public void setSpDataStreams(List<SpDataStream> spDataStreams) {
    this.spDataStreams = spDataStreams;
  }

  public void addEventStream(SpDataStream spDataStream) {
    spDataStreams.add(spDataStream);
  }

  public String getCorrespondingSourceId() {
    return correspondingSourceId;
  }

  public void setCorrespondingSourceId(String correspondingSourceId) {
    this.correspondingSourceId = correspondingSourceId;
  }
}
