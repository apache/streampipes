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

package org.apache.streampipes.processors.transformation.jvm.processor.state.labeler;

import com.google.common.math.Stats;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.logging.api.Logger;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.processors.transformation.jvm.processor.state.labeler.model.Statement;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.runtime.EventProcessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StateLabeler implements EventProcessor<StateLabelerParameters> {

  private static Logger LOG;
  private String sensorListValueProperty;
  private String stateProperty;
  private String stateFilter;
  private String selectedOperation;
  private List<Statement> statements;

  @Override
  public void onInvocation(StateLabelerParameters stateBufferParameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    LOG = stateBufferParameters.getGraph().getLogger(StateLabeler.class);

    this.sensorListValueProperty = stateBufferParameters.getSensorListValueProperty();
    this.stateProperty = stateBufferParameters.getStateProperty();
    this.stateFilter = stateBufferParameters.getStateFilter();
    this.selectedOperation = stateBufferParameters.getSelectedOperation();

    this.statements = new ArrayList<>();

    for (String s : stateBufferParameters.getStatementsStrings()) {
      Statement statement = Statement.getStatement(s);
      if (statement == null) {
        throw new SpRuntimeException("Statement: " + s + " is not correctly formatted");
      }
      this.statements.add(statement);
    }
    Collections.reverse(this.statements);

  }

  @Override
  public void onEvent(Event inputEvent, SpOutputCollector out) {

    List<Double> values = inputEvent.getFieldBySelector(this.sensorListValueProperty).getAsList().parseAsSimpleType(Double.class);
    List<String> states = inputEvent.getFieldBySelector(this.stateProperty).getAsList().parseAsSimpleType(String.class);

    if (states.contains(this.stateFilter) || this.stateFilter.equals("*"))  {
      double calculatedValue;

      if (StateLabelerController.MAXIMUM.equals(this.selectedOperation)) {
        calculatedValue = Stats.of(values).max();
      } else if (StateLabelerController.MINIMUM.equals(this.selectedOperation)) {
        calculatedValue = Stats.of(values).min();
      } else {
        calculatedValue = Stats.of(values).mean();
      }

      String label = getLabel(calculatedValue);
      if (label != null) {
        inputEvent.addField(StateLabelerController.LABEL, label);
        out.collect(inputEvent);
      } else {
        LOG.info("No condition of statements was fulfilled, add a default case (*) to the statements");
      }

    }
  }

  @Override
  public void onDetach() {
  }

  private String getLabel(double calculatedValue) {
    for (Statement statement : this.statements) {
      if (condition(statement, calculatedValue)) {
        return statement.getLabel();
      }
    }
    return null;
  }

  private boolean condition(Statement statement, double calculatedValue) {
    if (">".equals(statement.getOperator())) {
      return calculatedValue > statement.getValue();
    } else if ("<".equals(statement.getOperator())) {
      return calculatedValue < statement.getValue();
    } else if ("=".equals(statement.getOperator())) {
      return calculatedValue == statement.getValue();
    } else {
      return true;
    }

  }
}
