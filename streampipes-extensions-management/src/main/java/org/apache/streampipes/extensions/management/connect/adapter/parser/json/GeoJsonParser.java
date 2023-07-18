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

package org.apache.streampipes.extensions.management.connect.adapter.parser.json;

import org.apache.streampipes.commons.exceptions.connect.ParseException;
import org.apache.streampipes.extensions.api.connect.IParserEventHandler;
import org.apache.streampipes.extensions.management.connect.adapter.parser.util.JsonEventProperty;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.sdk.builder.adapter.GuessSchemaBuilder;
import org.apache.streampipes.vocabulary.Geo;
import org.apache.streampipes.vocabulary.SO;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.geojson.Feature;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GeoJsonParser extends JsonParser {

  private static final Logger LOG = LoggerFactory.getLogger(JsonArrayKeyParser.class);

  @Override
  public GuessSchema getGuessSchema(InputStream inputStream) {
    Feature geoFeature = null;
    try {
      geoFeature = new ObjectMapper().readValue(inputStream, Feature.class);

    } catch (IOException e) {
      throw new ParseException("Could not parse geo json into a feature type", e);
    }

    List<EventProperty> eventProperties = new LinkedList<>();
    var sampleValues = new HashMap<String, Object>();

    if (geoFeature.getGeometry() instanceof Point) {
      Point point = (Point) geoFeature.getGeometry();
      eventProperties.add(
          getEventPropertyGeoJson(
              GeoJsonConstants.LONGITUDE,
              point.getCoordinates().getLongitude(),
              Geo.LNG));
      eventProperties.add(
          getEventPropertyGeoJson(
              GeoJsonConstants.LATITUDE,
              point.getCoordinates().getLatitude(),
              Geo.LAT));

      sampleValues.put(GeoJsonConstants.LONGITUDE,
          point.getCoordinates().getLongitude());
      sampleValues.put(GeoJsonConstants.LATITUDE,
          point.getCoordinates().getLatitude());
      if (point.getCoordinates().hasAltitude()) {
        eventProperties.add(
            getEventPropertyGeoJson(GeoJsonConstants.ALTITUDE, point.getCoordinates().getAltitude(), SO.ALTITUDE));
        point.getCoordinates().getAltitude();
      }

    } else if (geoFeature.getGeometry() instanceof LineString) {
      LineString lineString = (LineString) geoFeature.getGeometry();
      eventProperties.add(
          JsonEventProperty.getEventProperty(GeoJsonConstants.COORDINATES_LINE_STRING, lineString.getCoordinates()));
      sampleValues.put(GeoJsonConstants.COORDINATES_LINE_STRING,
          lineString.getCoordinates());
    } else if (geoFeature.getGeometry() instanceof Polygon) {
      Polygon polygon = (Polygon) geoFeature.getGeometry();
      eventProperties.add(
          JsonEventProperty.getEventProperty(GeoJsonConstants.COORDINATES_POLYGON, polygon.getCoordinates()));
      sampleValues.put(GeoJsonConstants.COORDINATES_POLYGON,
          polygon.getCoordinates());
    } else if (geoFeature.getGeometry() instanceof MultiPoint) {
      MultiPoint multiPoint = (MultiPoint) geoFeature.getGeometry();
      eventProperties.add(
          JsonEventProperty.getEventProperty(GeoJsonConstants.COORDINATES_MULTI_POINT, multiPoint.getCoordinates()));
      sampleValues.put(GeoJsonConstants.COORDINATES_MULTI_POINT,
          multiPoint.getCoordinates());
    } else if (geoFeature.getGeometry() instanceof MultiLineString) {
      MultiLineString multiLineString = (MultiLineString) geoFeature.getGeometry();
      eventProperties.add(
          JsonEventProperty.getEventProperty(GeoJsonConstants.COORDINATES_LINE_STRING,
              multiLineString.getCoordinates()));
      sampleValues.put(GeoJsonConstants.COORDINATES_LINE_STRING,
          multiLineString.getCoordinates());
    } else if (geoFeature.getGeometry() instanceof MultiPolygon) {
      MultiPolygon multiPolygon = (MultiPolygon) geoFeature.getGeometry();
      eventProperties.add(JsonEventProperty.getEventProperty(GeoJsonConstants.COORDINATES_MULTI_POLYGON,
          multiPolygon.getCoordinates()));
      sampleValues.put(GeoJsonConstants.COORDINATES_MULTI_POLYGON,
          multiPolygon.getCoordinates());
    } else {
      LOG.error("No geometry field found in geofeature: " + geoFeature.toString());
    }


    for (Map.Entry<String, Object> entry : geoFeature.getProperties().entrySet()) {
      EventProperty p = JsonEventProperty.getEventProperty(entry.getKey(), entry.getValue());
      eventProperties.add(p);
      sampleValues.put(p.getRuntimeName(),
          entry.getValue());
    }

    var schemaBuilder = GuessSchemaBuilder.create();
    eventProperties.forEach(schemaBuilder::property);
    sampleValues.forEach(schemaBuilder::sample);

    return schemaBuilder.build();
  }

  @Override
  public void parse(InputStream inputStream, IParserEventHandler handler) throws ParseException {
    Map<String, Object> event = toMap(inputStream, Map.class);
    handler.handle(geoJsonFormatter(event));
  }

  private EventProperty getEventPropertyGeoJson(String name, Object value, String domain) {
    EventProperty eventProperty = JsonEventProperty.getEventProperty(name, value);
    try {
      ((EventPropertyPrimitive) eventProperty).setDomainProperties(Arrays.asList(new URI(domain)));

    } catch (URISyntaxException e) {
      LOG.error(e.getMessage());
    }
    return eventProperty;
  }

  private Map<String, Object> geoJsonFormatter(Map<String, Object> map) {
    Map<String, Object> geoJson = new HashMap<String, Object>();
    Boolean foundGeometry = false;
    Boolean foundProperties = false;

    for (Map.Entry<String, Object> entry : map.entrySet()) {
      if (entry.getKey().equalsIgnoreCase("GEOMETRY")) {
        foundGeometry = true;
        geoJson.putAll(formatGeometryField((Map<String, Object>) entry.getValue()));
      }
      if (entry.getKey().equalsIgnoreCase("PROPERTIES")) {
        foundProperties = true;
        for (Map.Entry<String, Object> innerEntry : ((Map<String, Object>) entry.getValue()).entrySet()) {
          geoJson.put(innerEntry.getKey(), innerEntry.getValue());
        }
      }
    }

    if (!foundGeometry) {
      LOG.warn("Geometry field not found");
    }
    if (!foundProperties) {
      LOG.warn("Property field not found");
    }

    return geoJson;
  }

  private Map<String, Object> formatGeometryField(Map<String, Object> map) {
    Map<String, Object> geometryFields = new HashMap<String, Object>();

    String type = (String) map.get("type");

    if (type.equalsIgnoreCase("POINT")) {
      List<Double> coordinates = (List<Double>) map.get(GeoJsonConstants.COORDINATES);

      try {
        geometryFields.put(GeoJsonConstants.LONGITUDE, coordinates.get(0));
        geometryFields.put(GeoJsonConstants.LATITUDE, coordinates.get(1));
        if (coordinates.size() == 3) {
          geometryFields.put(GeoJsonConstants.ALTITUDE, coordinates.get(2));
        }
      } catch (IndexOutOfBoundsException e) {
        LOG.error(e.getMessage());
      }

    } else if (type.equalsIgnoreCase("LINESTRING")) {
      geometryFields.put(
          GeoJsonConstants.COORDINATES_LINE_STRING,
          map.get(GeoJsonConstants.COORDINATES).toString());

    } else if (type.equalsIgnoreCase("POLYGON")) {
      geometryFields.put(
          GeoJsonConstants.COORDINATES_POLYGON,
          map.get(GeoJsonConstants.COORDINATES).toString());

    } else if (type.equalsIgnoreCase("MULTIPOINT")) {
      geometryFields.put(
          GeoJsonConstants.COORDINATES_MULTI_POINT,
          map.get(GeoJsonConstants.COORDINATES).toString());

    } else if (type.equalsIgnoreCase("MULTILINESTRING")) {
      geometryFields.put(
          GeoJsonConstants.COORDINATES_MULTI_STRING,
          map.get(GeoJsonConstants.COORDINATES).toString());

    } else if (type.equalsIgnoreCase("MULTIPOLYGON")) {
      geometryFields.put(
          GeoJsonConstants.COORDINATES_MULTI_POLYGON,
          map.get(GeoJsonConstants.COORDINATES).toString());

    } else {
      LOG.error(type + "is not a suppported field type");
    }

    return geometryFields;
  }

}
