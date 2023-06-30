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


package org.apache.streampipes.processors.transformation.jvm.processor.measurementconverter;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.extensions.api.runtime.ResolvesContainerProvidedOptions;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableOneOfStaticProperty;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.PropertyRequirementsBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.helpers.TransformOperations;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.units.UnitProvider;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import com.github.jqudt.Quantity;
import com.github.jqudt.Unit;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MeasurementUnitConverterProcessor extends StreamPipesDataProcessor
    implements ResolvesContainerProvidedOptions {

  private static final String CONVERT_PROPERTY = "convert-property";
  private static final String OUTPUT_UNIT = "output-unit";

  private Unit inputUnit;
  private Unit outputUnit;

  private String convertProperty;

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create(
            "org.apache.streampipes.processors.transformation.jvm.measurementunitconverter")
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
  public void onInvocation(ProcessorParams parameters, SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    var extractor = parameters.extractor();

    this.convertProperty = extractor.mappingPropertyValue(CONVERT_PROPERTY);
    String runtimeName = extractor.getEventPropertyBySelector(this.convertProperty).getRuntimeName();
    String inputUnitId = extractor.measurementUnit(runtimeName, 0);
    String outputUnitId = parameters.getGraph().getStaticProperties().stream().filter(sp -> sp
            .getInternalName().equals(OUTPUT_UNIT))
        .map(sp ->
            (RuntimeResolvableOneOfStaticProperty) sp)
        .findFirst()
        .get().getOptions()
        .stream
            ().filter(Option::isSelected).map(Option::getInternalName).findFirst().get();

    this.inputUnit = UnitProvider.INSTANCE.getUnit(inputUnitId);
    this.outputUnit = UnitProvider.INSTANCE.getUnit(outputUnitId);
  }

  @Override
  public void onEvent(Event in, SpOutputCollector out) throws SpRuntimeException {
    double value = in.getFieldBySelector(convertProperty).getAsPrimitive().getAsDouble();

    // transform old value to new unit
    Quantity obs = new Quantity(value, inputUnit);
    try {
      Double newValue = obs.convertTo(outputUnit).getValue();
      in.updateFieldBySelector(convertProperty, newValue);
      out.collect(in);
    } catch (IllegalAccessException e) {
      throw new SpRuntimeException("Could not convert measurement", e);
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }

  @Override
  public List<Option> resolveOptions(String staticPropertyInternalName,
                                     IStaticPropertyExtractor parameterExtractor) {
    try {
      String selector = parameterExtractor.mappingPropertyValue(CONVERT_PROPERTY);
      EventProperty linkedEventProperty = parameterExtractor.getEventPropertyBySelector(selector);
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
