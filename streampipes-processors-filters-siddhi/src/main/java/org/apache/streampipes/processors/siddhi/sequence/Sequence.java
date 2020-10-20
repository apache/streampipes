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
package org.apache.streampipes.processors.siddhi.sequence;

import org.apache.streampipes.wrapper.siddhi.engine.SiddhiEventEngine;
import org.apache.streampipes.wrapper.siddhi.model.SiddhiProcessorParams;

public class Sequence extends SiddhiEventEngine<SequenceParameters> {

  @Override
  public String fromStatement(SiddhiProcessorParams<SequenceParameters> siddhiParams) {

//    from every (e1=MaterialSupplyStream) -> e2=MaterialConsumptionStream within 10 min

//      return "from every(e1=" + inputStreamNames.get(0) + ") -> not e2=" + inputStreamNames.get(0) + " for " + params.getDuration() + " sec";
//    return "define stream Test(timestamp LONG,message STRING);\n" +
            return "from every not "
                    + siddhiParams.getInputStreamNames().get(0)
                    + " for "
                    + siddhiParams.getParams().getDuration()
                    + " sec";
  }

  @Override
  public String selectStatement(SiddhiProcessorParams<SequenceParameters> siddhiParams) {
//    return getCustomOutputSelectStatement(params.getGraph());
    return "select *";
//    return "select currentTimeMillis() as s0timestamp, 'Customer has not arrived' as message";
  }

}
