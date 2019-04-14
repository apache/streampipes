/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.streampipes.processors.siddhi.trend;

import org.streampipes.wrapper.siddhi.engine.SiddhiEventEngine;

import java.util.List;

public class Trend extends SiddhiEventEngine<TrendParameters> {


//  from every( e1=TempStream ) -> e2=TempStream[ e1.roomNo == roomNo and (e1.temp + 5) <= temp ]
//  within 10 min
//  select e1.roomNo, e1.temp as initialTemp, e2.temp as finalTemp
//  insert into AlertStream;

  @Override
  protected String fromStatement(List<String> inputStreamNames, TrendParameters params) {
      String mappingProperty = prepareName(params.getMapping());
      int duration = params.getDuration();
      String operator;

      double increase = Double.valueOf(params.getIncrease());
      increase = (increase / 100) + 1;

      if (params.getOperation() == TrendOperator.INCREASE) {
          operator = ">=";
      } else {
          operator = "<=";
      }

      String s = "from every(e1=" + inputStreamNames.get(0) +") -> e2=" +inputStreamNames.get(0) + "[e1." + mappingProperty + operator + mappingProperty + " * " + increase + "]<1>" +
            " within " + duration + " sec";

    return s;
  }

  @Override
  protected String selectStatement(TrendParameters params) {
      return getCustomOutputSelectStatement(params.getGraph());
  }

}
