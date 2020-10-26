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
package org.apache.streampipes.processors.siddhi.filter;

import org.apache.streampipes.wrapper.siddhi.constants.SiddhiStreamSelector;
import org.apache.streampipes.wrapper.siddhi.engine.SiddhiEventEngine;
import org.apache.streampipes.wrapper.siddhi.engine.callback.SiddhiDebugCallback;
import org.apache.streampipes.wrapper.siddhi.model.SiddhiProcessorParams;
import org.apache.streampipes.wrapper.siddhi.query.FromClause;
import org.apache.streampipes.wrapper.siddhi.query.SelectClause;
import org.apache.streampipes.wrapper.siddhi.query.expression.Expression;
import org.apache.streampipes.wrapper.siddhi.query.expression.Expressions;
import org.apache.streampipes.wrapper.siddhi.query.expression.RelationalOperator;
import org.apache.streampipes.wrapper.siddhi.query.expression.RelationalOperatorExpression;

public class NumericalFilter extends SiddhiEventEngine<NumericalFilterParameters> {

  public NumericalFilter() {
    super();
  }

  public NumericalFilter(SiddhiDebugCallback callback) {
    super(callback);
  }

  @Override
  public String fromStatement(SiddhiProcessorParams<NumericalFilterParameters> siddhiParams) {
    NumericalFilterParameters filterParameters = siddhiParams.getParams();
    String filterProperty = filterParameters.getFilterProperty();
    RelationalOperator operator = filterParameters.getFilterOperator();
    Double threshold = filterParameters.getThreshold();

    FromClause fromClause = FromClause.create();
    Expression filter = new RelationalOperatorExpression(operator, Expressions.property(filterProperty), Expressions.staticValue(threshold));
    Expression stream = Expressions.filter(Expressions.stream(siddhiParams.getInputStreamNames().get(0)), filter);

    fromClause.add(stream);

    return fromClause.toSiddhiEpl();
    // e.g. Filter for numberField value less than 10 and output all fields
    //
    // Siddhi query: from inputstreamname[numberField<10]
    //return "from " + siddhiParams.getInputStreamNames().get(0) +"[" + filterProperty + filterOperator + filterParameters.getThreshold() +"]";
  }

  @Override
  public String selectStatement(SiddhiProcessorParams<NumericalFilterParameters> siddhiParams) {
    SelectClause selectClause = SelectClause.create();
    siddhiParams
            .getOutputEventKeys()
            .forEach(fieldName -> selectClause.addProperty(makeProperty(fieldName)));

    return selectClause.toSiddhiEpl();
  }

  private Expression makeProperty(String fieldName) {
    return Expressions.property(SiddhiStreamSelector.FIRST_INPUT_STREAM, fieldName);
  }

}
