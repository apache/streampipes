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

import org.apache.streampipes.model.shared.annotation.TsModel;

import java.util.List;

@TsModel
public class FunctionDefinition {

  private FunctionId functionId;
  private List<String> consumedStreams;

  public FunctionDefinition() {
  }

  public FunctionDefinition(FunctionId functionId, List<String> consumedStreams) {
    this.functionId = functionId;
    this.consumedStreams = consumedStreams;
  }

  public FunctionId getFunctionId() {
    return functionId;
  }

  public void setFunctionId(FunctionId functionId) {
    this.functionId = functionId;
  }

  public List<String> getConsumedStreams() {
    return consumedStreams;
  }

  public void setConsumedStreams(List<String> consumedStreams) {
    this.consumedStreams = consumedStreams;
  }

  @Override
  public String toString() {
    return String.format("FunctionDefinition{functionId='%s',consumedStreams='%s'}",
        functionId.toString(),
        consumedStreams.toString()
    );
  }
}
