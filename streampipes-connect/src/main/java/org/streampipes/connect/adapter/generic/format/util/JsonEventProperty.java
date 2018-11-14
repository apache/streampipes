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

package org.streampipes.connect.adapter.generic.format.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyList;
import org.streampipes.model.schema.EventPropertyNested;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.vocabulary.SO;
import org.streampipes.vocabulary.XSD;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonEventProperty {

    static Logger logger = LoggerFactory.getLogger(JsonEventProperty.class);


    public static EventProperty getEventProperty(String key, Object o) {
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
            ((EventPropertyPrimitive) resultProperty).setRuntimeType(XSD._float.toString());
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
