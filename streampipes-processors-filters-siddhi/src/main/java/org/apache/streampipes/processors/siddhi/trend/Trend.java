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
package org.apache.streampipes.processors.siddhi.trend;

import org.apache.streampipes.wrapper.siddhi.engine.SiddhiEventEngine;
import org.apache.streampipes.wrapper.siddhi.engine.callback.SiddhiDebugCallback;
import org.apache.streampipes.wrapper.siddhi.model.SiddhiProcessorParams;
import org.apache.streampipes.wrapper.siddhi.query.SelectClause;
import org.apache.streampipes.wrapper.siddhi.query.expression.Expressions;

import java.util.List;

public class Trend extends SiddhiEventEngine<TrendParameters> {

  public Trend() {
    super();
  }

  public Trend(SiddhiDebugCallback callback) {
    super(callback);
  }

  @Override
  public String fromStatement(SiddhiProcessorParams<TrendParameters> siddhiParams) {
    TrendParameters trendParameters = siddhiParams.getParams();
    String mappingProperty = prepareName(trendParameters.getMapping());
    int duration = trendParameters.getDuration();
    String inequaloperator;
    String operator;

    double increase = trendParameters.getIncrease();
    increase = (increase / 100) + 1;

    if (trendParameters.getOperation() == TrendOperator.INCREASE) {
      inequaloperator = "<=";
      operator = "/";

    } else {
      inequaloperator = ">=";
      operator = "*";
    }

    String s = "from every(e1="
            + siddhiParams.getInputStreamNames().get(0)
            + ") -> e2="
            + siddhiParams.getInputStreamNames().get(0)
            + "[e1." + mappingProperty
            + inequaloperator
            + "("
            + mappingProperty
            + operator
            + increase
            + ")"
            + "]<1>"
            + " within " + duration + " sec";

    //String s = "from e1="+inputStreamNames.get(0) + "[e1.s0randomValue > 5]";

    return s;
  }

  @Override
  public String selectStatement(SiddhiProcessorParams<TrendParameters> siddhiParams) {
    SelectClause selectClause = SelectClause.create();
    List<String> outputFieldSelectors = siddhiParams.getParams().getOutputFieldSelectors();
    outputFieldSelectors
            .forEach(outputFieldSelector -> selectClause
                     .addProperty(Expressions.property("e2", outputFieldSelector)));

    return selectClause.toSiddhiEpl();
    //return siddhiParams.getCustomOutputSelectStatement(siddhiParams.getParams().getGraph(), "e2");
  }

}
