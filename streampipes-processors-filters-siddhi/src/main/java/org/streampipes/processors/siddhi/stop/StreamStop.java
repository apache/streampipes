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
package org.streampipes.processors.siddhi.stop;

import org.streampipes.wrapper.siddhi.engine.SiddhiEventEngine;

import java.util.List;

public class StreamStop extends SiddhiEventEngine<StreamStopParameters> {

  @Override
  protected String fromStatement(List<String> inputStreamNames, StreamStopParameters params) {

//    from every (e1=MaterialSupplyStream) -> e2=MaterialConsumptionStream within 10 min

//      return "from every(e1=" + inputStreamNames.get(0) + ") -> not e2=" + inputStreamNames.get(0) + " for " + params.getDuration() + " sec";
//    return "define stream Test(timestamp LONG,message STRING);\n" +
//            return "from every not " + inputStreamNames.get(0) + " for " + params.getDuration() + " sec";
    return "from every not " + inputStreamNames.get(0) + " for 1 sec";
  }

  @Override
  protected String selectStatement(StreamStopParameters params) {
//    return getCustomOutputSelectStatement(params.getGraph());
    return "select *";
//    return "select currentTimeMillis() as timestamp, 'Customer has not arrived' as message";
  }

}
