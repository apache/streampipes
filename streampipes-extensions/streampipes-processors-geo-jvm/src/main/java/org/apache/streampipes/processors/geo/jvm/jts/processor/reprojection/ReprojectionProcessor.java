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

package org.apache.streampipes.processors.geo.jvm.jts.processor.reprojection;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.processors.geo.jvm.jts.exceptions.SpNotSupportedGeometryException;
import org.apache.streampipes.processors.geo.jvm.jts.helper.SpGeometryBuilder;
import org.apache.streampipes.processors.geo.jvm.jts.helper.SpReprojectionBuilder;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import org.locationtech.jts.geom.Geometry;
import org.opengis.util.FactoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReprojectionProcessor extends StreamPipesDataProcessor {
  public static final String GEOM_KEY = "geom-key";
  public static final String SOURCE_EPSG_KEY = "source-epsg-key";
  public static final String TARGET_EPSG_KEY = "target-epsg-key";
  public static final String GEOMETRY_RUNTIME = "geometry";
  public static final String EPSG_RUNTIME = "epsg";
  private String geometryMapper;
  private String sourceEpsgMapper;
  private Integer targetEpsg;
  private static final Logger LOG = LoggerFactory.getLogger(ReprojectionProcessor.class);

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.geo.jvm.jts.processor.reprojection")
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
                Labels.withId(SOURCE_EPSG_KEY),
                PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .outputStrategy(OutputStrategies.keep())
        .requiredIntegerParameter(Labels.withId(TARGET_EPSG_KEY), 32632)
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams parameters, SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {

    this.geometryMapper = parameters.extractor().mappingPropertyValue(GEOM_KEY);
    this.sourceEpsgMapper = parameters.extractor().mappingPropertyValue(SOURCE_EPSG_KEY);
    this.targetEpsg = parameters.extractor().singleValueParameter(TARGET_EPSG_KEY, Integer.class);

    // check if SIS DB is set up with imported data or is null
    try {
      if (SpReprojectionBuilder.isSisConfigurationValid()){
        LOG.info("SIS DB Settings successful checked ");
      } else {
        LOG.warn("The required EPSG database is not imported");
        throw new SpRuntimeException("The required EPSG database is not imported");
      }
    } catch (FactoryException e) {
      throw new SpRuntimeException("Something unexpected happened " + e);
    }

    // check if SIS DB has the supported 9.9.1 Version.
    try {
      if (!SpReprojectionBuilder.isSisDbCorrectVersion()) {
        LOG.warn("Not supported EPSG DB is used.");
        throw new SpRuntimeException("Your current EPSG DB version " + SpReprojectionBuilder.getSisDbVersion()
            + " is not the supported 9.9.1 version. ");
      }
    } catch (FactoryException e) {
      throw new SpRuntimeException("Something unexpected happened " + e);
    }

    // checks if Input EPSG in valid and exists in EPSG DB
    if (!SpReprojectionBuilder.isSisEpsgValid(this.targetEpsg)) {
      throw new SpRuntimeException("Your chosen EPSG Code " + this.targetEpsg + " is not valid. "
          + "Check EPSG on https://spatialreference.org");
    }
  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) throws SpRuntimeException {
    String geom = event.getFieldBySelector(geometryMapper).getAsPrimitive().getAsString();
    Integer sourceEpsg = event.getFieldBySelector(sourceEpsgMapper).getAsPrimitive().getAsInt();

    Geometry geometry = SpGeometryBuilder.createSPGeom(geom, sourceEpsg);

    Geometry reprojected;
    try {
      reprojected = SpReprojectionBuilder.reprojectSpGeometry(geometry, targetEpsg);
    } catch (SpNotSupportedGeometryException e) {
      reprojected = SpGeometryBuilder.createEmptyGeometry(geometry);
    }

    if (!reprojected.isEmpty()) {
      event.updateFieldBySelector("s0::" + EPSG_RUNTIME, targetEpsg);
      event.updateFieldBySelector("s0::" + GEOMETRY_RUNTIME, reprojected.toText());

      collector.collect(event);
    } else {
      LOG.warn("An empty point geometry is created"
          + " due invalid input values. Check used epsg Code:" + targetEpsg);
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }
}
