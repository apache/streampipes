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
import org.apache.streampipes.wrapper.siddhi.output.SiddhiFirstOutputConfig;
import org.apache.streampipes.wrapper.siddhi.output.SiddhiOutputConfig;
import org.apache.streampipes.wrapper.siddhi.query.SiddhiQuery;

public class SiddhiAppConfigBuilder {

  private final SiddhiAppConfig siddhiAppConfig;

  public static SiddhiAppConfigBuilder create(SiddhiOutputConfig outputConfig) {
    return new SiddhiAppConfigBuilder(outputConfig);
  }

  public static SiddhiAppConfigBuilder create() {
    return new SiddhiAppConfigBuilder(new SiddhiFirstOutputConfig());
  }

  private SiddhiAppConfigBuilder(SiddhiOutputConfig outputOptions) {
    this.siddhiAppConfig = new SiddhiAppConfig();
    this.siddhiAppConfig.setOutputConfig(outputOptions);
  }

  public SiddhiAppConfigBuilder addQuery(SiddhiQuery query) {
    this.siddhiAppConfig.addQuery(query);
    return this;
  }

  public SiddhiAppConfigBuilder addDefinition(SiddhiDefinition definition) {
    this.siddhiAppConfig.addDefinition(definition);
    return this;
  }

  public SiddhiAppConfigBuilder addDefinition(String definition) {
    this.siddhiAppConfig.addDefinition(definition);
    return this;
  }

  public SiddhiAppConfig build() {
    return this.siddhiAppConfig;
  }

}
