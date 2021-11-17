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
package org.apache.streampipes.sinks.databases.jvm.ditto;

import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.wrapper.params.binding.EventSinkBindingParams;

import java.util.List;

public class DittoParameters extends EventSinkBindingParams {

  private String dittoApiEndpoint;
  private String dittoUser;
  private String dittoPassword;

  private String thingId;
  private String featureId;

  private List<String> selectedFields;

  public DittoParameters(DataSinkInvocation graph,
                         String dittoApiEndpoint,
                         String dittoUser,
                         String dittoPassword,
                         String thingId,
                         String featureId,
                         List<String> selectedFields) {
    super(graph);
    this.dittoApiEndpoint = dittoApiEndpoint;
    this.dittoUser = dittoUser;
    this.dittoPassword = dittoPassword;
    this.thingId = thingId;
    this.featureId = featureId;
    this.selectedFields = selectedFields;
  }

  public String getDittoApiEndpoint() {
    return dittoApiEndpoint;
  }

  public String getDittoUser() {
    return dittoUser;
  }

  public String getDittoPassword() {
    return dittoPassword;
  }

  public String getThingId() {
    return thingId;
  }

  public String getFeatureId() {
    return featureId;
  }

  public List<String> getSelectedFields() {
    return selectedFields;
  }
}
