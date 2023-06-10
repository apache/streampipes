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

package org.apache.streampipes.processors.transformation.jvm.processor.booloperator.logical;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.logical.enums.BooleanOperatorType;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.logical.operations.IBoolOperation;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.logical.operations.factory.BoolOperationFactory;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.sdk.utils.Datatypes;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import java.util.List;

import static org.apache.streampipes.processors.transformation.jvm.processor.booloperator.logical.enums.BooleanOperatorType.NOT;

public class BooleanOperatorProcessor extends StreamPipesDataProcessor {

  private static final String BOOLEAN_PROCESSOR_OUT_KEY = "boolean-operations-result";
  private static final String BOOLEAN_OPERATOR_TYPE = "operator-field";
  private static final String PROPERTIES_LIST = "properties-field";
  private BooleanOperationInputConfigs configs;

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.transformation.jvm.booloperator.logical")
        .withAssets(Assets.DOCUMENTATION)
        .withLocales(Locales.EN)
        .category(DataProcessorType.BOOLEAN_OPERATOR)
        .requiredStream(
            StreamRequirementsBuilder
                .create()
                .requiredPropertyWithNaryMapping(EpRequirements.booleanReq(),
                    Labels.withId(PROPERTIES_LIST), PropertyScope.NONE)
                .build())
        .requiredSingleValueSelection(Labels.withId(BOOLEAN_OPERATOR_TYPE), Options.from(
            BooleanOperatorType.AND.operator(),
            BooleanOperatorType.OR.operator(),
            BooleanOperatorType.NOT.operator(),
            BooleanOperatorType.XOR.operator(),
            BooleanOperatorType.X_NOR.operator(),
            BooleanOperatorType.NOR.operator()))
        .outputStrategy(OutputStrategies.append(
            PrimitivePropertyBuilder.create(
                    Datatypes.String, BOOLEAN_PROCESSOR_OUT_KEY)
                .build()))
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams processorParams, SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext eventProcessorRuntimeContext) throws SpRuntimeException {
    List<String> properties = processorParams.extractor().mappingPropertyValues(PROPERTIES_LIST);
    String operator = processorParams.extractor().selectedSingleValue(BOOLEAN_OPERATOR_TYPE, String.class);
    BooleanOperationInputConfigs configs =
        new BooleanOperationInputConfigs(properties, BooleanOperatorType.getBooleanOperatorType(operator));
    preChecks(configs);
    this.configs = configs;
  }

  @Override
  public void onEvent(Event event, SpOutputCollector spOutputCollector) throws SpRuntimeException {
    List<String> properties = configs.getProperties();
    BooleanOperatorType operatorType = configs.getOperator();
    Boolean firstProperty = event.getFieldBySelector(properties.get(0)).getAsPrimitive().getAsBoolean();
    IBoolOperation<Boolean> boolOperation = BoolOperationFactory.getBoolOperation(operatorType);
    Boolean result;
    if (properties.size() == 1) {
      // support for NOT operator
      result = boolOperation.evaluate(firstProperty, firstProperty);

    } else {
      Boolean secondProperty = event.getFieldBySelector(properties.get(1)).getAsPrimitive().getAsBoolean();
      result = boolOperation.evaluate(firstProperty, secondProperty);

      //loop through rest of the properties to get final result
      for (int i = 2; i < properties.size(); i++) {
        result =
            boolOperation.evaluate(result, event.getFieldBySelector(properties.get(i)).getAsPrimitive().getAsBoolean());
      }

    }
    event.addField(BOOLEAN_PROCESSOR_OUT_KEY, result);
    spOutputCollector.collect(event);
  }

  @Override
  public void onDetach() throws SpRuntimeException {
    configs = null;
  }

  private void preChecks(BooleanOperationInputConfigs configs) {
    BooleanOperatorType operatorType = configs.getOperator();
    List<String> properties = configs.getProperties();
    if (operatorType == NOT && properties.size() != 1) {
      throw new SpRuntimeException("NOT operator can operate only on single operand");
    } else if (operatorType != NOT && properties.size() < 2) {
      throw new SpRuntimeException("Number of operands are less that 2");
    }
  }
}
