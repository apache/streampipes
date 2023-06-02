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

package org.apache.streampipes.processors.geo.jvm.latlong.processor.revgeocoder.geocityname;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.processors.geo.jvm.latlong.processor.revgeocoder.geocityname.geocode.GeoName;
import org.apache.streampipes.processors.geo.jvm.latlong.processor.revgeocoder.geocityname.geocode.ReverseGeoCode;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.vocabulary.Geo;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import org.apache.http.client.fluent.Request;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

public class GeoCityNameRevdecodeProcessor extends StreamPipesDataProcessor {
  private static final String LATITUDE_MAPPING_KEY = "latitude-mapping-key";
  private static final String LONGITUDE_MAPPING_KEY = "longitude-mapping-key";
  private static final String GEONAME_RUNTIME_NAME = "geoname";
  String latitudeFieldMapper;
  String longitudeFieldMapper;
  private static final String CITIES_DATASET_URL = "http://download.geonames"
      + ".org/export/dump/cities1000.zip";
  private ReverseGeoCode reverseGeoCode;

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder
        .create("org.apache.streampipes.processors.geo.jvm.latlong.processor.revgeocoder.geocityname")
        .category(DataProcessorType.GEO)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithUnaryMapping(EpRequirements.domainPropertyReq(Geo.LAT),
                Labels.withId(LATITUDE_MAPPING_KEY),
                PropertyScope.NONE)
            .requiredPropertyWithUnaryMapping(EpRequirements.domainPropertyReq(Geo.LNG),
                Labels.withId(LONGITUDE_MAPPING_KEY),
                PropertyScope.NONE)
            .build())
        .outputStrategy(OutputStrategies.append(
            EpProperties.stringEp(Labels.empty(), GEONAME_RUNTIME_NAME, "http://schema.org/city")
        ))
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams parameters, SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    this.latitudeFieldMapper = parameters.extractor().mappingPropertyValue(LATITUDE_MAPPING_KEY);
    this.longitudeFieldMapper = parameters.extractor().mappingPropertyValue(LONGITUDE_MAPPING_KEY);

    try {
      InputStream stream = downloadCitiesDataSet();
      if (stream != null) {
        ZipInputStream zipInputStream = null;
        zipInputStream = new ZipInputStream(stream);
        this.reverseGeoCode = new ReverseGeoCode(zipInputStream, false);
      }
    } catch (IOException e) {
      throw new SpRuntimeException("Could not download cities file...");
    }
  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) throws SpRuntimeException {

    Double latitude = event.getFieldBySelector(latitudeFieldMapper).getAsPrimitive().getAsDouble();
    Double longitude = event.getFieldBySelector(longitudeFieldMapper).getAsPrimitive().getAsDouble();

    GeoName geoName = this.reverseGeoCode.nearestPlace(latitude, longitude);

    event.addField(GEONAME_RUNTIME_NAME, geoName.name + " | " + geoName.country);
    collector.collect(event);
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }

  private InputStream downloadCitiesDataSet() throws IOException {
    byte[] citiesDataset = Request.Get(CITIES_DATASET_URL).execute().returnContent().asBytes();
    return new ByteArrayInputStream(citiesDataset);
  }
}
