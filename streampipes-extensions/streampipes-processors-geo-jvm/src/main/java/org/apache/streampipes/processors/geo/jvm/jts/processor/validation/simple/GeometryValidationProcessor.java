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

package org.apache.streampipes.processors.geo.jvm.jts.processor.validation.simple;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.processors.geo.jvm.jts.helper.SpGeometryBuilder;
import org.apache.streampipes.processors.geo.jvm.jts.processor.validation.ValidationOutput;
import org.apache.streampipes.processors.geo.jvm.jts.processor.validation.ValidationType;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import org.locationtech.jts.geom.Geometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GeometryValidationProcessor extends StreamPipesDataProcessor {
  public static final String GEOM_KEY = "geom-key";
  public static final String EPSG_KEY = "epsg-key";
  public static final String VALIDATION_OUTPUT_KEY = "validation-output-key";
  public static final String VALIDATION_TYPE_KEY = "validation-type-key";
  private String geometryMapper;
  private String epsgMapper;
  private String outputChoice;
  private boolean isEmptySelected;
  private boolean isSimpleSelected;
  private boolean isMultiSelected = false;

  private static final Logger LOG = LoggerFactory.getLogger(GeometryValidationProcessor.class);

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.geo.jvm.jts.processor.validation.simple")
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
                ValidationType.IsSimple.name()
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
    List<String> readValidationType = parameters.extractor().selectedMultiValues(VALIDATION_TYPE_KEY, String.class);

    if (readValidationOutput.equals(ValidationOutput.VALID.name())) {
      this.outputChoice = ValidationOutput.VALID.name();
    } else {
      this.outputChoice = ValidationOutput.INVALID.name();
    }
    if (readValidationType.size() == 0) {
      throw new SpRuntimeException("You have to chose at least one validation type");
    } else if (readValidationType.size() == 2) {
      this.isEmptySelected = true;
      this.isSimpleSelected = true;
      this.isMultiSelected = true;
    } else if (readValidationType.get(0).equals(ValidationType.IsEmpty.name())) {
      this.isEmptySelected = true;
      this.isSimpleSelected = false;
    } else if (readValidationType.get(0).equals(ValidationType.IsSimple.name())) {
      this.isEmptySelected = false;
      this.isSimpleSelected = true;
    }
  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) throws SpRuntimeException {
    String geom = event.getFieldBySelector(geometryMapper).getAsPrimitive().getAsString();
    Integer sourceEpsg = event.getFieldBySelector(epsgMapper).getAsPrimitive().getAsInt();

    Geometry geometry = SpGeometryBuilder.createSPGeom(geom, sourceEpsg);

    boolean itIsNotValid = false;

    if (isMultiSelected) {
      itIsNotValid = geometry.isEmpty() || !geometry.isSimple();
    } else if (isEmptySelected) {
      itIsNotValid = geometry.isEmpty();
    } else if (isSimpleSelected) {
      itIsNotValid = !geometry.isSimple();
    }

    if (outputChoice.equals(ValidationOutput.VALID.name())) {
      if (!itIsNotValid) {
        collector.collect(event);
      }
    } else if (outputChoice.equals(ValidationOutput.INVALID.name())) {
      if (itIsNotValid) {
        collector.collect(event);
      }
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }
}
