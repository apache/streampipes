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

package org.apache.streampipes.processors.geo.jvm.jts.processor.validation;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.processors.geo.jvm.jts.helper.SpGeometryBuilder;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.standalone.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.operation.valid.IsValidOp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeometryValidationProcessor extends StreamPipesDataProcessor {
  public static final String GEOM_KEY = "geom-key";
  public static final String EPSG_KEY = "epsg-key";
  public static final String VALIDATION_OUTPUT_KEY = "validation-output-key";
  public static final String VALIDATION_TYPE_KEY = "validation-type-key";
  private String geometryMapper;
  private String epsgMapper;
  private Integer validationOutput;
  private Integer validationType;

  private static final Logger LOG = LoggerFactory.getLogger(GeometryValidationProcessor.class);

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.geo.jvm.jts.processor.validation")
        .category(DataProcessorType.GEO)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithUnaryMapping(
                EpRequirements.domainPropertyReq("http://www.opengis.net/ont/geosparql#Geometry"),
                Labels.withId(GEOM_KEY),
                PropertyScope.MEASUREMENT_PROPERTY)
            .requiredPropertyWithUnaryMapping(
                EpRequirements.domainPropertyReq("http://data.ign.fr/def/ignf#CartesianCS"),
                Labels.withId(EPSG_KEY),
                PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .outputStrategy(OutputStrategies.keep())
        .requiredSingleValueSelection(
            Labels.withId(VALIDATION_OUTPUT_KEY),
            Options.from(
                ValidationOutput.VALID.name(),
                ValidationOutput.INVALID.name()
            )
        )
        .requiredMultiValueSelection(
            Labels.withId(VALIDATION_TYPE_KEY),
            Options.from(
                ValidationType.IsEmpty.name(),
                ValidationType.IsSimple.name(),
                ValidationType.IsValid.name()
            )
        )
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams parameters, SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {

    this.geometryMapper = parameters.extractor().mappingPropertyValue(GEOM_KEY);
    this.epsgMapper = parameters.extractor().mappingPropertyValue(EPSG_KEY);

    String readValidationOutput = parameters.extractor().selectedSingleValue(VALIDATION_OUTPUT_KEY, String.class);
    String readValidationType = parameters.extractor().selectedSingleValue(VALIDATION_TYPE_KEY, String.class);

    if (readValidationOutput.equals(ValidationOutput.VALID.name())) {
      this.validationOutput = ValidationOutput.VALID.getNumber(); // 1
    } else {
      this.validationOutput = 2;
    }

    if (readValidationType.equals(ValidationType.IsEmpty.name())) {
      this.validationType = ValidationType.IsEmpty.getNumber(); // 1
    } else if (readValidationType.equals(ValidationType.IsValid.name())) {
      this.validationType = ValidationType.IsValid.getNumber(); // 2
    } else {
      this.validationType = ValidationType.IsSimple.getNumber(); // 3
    }
  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) throws SpRuntimeException {
    String geom = event.getFieldBySelector(geometryMapper).getAsPrimitive().getAsString();
    Integer sourceEpsg = event.getFieldBySelector(epsgMapper).getAsPrimitive().getAsInt();

    Geometry geometry = SpGeometryBuilder.createSPGeom(geom, sourceEpsg);

    boolean itIsValid = false;

    switch (validationType) {
      case 1:
        itIsValid = geometry.isEmpty();
        break;
      case 2:
        itIsValid = geometry.isSimple();
        break;
      case 3:
        IsValidOp validater = new IsValidOp(geometry);
        validater.setSelfTouchingRingFormingHoleValid(true);
        itIsValid = validater.isValid();
        if (itIsValid = false) {
          validater.getValidationError();
        }
    }


    switch (validationOutput) {
      case 1:
        if (itIsValid) {
          collector.collect(event);
        }
        break;
      case 2:
        if (!itIsValid) {
          collector.collect(event);
        }
        break;
    }
  }



  @Override
  public void onDetach() throws SpRuntimeException {

  }
}
