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
package org.apache.streampipes.model.output;

import java.io.Serializable;

public class PropertyRenameRule implements Serializable {

  private String runtimeId;

  private String newRuntimeName;

  public PropertyRenameRule() {
    super();
  }

  public PropertyRenameRule(String runtimeId, String newRuntimeName) {
    super();
    this.runtimeId = runtimeId;
    this.newRuntimeName = newRuntimeName;
  }

  public PropertyRenameRule(PropertyRenameRule other) {
    this.runtimeId = other.getRuntimeId();
    this.newRuntimeName = other.getNewRuntimeName();
  }

  public String getRuntimeId() {
    return runtimeId;
  }

  public void setRuntimeId(String runtimeId) {
    this.runtimeId = runtimeId;
  }

  public String getNewRuntimeName() {
    return newRuntimeName;
  }

  public void setNewRuntimeName(String newRuntimeName) {
    this.newRuntimeName = newRuntimeName;
  }
}
