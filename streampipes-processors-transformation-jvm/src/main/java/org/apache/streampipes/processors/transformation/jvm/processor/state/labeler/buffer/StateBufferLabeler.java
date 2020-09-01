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

package org.apache.streampipes.processors.transformation.jvm.processor.state.labeler.buffer;

import com.google.common.math.Stats;
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

public class StateBufferLabeler implements EventProcessor<StateBufferLabelerParameters> {

  private static Logger LOG;
  private String sensorListValueProperty;
  private String stateProperty;
  private String stateFilter;
  private String selectedOperation;
  private List<Statement> statements;

  @Override
  public void onInvocation(StateBufferLabelerParameters stateBufferLabelerParameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    LOG = stateBufferLabelerParameters.getGraph().getLogger(StateBufferLabeler.class);

    this.sensorListValueProperty = stateBufferLabelerParameters.getSensorListValueProperty();
    this.stateProperty = stateBufferLabelerParameters.getStateProperty();
    this.stateFilter = stateBufferLabelerParameters.getStateFilter();
    this.selectedOperation = stateBufferLabelerParameters.getSelectedOperation();

    this.statements = StatementUtils.getStatements(
            stateBufferLabelerParameters.getNumberValues(),
            stateBufferLabelerParameters.getLabelStrings(),
            stateBufferLabelerParameters.getComparators());
  }

  @Override
  public void onEvent(Event inputEvent, SpOutputCollector out) {

    List<Double> values = inputEvent.getFieldBySelector(this.sensorListValueProperty).getAsList().parseAsSimpleType(Double.class);
    List<String> states = inputEvent.getFieldBySelector(this.stateProperty).getAsList().parseAsSimpleType(String.class);

    if (states.contains(this.stateFilter) || this.stateFilter.equals("*"))  {
      double calculatedValue;

      if (StateBufferLabelerController.MAXIMUM.equals(this.selectedOperation)) {
        calculatedValue = Stats.of(values).max();
      } else if (StateBufferLabelerController.MINIMUM.equals(this.selectedOperation)) {
        calculatedValue = Stats.of(values).min();
      } else {
        calculatedValue = Stats.of(values).mean();
      }

      Event resultEvent = StatementUtils.addLabel(inputEvent, calculatedValue, this.statements, LOG);
      out.collect(resultEvent);
    }
  }

  @Override
  public void onDetach() {
  }
}
