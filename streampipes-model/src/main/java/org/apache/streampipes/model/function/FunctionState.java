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

package org.apache.streampipes.model.function;

import org.apache.streampipes.commons.constants.GenericDocTypes;
import org.apache.streampipes.model.shared.api.Storable;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class FunctionState implements Storable {

  public static final String APP_DOC_TYPE = GenericDocTypes.DOC_FUNCTION_STATE;

  private final String appDocType = APP_DOC_TYPE;

  protected @SerializedName("_rev") String rev;
  private @SerializedName("_id") String functionId;
  private Map<String, Object> state;

  public FunctionState() {
  }

  public FunctionState(String functionId,
                       Map<String, Object> state) {
    this.functionId = functionId;
    this.state = state;
  }

  public String getFunctionId() {
    return functionId;
  }

  public void setFunctionId(String functionId) {
    this.functionId = functionId;
  }

  public Map<String, Object> getState() {
    return state;
  }

  public void setState(Map<String, Object> state) {
    this.state = state;
  }

  @Override
  public String getRev() {
    return rev;
  }

  @Override
  public void setRev(String rev) {
    this.rev = rev;
  }

  @Override
  public String getElementId() {
    return functionId;
  }

  @Override
  public void setElementId(String elementId) {
    this.functionId = elementId;
  }

  public String getAppDocType() {
    return appDocType;
  }
}
