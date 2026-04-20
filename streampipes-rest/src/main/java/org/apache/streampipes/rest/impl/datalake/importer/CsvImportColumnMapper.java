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

package org.apache.streampipes.rest.impl.datalake.importer;

import org.apache.streampipes.model.datalake.importer.CsvImportColumn;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class CsvImportColumnMapper {

    public static List<CsvImportColumn> fromEventSchema(EventSchema schema) {
        return schema.getEventProperties()
                .stream()
                .map(CsvImportColumnMapper::mapProperty)
                .collect(Collectors.toList());
    }

    private static CsvImportColumn mapProperty(EventProperty prop) {
        CsvImportColumn col = new CsvImportColumn();

        col.setCsvColumn(prop.getRuntimeName());
        col.setRuntimeName(prop.getRuntimeName());
        col.setLabel(emptyToNull(prop.getLabel()));
        col.setDescription(emptyToNull(prop.getDescription()));
        col.setSemanticType(prop.getSemanticType());
        col.setPropertyScope(prop.getPropertyScope());
        String runtimeType = null;
        // prop.getAdditionalMetadata().get("runtimeType")
        try {
            Field field = prop.getClass().getDeclaredField("runtimeType");
            field.setAccessible(true);
            runtimeType = (String) field.get(prop);

            // use runtimeType here

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to access runtimeType via reflection", e);
        }

        String normalizedType = normalizeRuntimeType((String) runtimeType);// prop.getRuntimeType());

        col.setRuntimeType(normalizedType);
        col.setInferredType(normalizedType);

        col.setTimestampCandidate(isTimestamp(prop));

        // col.setEventProperties(Collections.singletonList(prop));

        return col;
    }

    private static String normalizeRuntimeType(String runtimeType) {
        if (runtimeType == null) {
            return "STRING";
        }
        String type = runtimeType.contains("#")
                ? runtimeType.substring(runtimeType.indexOf('#') + 1)
                : runtimeType;

        switch (type.toLowerCase(Locale.ROOT)) {
            case "long":
                return "LONG";
            case "int":
            case "integer":
                return "INTEGER";
            case "float":
                return "FLOAT";
            case "double":
                return "DOUBLE";
            case "boolean":
                return "BOOLEAN";
            case "string":
                return "STRING";
            case "datetime":
                return "LONG";
            default:
                return type.toUpperCase(Locale.ROOT);
        }
    }

    private static boolean isTimestamp(EventProperty prop) {
        if (prop.getSemanticType() != null
                && prop.getSemanticType().equals("http://schema.org/DateTime")) {
            return true;
        }

        String name = prop.getRuntimeName();
        return name != null && name.toLowerCase(Locale.ROOT).contains("time");
    }

    private static String emptyToNull(String value) {
        return (value == null || value.trim().isEmpty()) ? null : value;
    }
}