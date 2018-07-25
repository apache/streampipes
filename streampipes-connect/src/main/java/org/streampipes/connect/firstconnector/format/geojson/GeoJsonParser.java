/*
Copyright 2018 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.streampipes.connect.firstconnector.format.geojson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import org.geojson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.connect.EmitBinaryEvent;
import org.streampipes.connect.firstconnector.format.Parser;
import org.streampipes.model.modelconnect.FormatDescription;
import org.streampipes.model.schema.*;
import org.streampipes.vocabulary.SO;
import org.streampipes.vocabulary.XSD;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class GeoJsonParser extends Parser {

    Logger logger = LoggerFactory.getLogger(GeoJsonParser.class);
    private static String KEY = "features";


    @Override
    public Parser getInstance(FormatDescription formatDescription) {
        return new GeoJsonParser();
    }

    @Override
    public void parse(InputStream data, EmitBinaryEvent emitBinaryEvent) {
        FeatureCollection geoFeature;
        Gson gson = new Gson();

        try {
            String dataString = CharStreams.toString(new InputStreamReader(data, Charsets.UTF_8));
            List<Map> features = (List) gson.fromJson(dataString, HashMap.class).get("features");

            for(Map feature : features) {
                byte[] bytes = gson.toJson(feature).getBytes();
                emitBinaryEvent.emit(bytes);
            }

        } catch (IOException e) {
            e.printStackTrace();
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
            EventProperty p = getEventProperty(entry.getKey(), entry.getValue());
            resultSchema.addEventProperty(p);
        }

        List<EventProperty> eventProperties = parseGeometryField(geoFeature);
        eventProperties.forEach(eventProperty -> resultSchema.addEventProperty(eventProperty));

        return resultSchema;
    }

    private List<EventProperty> parseGeometryField(Feature geoFeature) {
        List<EventProperty> eventProperties = new LinkedList<>();

        if(geoFeature.getGeometry() instanceof Point) {
            Point point = (Point) geoFeature.getGeometry();
            eventProperties.add(getEventPropertyGeoJson("longitude", point.getCoordinates().getLongitude(), SO.Longitude));
            eventProperties.add(getEventPropertyGeoJson("latitude", point.getCoordinates().getLatitude(), SO.Latitude));
            if (point.getCoordinates().hasAltitude()) {
                eventProperties.add(getEventPropertyGeoJson("altitude", point.getCoordinates().getAltitude(), SO.Altitude));
            }

        } else if (geoFeature.getGeometry() instanceof LineString) {
            LineString lineString = (LineString) geoFeature.getGeometry();
            eventProperties.add(getEventProperty("coorindatesLineString", lineString.getCoordinates()));

        } else if (geoFeature.getGeometry() instanceof Polygon) {
            Polygon polygon = (Polygon) geoFeature.getGeometry();
            eventProperties.add(getEventProperty("coorindatesPolygon", polygon.getCoordinates()));

        } else if (geoFeature.getGeometry() instanceof MultiPoint) {
            MultiPoint multiPoint = (MultiPoint) geoFeature.getGeometry();
            eventProperties.add(getEventProperty("coorindatesMultiPoint", multiPoint.getCoordinates()));

        } else if (geoFeature.getGeometry() instanceof MultiLineString) {
            MultiLineString multiLineString = (MultiLineString) geoFeature.getGeometry();
            eventProperties.add(getEventProperty("coorindatesMultiLineString", multiLineString.getCoordinates()));

        } else if (geoFeature.getGeometry() instanceof MultiPolygon) {
            MultiPolygon multiPolygon = (MultiPolygon) geoFeature.getGeometry();
            eventProperties.add(getEventProperty("coorindatesMultiPolygon", multiPolygon.getCoordinates()));
        } else {
            logger.error("No geometry field found in geofeature: " + geoFeature.toString());
        }

        return eventProperties;

    }

    private EventProperty getEventPropertyGeoJson(String name, Object value, String domain) {
        EventProperty eventProperty = getEventProperty(name, value);
        try {
            ((EventPropertyPrimitive) eventProperty).setDomainProperties(Arrays.asList(new URI(domain)));

        } catch (URISyntaxException e) {
            logger.error(e.getMessage());
        }
        return eventProperty;
    }


    private EventProperty getEventProperty(String key, Object o) {
        EventProperty resultProperty = null;

        System.out.println("Key: " + key);
        System.out.println("Class: " + o.getClass());
        System.out.println("Primitive: " + o.getClass().isPrimitive());
        System.out.println("Array: " + o.getClass().isArray());
        System.out.println("TypeName: " + o.getClass().getTypeName());


        System.out.println("=======================");

        if (o.getClass().equals(Boolean.class)) {
            resultProperty = new EventPropertyPrimitive();
            resultProperty.setRuntimeName(key);
            ((EventPropertyPrimitive) resultProperty).setRuntimeType(XSD._boolean.toString());
        }
        else if (o.getClass().equals(String.class)) {
            resultProperty = new EventPropertyPrimitive();
            resultProperty.setRuntimeName(key);
            ((EventPropertyPrimitive) resultProperty).setRuntimeType(XSD._string.toString());
        }
        else if (o.getClass().equals(Integer.class) || o.getClass().equals(Double.class)|| o.getClass().equals(Long.class)) {
            resultProperty = new EventPropertyPrimitive();
            resultProperty.setRuntimeName(key);
            ((EventPropertyPrimitive) resultProperty).setRuntimeType(SO.Number);
        }
        else if (o.getClass().equals(LinkedHashMap.class)) {
            resultProperty = new EventPropertyNested();
            resultProperty.setRuntimeName(key);
            List<EventProperty> all = new ArrayList<>();
            for (Map.Entry<String, Object> entry : ((Map<String, Object>) o).entrySet()) {
                all.add(getEventProperty(entry.getKey(), entry.getValue()));
            }

            ((EventPropertyNested) resultProperty).setEventProperties(all);

        } else if (o.getClass().equals(ArrayList.class)) {
            resultProperty = new EventPropertyList();
            resultProperty.setRuntimeName(key);
        }

        if (resultProperty == null) {
            logger.error("Property Type was not detected in JsonParser for the schema detection. This should never happen!");
        }

        return resultProperty;
    }

}
