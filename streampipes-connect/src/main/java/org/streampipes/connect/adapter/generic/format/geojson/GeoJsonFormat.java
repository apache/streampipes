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

package org.streampipes.connect.adapter.generic.format.geojson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.connect.adapter.generic.format.Format;
import org.streampipes.connect.exception.ParseException;
import org.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.streampipes.model.connect.grounding.FormatDescription;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeoJsonFormat extends Format {

    public static final String ID = "https://streampipes.org/vocabulary/v1/format/geojson";
    private static final Logger logger = LoggerFactory.getLogger(GeoJsonFormat.class);

    @Override
    public FormatDescription declareModel() {
        FormatDescription description = new FormatDescription(ID, "GeoJSON", "This is the description " +
                "for the geoJSON format");

        description.setAppId(ID);
        return description;
    }

    @Override
    public Format getInstance(FormatDescription formatDescription) {
       return new GeoJsonFormat();
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public Map<String, Object> parse(byte[] object) throws ParseException {
        JsonDataFormatDefinition jsonDefinition = new JsonDataFormatDefinition();
        Map<String, Object> result = null;

        try {
            result = jsonDefinition.toMap(object);
        } catch (SpRuntimeException e) {
            throw new ParseException("Could not parse Data: " + e.toString());
        }

        return  geoJsonFormatter(result);
    }

    private Map<String, Object> geoJsonFormatter(Map<String, Object> map) {
        Map<String, Object> geoJson = new HashMap<String, Object>();
        Boolean foundGeometry = false;
        Boolean foundProperties = false;

        for (Map.Entry<String, Object> entry : map.entrySet())
        {
            if (entry.getKey().equalsIgnoreCase("GEOMETRY")) {
                foundGeometry = true;
                geoJson.putAll(formatGeometryField( (Map<String, Object>) entry.getValue()));
            }
            if(entry.getKey().equalsIgnoreCase("PROPERTIES")) {
                foundProperties = true;
                for (Map.Entry<String, Object> innerEntry : ((Map<String, Object>) entry.getValue()).entrySet()) {
                    geoJson.put(innerEntry.getKey(), innerEntry.getValue());
                }
            }
        }

        if (!foundGeometry) logger.warn("Geometry field not found");
        if (!foundProperties) logger.warn("Property field not found");

        return geoJson;
    }

    private Map<String, Object> formatGeometryField(Map<String, Object> map) {
        Map<String, Object> geometryFields = new HashMap<String, Object>();

        String type = (String) map.get("type");

        if(type.equalsIgnoreCase("POINT")) {
            List<Double> coordinates = (List<Double>) map.get("coordinates");

            try {
                geometryFields.put("longitude", coordinates.get(0));
                geometryFields.put("latitude", coordinates.get(1));
                if (coordinates.size() == 3) {
                    geometryFields.put("altitude", coordinates.get(2));
                }
            } catch (IndexOutOfBoundsException e) {
                logger.error(e.getMessage());
            }

        } else if (type.equalsIgnoreCase("LINESTRING")) {
            geometryFields.put("coordinatesLineString", map.get("coordinates").toString());

        } else if (type.equalsIgnoreCase("POLYGON")) {
            geometryFields.put("coordinatesPolygon", map.get("coordinates").toString());

        } else if (type.equalsIgnoreCase("MULTIPOINT")) {
            geometryFields.put("coordinatesMultiPoint", map.get("coordinates").toString());

        } else if (type.equalsIgnoreCase("MULTILINESTRING")) {
            geometryFields.put("coordinatesMultiString", map.get("coordinates").toString());

        } else if (type.equalsIgnoreCase("MULTIPOLYGON")) {
            geometryFields.put("coordinatesMultiPolygon", map.get("coordinates").toString());

        } else {
            logger.error(type + "is not a suppported field type");
        }

        return geometryFields;
    }

}
