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

package org.apache.streampipes.processors.geo.jvm.jts.processor.validation.complex;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.monitoring.SpMonitoringManager;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.monitoring.SpLogEntry;
import org.apache.streampipes.model.monitoring.SpLogMessage;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.processors.geo.jvm.jts.exceptions.SpJtsGeoemtryException;
import org.apache.streampipes.processors.geo.jvm.jts.helper.SpGeometryBuilder;
import org.apache.streampipes.processors.geo.jvm.jts.processor.validation.ValidationOutput;
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
import org.locationtech.jts.operation.valid.IsValidOp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopologyValidationProcessor extends StreamPipesDataProcessor {
  public static final String GEOM_KEY = "geom-key";
  public static final String EPSG_KEY = "epsg-key";
  public static final String VALIDATION_OUTPUT_KEY = "validation-output-key";
  public static final String LOG_OUTPUT_KEY = "log-output-key";
  private String geometryMapper;
  private String epsgMapper;
  private String outputChoice;
  private boolean isLogOutput;
  ProcessorParams params;
  private static final Logger LOG = LoggerFactory.getLogger(TopologyValidationProcessor.class);

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.geo.jvm.jts.processor.validation.complex")
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
        .requiredSlideToggle(
            Labels.withId(LOG_OUTPUT_KEY),
            false)
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams parameters, SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {

    this.params = parameters;
    this.geometryMapper = parameters.extractor().mappingPropertyValue(GEOM_KEY);
    this.epsgMapper = parameters.extractor().mappingPropertyValue(EPSG_KEY);
    this.isLogOutput = parameters.extractor().slideToggleValue(LOG_OUTPUT_KEY);
    String readValidationOutput = parameters.extractor().selectedSingleValue(VALIDATION_OUTPUT_KEY, String.class);

    if (readValidationOutput.equals(ValidationOutput.VALID.name())) {
      this.outputChoice = ValidationOutput.VALID.name();
    } else {
      this.outputChoice = ValidationOutput.INVALID.name();
    }
  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) throws SpRuntimeException {
    String geom = event.getFieldBySelector(geometryMapper).getAsPrimitive().getAsString();
    Integer epsg = event.getFieldBySelector(epsgMapper).getAsPrimitive().getAsInt();

    Geometry geometry = SpGeometryBuilder.createSPGeom(geom, epsg);
    IsValidOp validator = new IsValidOp(geometry);
    validator.setSelfTouchingRingFormingHoleValid(true);
    boolean itIsValid = validator.isValid();
    if (!itIsValid){
      if (isLogOutput) {
        SpMonitoringManager.INSTANCE.addErrorMessage(params.getGraph().getElementId(),
            SpLogEntry.from(System.currentTimeMillis(),
                SpLogMessage.from(new SpJtsGeoemtryException(
                    validator.getValidationError().toString()))));
      }
    }

    if (outputChoice.equals(ValidationOutput.VALID.name())) {
      if (itIsValid) {
        collector.collect(event);
      }
    } else if (outputChoice.equals(ValidationOutput.INVALID.name())) {
      if (!itIsValid) {
        collector.collect(event);
      }
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }
}
