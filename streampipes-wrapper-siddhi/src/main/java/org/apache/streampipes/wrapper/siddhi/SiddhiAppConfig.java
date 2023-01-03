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
package org.apache.streampipes.wrapper.siddhi;

import org.apache.streampipes.wrapper.siddhi.definition.SiddhiDefinition;
import org.apache.streampipes.wrapper.siddhi.output.SiddhiOutputConfig;
import org.apache.streampipes.wrapper.siddhi.query.SiddhiQuery;

import java.util.ArrayList;
import java.util.List;

public class SiddhiAppConfig {

  private final List<String> queries;
  private final List<String> definitions;
  private SiddhiOutputConfig outputConfig;

  public SiddhiAppConfig() {
    this.queries = new ArrayList<>();
    this.definitions = new ArrayList<>();
  }

  public void addDefinition(SiddhiDefinition definition) {
    this.definitions.add(definition.toSiddhiEpl());
  }

  public void addDefinition(String definition) {
    this.definitions.add(definition);
  }

  public void addQuery(SiddhiQuery query) {
    this.queries.add(query.toSiddhiEpl());
  }

  public void addQuery(String query) {
    this.queries.add(query);
  }

  public List<String> getQueries() {
    return queries;
  }

  public List<String> getDefinitions() {
    return definitions;
  }

  public SiddhiOutputConfig getOutputConfig() {
    return outputConfig;
  }

  public void setOutputConfig(SiddhiOutputConfig outputConfig) {
    this.outputConfig = outputConfig;
  }

}
