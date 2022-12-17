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
package org.apache.streampipes.vocabulary;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public enum SemanticTypeRegistry {

  INSTANCE;

  private List<String> semanticTypes;

  SemanticTypeRegistry() {
    this.semanticTypes = new ArrayList<>();
    registerTypes();
  }

  private void registerTypes() {
    this.semanticTypes.addAll(SPSensor.getAll());
    this.semanticTypes.addAll(SO.getAll());
    this.semanticTypes.addAll(Geo.getAll());
    this.semanticTypes.addAll(Geonames.getAll());
  }

  public List<String> getAllSemanticTypes() {
    return semanticTypes;
  }

  public List<String> matches(String text) {
    return this.semanticTypes
        .stream()
        .filter(type -> type.contains(text))
        .collect(Collectors.toList());
  }
}
