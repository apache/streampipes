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

import org.apache.streampipes.model.base.ConsumableStreamPipesEntity;
import org.apache.streampipes.model.util.Cloner;

import java.util.ArrayList;
import java.util.List;

public class DataSinkDescription extends ConsumableStreamPipesEntity {

  private static final long serialVersionUID = -6553066396392585731L;

  private List<String> category;

  public DataSinkDescription(String uri, String name, String description, String iconUrl) {
    super(uri, name, description, iconUrl);
    this.spDataStreams = new ArrayList<>();
    this.category = new ArrayList<>();
  }

  public DataSinkDescription(DataSinkDescription other) {
    super(other);
    this.category = new Cloner().ecTypes(other.getCategory());
  }

  public DataSinkDescription(String uri, String name, String description) {
    this(uri, name, description, "");
    this.category = new ArrayList<>();
  }

  public DataSinkDescription() {
    super();
    this.category = new ArrayList<>();
  }

  public List<String> getCategory() {
    return category;
  }

  public void setCategory(List<String> category) {
    this.category = category;
  }


}
