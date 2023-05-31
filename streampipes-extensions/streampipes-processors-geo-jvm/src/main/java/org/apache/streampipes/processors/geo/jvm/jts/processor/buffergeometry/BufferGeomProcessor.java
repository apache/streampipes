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
package org.apache.streampipes.processors.geo.jvm.jts.processor.buffergeometry;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.processors.geo.jvm.jts.helper.SpGeometryBuilder;
import org.apache.streampipes.processors.geo.jvm.jts.helper.SpReprojectionBuilder;
import org.apache.streampipes.processors.geo.jvm.jts.helper.buffer.BufferSide;
import org.apache.streampipes.processors.geo.jvm.jts.helper.buffer.CapStyle;
import org.apache.streampipes.processors.geo.jvm.jts.helper.buffer.JoinStyle;
import org.apache.streampipes.processors.geo.jvm.jts.helper.buffer.SpBufferBuilder;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import org.locationtech.jts.geom.Geometry;
import org.opengis.util.FactoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BufferGeomProcessor extends StreamPipesDataProcessor {
  public static final String GEOM_KEY = "geometry-key";
  public static final String EPSG_KEY = "epsg-key";
  public static final String CAP_KEY = "cap-style-key";
  public static final String SEGMENTS_KEY = "segments-key";
  public static final String SIMPLIFY_FACTOR_KEY = "simplify-factor-key";
  public static final String DISTANCE_KEY = "distance-key";
  public static final String JOIN_KEY = "join-style-key";
  public static final String MITRE_LIMIT_KEY = "mitre-limit-key";
  public static final String SIDE_KEY = "side-key";
  public static final String GEOM_RUNTIME = "geometry-buffer";
  public static final String EPSG_RUNTIME = "epsg-buffer";
  private String geometryMapper;
  private String epsgMapper;
  private Integer capStyle;
  private Integer joinStyle;
  private Integer side;
  private Double mitreLimit;
  private Integer segments;
  private Double simplifyFactor;
  private Double distance;
  private Boolean singleSided;
  private static final Logger LOG = LoggerFactory.getLogger(BufferGeomProcessor.class);

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.geo.jvm.jts.processor.buffergeometry")
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
        .outputStrategy(OutputStrategies.append(
                EpProperties.stringEp(
                    Labels.withId(GEOM_KEY),
                    GEOM_RUNTIME,
                    "http://www.opengis.net/ont/geosparql#Geometry"
                ),
                EpProperties.numberEp(
                    Labels.withId(EPSG_KEY),
                    EPSG_RUNTIME,
                    "http://data.ign.fr/def/ignf#CartesianCS"
                )
            )
        )
        .requiredSingleValueSelection(
            Labels.withId(CAP_KEY),
            Options.from(
                CapStyle.Square.name(),
                CapStyle.Flat.name(),
                CapStyle.Round.name())
        )
        .requiredSingleValueSelection(
            Labels.withId(JOIN_KEY),
            Options.from(
                JoinStyle.Bevel.name(),
                JoinStyle.Mitre.name(),
                JoinStyle.Round.name())
        )
        .requiredSingleValueSelection(
            Labels.withId(SIDE_KEY),
            Options.from(
                BufferSide.Both.name(),
                BufferSide.Left.name(),
                BufferSide.Right.name())
        )
        .requiredIntegerParameter(
            Labels.withId(MITRE_LIMIT_KEY),
            5)
        .requiredIntegerParameter(
            Labels.withId(SEGMENTS_KEY),
            8
        )
        .requiredFloatParameter(
            Labels.withId(SIMPLIFY_FACTOR_KEY),
            0.01f
        )
        .requiredFloatParameter(
            Labels.withId(DISTANCE_KEY)
        )
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams parameters, SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {

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

    this.geometryMapper = parameters.extractor().mappingPropertyValue(GEOM_KEY);
    this.epsgMapper = parameters.extractor().mappingPropertyValue(EPSG_KEY);
    String readCapStyle = parameters.extractor().selectedSingleValue(CAP_KEY, String.class);
    String readJoinStyle = parameters.extractor().selectedSingleValue(JOIN_KEY, String.class);
    String readSide = parameters.extractor().selectedSingleValue(SIDE_KEY, String.class);
    this.mitreLimit = parameters.extractor().singleValueParameter(MITRE_LIMIT_KEY, Double.class);
    this.segments = parameters.extractor().singleValueParameter(SEGMENTS_KEY, Integer.class);
    this.simplifyFactor = parameters.extractor().singleValueParameter(SIMPLIFY_FACTOR_KEY, Double.class);
    this.distance = parameters.extractor().singleValueParameter(DISTANCE_KEY, Double.class);
    // transform names to numbers
    this.capStyle = 1;
    if (readCapStyle.equals(CapStyle.Square.name())) {
      this.capStyle = CapStyle.Square.getNumber();
    } else if (readCapStyle.equals(CapStyle.Flat.name())) {
      this.capStyle = CapStyle.Flat.getNumber();
    }
    // transform names to numbers
    this.joinStyle = 1;
    if (readJoinStyle.equals(JoinStyle.Bevel.name())) {
      this.joinStyle = JoinStyle.Bevel.getNumber();
    } else if (readJoinStyle.equals(JoinStyle.Mitre.name())) {
      this.joinStyle = JoinStyle.Mitre.getNumber();
    }
    // transform names to numbers
    this.singleSided = false;
    this.side = 0;
    if (readSide.equals(BufferSide.Right.name())) {
      this.side = BufferSide.Right.getNumber();
      this.singleSided = true;
    } else if (readSide.equals(BufferSide.Left.name())) {
      this.side = BufferSide.Left.getNumber();
      singleSided = true;
    }
  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) throws SpRuntimeException {
    String geom = event.getFieldBySelector(geometryMapper).getAsPrimitive().getAsString();
    Integer epsg = event.getFieldBySelector(epsgMapper).getAsPrimitive().getAsInt();
    Geometry geometry = SpGeometryBuilder.createSPGeom(geom, epsg);
    Geometry bufferGeom =
        SpBufferBuilder.createSpBuffer(
            geometry,
            distance,
            capStyle,
            joinStyle,
            mitreLimit,
            segments,
            simplifyFactor,
            singleSided,
            side);

    if (!bufferGeom.isEmpty()) {
      event.addField(GEOM_RUNTIME, bufferGeom.toText());
      event.addField(EPSG_RUNTIME, bufferGeom.getSRID());
      collector.collect(event);
    } else {
      LOG.warn("An empty polygon geometry is created and is not parsed into the stream");
    }

    collector.collect(event);

  }

  @Override
  public void onDetach() throws SpRuntimeException {
  }
}
