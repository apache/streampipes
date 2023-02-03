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

package org.apache.streampipes.manager.function;

import org.apache.streampipes.model.function.FunctionDefinition;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public enum FunctionRegistrationService {

  INSTANCE;

  Map<String, FunctionDefinition> registeredFunctions;

  FunctionRegistrationService() {
    this.registeredFunctions = new HashMap<>();
  }

  public Collection<FunctionDefinition> getAllFunctions() {
    return registeredFunctions.values();
  }

  public FunctionDefinition getFunction (String functionId) {
    return registeredFunctions.get(functionId);
  }

  public void registerFunction(FunctionDefinition function) {
    this.registeredFunctions.put(function.getFunctionId().getId(), function);
  }

  public void deregisterFunction(String functionId) {

    this.registeredFunctions.remove(functionId);
  }
}
