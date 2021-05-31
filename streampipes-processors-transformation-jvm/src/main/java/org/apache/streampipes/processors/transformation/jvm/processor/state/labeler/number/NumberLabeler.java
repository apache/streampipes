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

package org.apache.streampipes.processors.transformation.jvm.processor.state.labeler.number;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.logging.api.Logger;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.processors.transformation.jvm.processor.state.labeler.model.Statement;
import org.apache.streampipes.processors.transformation.jvm.processor.state.labeler.model.StatementUtils;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.runtime.EventProcessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NumberLabeler implements EventProcessor<NumberLabelerParameters> {

  private static Logger LOG;
  private String sensorListValueProperty;
  private List<Statement> statements;

  @Override
  public void onInvocation(NumberLabelerParameters numberLabelerParameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    LOG = numberLabelerParameters.getGraph().getLogger(NumberLabeler.class);

    this.statements = StatementUtils.getStatements(
            numberLabelerParameters.getNumberValues(),
            numberLabelerParameters.getLabelStrings(),
            numberLabelerParameters.getComparators());

    this.sensorListValueProperty = numberLabelerParameters.getSensorListValueProperty();
  }

  @Override
  public void onEvent(Event inputEvent, SpOutputCollector out) {

    Double value = inputEvent.getFieldBySelector(this.sensorListValueProperty).getAsPrimitive().getAsDouble();

    Event resultEvent = StatementUtils.addLabel(inputEvent, value, this.statements, LOG);

    out.collect(resultEvent);
  }

  @Override
  public void onDetach() {
  }


}
