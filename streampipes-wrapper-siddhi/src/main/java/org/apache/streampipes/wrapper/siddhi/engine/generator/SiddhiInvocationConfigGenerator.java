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
package org.apache.streampipes.wrapper.siddhi.engine.generator;

import org.apache.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.apache.streampipes.wrapper.siddhi.model.SiddhiProcessorParams;
import org.apache.streampipes.wrapper.siddhi.model.EventPropertyDef;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class SiddhiInvocationConfigGenerator<B extends EventProcessorBindingParams> {

  private final String siddhiAppString;
  private final String fromStatement;
  private final String selectStatement;
  private final String groupByStatement;

  private final SiddhiProcessorParams<B> siddhiProcessorParams;

  public SiddhiInvocationConfigGenerator(B params,
                                         Function<SiddhiProcessorParams<B>, String> fromStatementFunction,
                                         Function<SiddhiProcessorParams<B>, String> selectStatementFunction,
                                         Function<SiddhiProcessorParams<B>, String> groupByStatementFunction) {
    List<String> inputStreamNames = new InputStreamNameGenerator<>(params).generateInputStreamNames();
    Map<String, List<EventPropertyDef>> eventTypeInfo = new EventTypeGenerator<>(params).generateEventTypes();
    List<String> outputEventKeys = new ArrayList<>(params.getOutEventType().keySet());
    this.siddhiProcessorParams = new SiddhiProcessorParams<>(params, inputStreamNames, eventTypeInfo, outputEventKeys);
    this.fromStatement = fromStatementFunction.apply(this.siddhiProcessorParams);
    this.selectStatement = selectStatementFunction.apply(this.siddhiProcessorParams);
    this.groupByStatement = groupByStatementFunction.apply(this.siddhiProcessorParams);
    this.siddhiAppString = new SiddhiAppGenerator<>(siddhiProcessorParams, fromStatement, selectStatement, groupByStatement)
            .generateSiddhiApp();
  }

  public String getSiddhiAppString() {
    return siddhiAppString;
  }

  public SiddhiProcessorParams<B> getSiddhiProcessorParams() {
    return siddhiProcessorParams;
  }

  public String getFromStatement() {
    return fromStatement;
  }

  public String getSelectStatement() {
    return selectStatement;
  }

  public String getGroupByStatement() {
    return groupByStatement;
  }
}
