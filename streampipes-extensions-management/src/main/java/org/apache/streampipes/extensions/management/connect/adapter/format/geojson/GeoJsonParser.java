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

package org.apache.streampipes.extensions.management.connect.adapter.format.geojson;


import org.apache.streampipes.extensions.api.connect.EmitBinaryEvent;
import org.apache.streampipes.extensions.api.connect.exception.ParseException;
import org.apache.streampipes.extensions.management.connect.adapter.format.util.JsonEventProperty;
import org.apache.streampipes.extensions.management.connect.adapter.model.generic.Parser;
import org.apache.streampipes.model.connect.grounding.FormatDescription;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.vocabulary.SO;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.LineString;
import org.geojson.MultiLineString;
import org.geojson.MultiPoint;
import org.geojson.MultiPolygon;
import org.geojson.Point;
import org.geojson.Polygon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GeoJsonParser extends Parser {

  Logger logger = LoggerFactory.getLogger(GeoJsonParser.class);

  @Override
  public Parser getInstance(FormatDescription formatDescription) {
    return new GeoJsonParser();
  }

  @Override
  public void parse(InputStream data, EmitBinaryEvent emitBinaryEvent) throws ParseException {
    FeatureCollection geoFeature;
    Gson gson = new Gson();

    try {
      String dataString = CharStreams.toString(new InputStreamReader(data, Charsets.UTF_8));
      List<Map> features = (List) gson.fromJson(dataString, HashMap.class).get("features");

      for (Map feature : features) {
        byte[] bytes = gson.toJson(feature).getBytes();
        emitBinaryEvent.emit(bytes);
      }

    } catch (IOException e) {
      throw new ParseException(e.getMessage());
    }
  }

  @Override
  public EventSchema getEventSchema(List<byte[]> oneEvent) {
    EventSchema resultSchema = new EventSchema();

    Feature geoFeature = null;
    try {
      geoFeature = new ObjectMapper().readValue(oneEvent.get(0), Feature.class);

    } catch (IOException e) {
      logger.error(e.toString());
    }

    for (Map.Entry<String, Object> entry : geoFeature.getProperties().entrySet()) {
      EventProperty p = JsonEventProperty.getEventProperty(entry.getKey(), entry.getValue());
      resultSchema.addEventProperty(p);
    }

    List<EventProperty> eventProperties = parseGeometryField(geoFeature);
    eventProperties.forEach(eventProperty -> resultSchema.addEventProperty(eventProperty));

    return resultSchema;
  }

  private List<EventProperty> parseGeometryField(Feature geoFeature) {
    List<EventProperty> eventProperties = new LinkedList<>();

    if (geoFeature.getGeometry() instanceof Point) {
      Point point = (Point) geoFeature.getGeometry();
      eventProperties.add(getEventPropertyGeoJson("longitude", point.getCoordinates().getLongitude(),
          "http://www.w3.org/2003/01/geo/wgs84_pos#long"));
      eventProperties.add(getEventPropertyGeoJson("latitude", point.getCoordinates().getLatitude(),
          "http://www.w3.org/2003/01/geo/wgs84_pos#lat"));
      if (point.getCoordinates().hasAltitude()) {
        eventProperties.add(getEventPropertyGeoJson("altitude", point.getCoordinates().getAltitude(), SO.ALTITUDE));
      }

    } else if (geoFeature.getGeometry() instanceof LineString) {
      LineString lineString = (LineString) geoFeature.getGeometry();
      eventProperties.add(JsonEventProperty.getEventProperty("coorindatesLineString", lineString.getCoordinates()));

    } else if (geoFeature.getGeometry() instanceof Polygon) {
      Polygon polygon = (Polygon) geoFeature.getGeometry();
      eventProperties.add(JsonEventProperty.getEventProperty("coorindatesPolygon", polygon.getCoordinates()));

    } else if (geoFeature.getGeometry() instanceof MultiPoint) {
      MultiPoint multiPoint = (MultiPoint) geoFeature.getGeometry();
      eventProperties.add(JsonEventProperty.getEventProperty("coorindatesMultiPoint", multiPoint.getCoordinates()));

    } else if (geoFeature.getGeometry() instanceof MultiLineString) {
      MultiLineString multiLineString = (MultiLineString) geoFeature.getGeometry();
      eventProperties.add(
          JsonEventProperty.getEventProperty("coorindatesMultiLineString", multiLineString.getCoordinates()));

    } else if (geoFeature.getGeometry() instanceof MultiPolygon) {
      MultiPolygon multiPolygon = (MultiPolygon) geoFeature.getGeometry();
      eventProperties.add(JsonEventProperty.getEventProperty("coorindatesMultiPolygon", multiPolygon.getCoordinates()));
    } else {
      logger.error("No geometry field found in geofeature: " + geoFeature.toString());
    }

    return eventProperties;

  }

  private EventProperty getEventPropertyGeoJson(String name, Object value, String domain) {
    EventProperty eventProperty = JsonEventProperty.getEventProperty(name, value);
    try {
      ((EventPropertyPrimitive) eventProperty).setDomainProperties(Arrays.asList(new URI(domain)));

    } catch (URISyntaxException e) {
      logger.error(e.getMessage());
    }
    return eventProperty;
  }


}
