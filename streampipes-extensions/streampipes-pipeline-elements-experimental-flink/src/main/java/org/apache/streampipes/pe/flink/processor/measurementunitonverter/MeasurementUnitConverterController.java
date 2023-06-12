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

package org.apache.streampipes.pe.flink.processor.measurementunitonverter;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.api.runtime.ResolvesContainerProvidedOptions;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableOneOfStaticProperty;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.PropertyRequirementsBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.helpers.TransformOperations;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.units.UnitProvider;
import org.apache.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.apache.streampipes.wrapper.flink.FlinkDataProcessorProgram;

import com.github.jqudt.Unit;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MeasurementUnitConverterController extends
    FlinkDataProcessorDeclarer<MeasurementUnitConverterParameters> implements ResolvesContainerProvidedOptions {

  private static final String CONVERT_PROPERTY = "convert-property";
  private static final String OUTPUT_UNIT = "output-unit";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create(
            "org.apache.streampipes.processors.transformation.flink.measurementunitconverter")
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithUnaryMapping(PropertyRequirementsBuilder
                    .create()
                    .measurementUnitPresence()
                    .build(),
                Labels.withId(CONVERT_PROPERTY),
                PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .requiredSingleValueSelectionFromContainer(Labels.withId(OUTPUT_UNIT))
        .outputStrategy(OutputStrategies.transform(TransformOperations
            .dynamicMeasurementUnitTransformation(CONVERT_PROPERTY, OUTPUT_UNIT)))
        .build();
  }


  @Override
  public FlinkDataProcessorProgram<MeasurementUnitConverterParameters> getProgram(
      DataProcessorInvocation sepa,
      ProcessingElementParameterExtractor extractor) {

    String convertProperty = extractor.mappingPropertyValue(CONVERT_PROPERTY);
    String inputUnitId = extractor.measurementUnit(convertProperty, 0);
    String outputUnitId = sepa.getStaticProperties().stream().filter(sp -> sp
        .getInternalName().equals(OUTPUT_UNIT)).map(sp ->
        (RuntimeResolvableOneOfStaticProperty) sp).findFirst().get().getOptions().stream
        ().filter(o -> o.isSelected()).map(o -> o.getInternalName()).findFirst().get();
    extractor.selectedSingleValueFromRemote(OUTPUT_UNIT, String.class);

    Unit inputUnit = UnitProvider.INSTANCE.getUnit(inputUnitId);
    Unit outputUnit = UnitProvider.INSTANCE.getUnit(outputUnitId);

    MeasurementUnitConverterParameters staticParams = new MeasurementUnitConverterParameters(
        sepa,
        convertProperty,
        inputUnit,
        outputUnit
    );

    return new MeasurementUnitConverterProgram(staticParams);
  }

  @Override
  public List<Option> resolveOptions(String requestId, IStaticPropertyExtractor parameterExtractor) {
    try {
      EventProperty linkedEventProperty = parameterExtractor.getEventPropertyBySelector(CONVERT_PROPERTY);
      if (linkedEventProperty instanceof EventPropertyPrimitive && ((EventPropertyPrimitive) linkedEventProperty)
          .getMeasurementUnit() != null) {
        Unit measurementUnit = UnitProvider.INSTANCE.getUnit(((EventPropertyPrimitive) linkedEventProperty)
            .getMeasurementUnit().toString());
        URI type = measurementUnit.getType();
        List<Unit> availableUnits = UnitProvider.INSTANCE.getUnitsByType(type);
        return availableUnits
            .stream()
            .filter(unit -> !(unit.getResource().toString().equals(measurementUnit.getResource().toString())))
            .map(unit -> new Option(unit.getLabel(), unit.getResource().toString()))
            .collect(Collectors.toList());
      } else {
        return new ArrayList<>();
      }
    } catch (SpRuntimeException e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }
}
