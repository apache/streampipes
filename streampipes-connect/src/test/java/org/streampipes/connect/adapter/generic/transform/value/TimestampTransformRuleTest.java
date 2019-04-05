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

package org.streampipes.connect.adapter.generic.transform.value;

import org.junit.Test;
import org.streampipes.model.schema.*;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class TimestampTransformRuleTest {

//    @Test
//    public void transformListFormatString() {
//        EventSchema eventSchema = new EventSchema();
//        EventPropertyList eventPropertyList = new EventPropertyList();
//        eventPropertyList.setRuntimeName("list");
//        EventProperty eventPropertyValue = new EventPropertyPrimitive();
//        eventPropertyValue.setLabel("value");
//        eventPropertyValue.setRuntimeName("value");
//        eventPropertyList.setEventProperty(eventPropertyValue);
//        eventSchema.setEventProperties(Collections.singletonList(eventPropertyList));
//
//        Map<String, Object> event = new HashMap<>();
//        Map<String, Object> subEvent = new HashMap<>();
//        subEvent.put("value", "2019-03-11T20:50:38.138Z");
//        event.put("list",subEvent);
//
//        List<String> keys = new ArrayList<>();
//        keys.add("list");
//        keys.add("value");
//
//        TimestampTranformationRule timestampTranformationRule = new TimestampTranformationRule(keys,
//                TimestampTranformationRuleMode.FORMAT_STRING, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", 1000);
//
//        Map result = timestampTranformationRule.transform(event);
//
//        assertEquals(1, result.keySet().size());
//        assertEquals( 1552333838138L, ((Map) result.get(eventPropertyList.getRuntimeName())).get(eventPropertyValue.getRuntimeName()));
//    }

//    @Test
//    public void transformListTimeUnit() {
//        EventSchema eventSchema = new EventSchema();
//        EventPropertyList eventPropertyList = new EventPropertyList();
//        eventPropertyList.setRuntimeName("list");
//        EventProperty eventPropertyValue = new EventPropertyPrimitive();
//        eventPropertyValue.setLabel("value");
//        eventPropertyValue.setRuntimeName("value");
//        eventPropertyList.setEventProperty(eventPropertyValue);
//        eventSchema.setEventProperties(Collections.singletonList(eventPropertyList));
//
//        Map<String, Object> event = new HashMap<>();
//        Map<String, Object> subEvent = new HashMap<>();
//        subEvent.put("value", 1552380411);
//        event.put("list",subEvent);
//
//        List<String> keys = new ArrayList<>();
//        keys.add("list");
//        keys.add("value");
//
//        TimestampTranformationRule timestampTranformationRule = new TimestampTranformationRule(keys,
//                TimestampTranformationRuleMode.TIME_UNIT, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", 1000);
//
//        Map result = timestampTranformationRule.transform(event);
//
//        assertEquals(1, result.keySet().size());
//        assertEquals( 1552380411000L, ((Map) result.get(eventPropertyList.getRuntimeName())).get(eventPropertyValue.getRuntimeName()));
//
//    }


    @Test
    public void transformNestedFormatString() {
        EventSchema eventSchema = new EventSchema();
        EventPropertyNested eventPropertyMainKey = new EventPropertyNested();
        eventPropertyMainKey.setLabel("mainKey");
        eventPropertyMainKey.setRuntimeName("mainKey");
        EventProperty eventPropertyValue = new EventPropertyPrimitive();
        eventPropertyValue.setLabel("value");
        eventPropertyValue.setRuntimeName("value");
        eventPropertyMainKey.setEventProperties(Collections.singletonList(eventPropertyValue));
        eventSchema.setEventProperties(Collections.singletonList(eventPropertyMainKey));

        Map<String, Object> event = new HashMap<>();
        Map<String, Object> subEvent = new HashMap<>();
        subEvent.put("value", "2009-12-31");
        event.put("mainKey",subEvent);

        List<String> keys = new ArrayList<>();
        keys.add("mainKey");
        keys.add("value");


        TimestampTranformationRule timestampTranformationRule = new TimestampTranformationRule(keys,
                TimestampTranformationRuleMode.FORMAT_STRING, "yyyy-MM-dd", 1000);

        Map result = timestampTranformationRule.transform(event);

        assertEquals(1, result.keySet().size());
        assertEquals(1262214000000L, ((Map) result.get(eventPropertyMainKey.getRuntimeName())).get(eventPropertyValue.getRuntimeName()));

    }

    @Test
    public void transformNestedTimeUnit() {
        EventSchema eventSchema = new EventSchema();
        EventPropertyNested eventPropertyMainKey = new EventPropertyNested();
        eventPropertyMainKey.setLabel("mainKey");
        eventPropertyMainKey.setRuntimeName("mainKey");
        EventProperty eventPropertyValue = new EventPropertyPrimitive();
        eventPropertyValue.setLabel("value");
        eventPropertyValue.setRuntimeName("value");
        eventPropertyMainKey.setEventProperties(Collections.singletonList(eventPropertyValue));
        eventSchema.setEventProperties(Collections.singletonList(eventPropertyMainKey));

        Map<String, Object> event = new HashMap<>();
        Map<String, Object> subEvent = new HashMap<>();
        subEvent.put("value", 1262214000);
        event.put("mainKey",subEvent);

        List<String> keys = new ArrayList<>();
        keys.add("mainKey");
        keys.add("value");


        TimestampTranformationRule timestampTranformationRule = new TimestampTranformationRule(keys,
                TimestampTranformationRuleMode.TIME_UNIT, "yyyy-MM-dd", 1000);

        Map result = timestampTranformationRule.transform(event);

        assertEquals(1, result.keySet().size());
        assertEquals(1262214000000L, ((Map) result.get(eventPropertyMainKey.getRuntimeName())).get(eventPropertyValue.getRuntimeName()));

    }


    @Test
    public void transformMultiEventFormatString() {
        EventSchema eventSchema = new EventSchema();
        EventProperty eventPropertyValue1 = new EventPropertyPrimitive();
        eventPropertyValue1.setLabel("value1");
        eventPropertyValue1.setRuntimeName("value1");
        EventProperty eventPropertyValue2 = new EventPropertyPrimitive();
        eventPropertyValue2.setLabel("value2");
        eventPropertyValue2.setRuntimeName("value2");
        eventSchema.addEventProperty(eventPropertyValue1);
        eventSchema.addEventProperty(eventPropertyValue2);

        List<String> keys = new ArrayList<>();
        keys.add("value2");


        TimestampTranformationRule timestampTranformationRule = new TimestampTranformationRule(keys,
                TimestampTranformationRuleMode.FORMAT_STRING, "yyyy-MM-dd HH:mm:ss", 1000);

        Map<String, Object> event = new HashMap<>();
        event.put("value1", 0.0);
        event.put("value2", "2019-03-15 10:00:00");

        Map result = timestampTranformationRule.transform(event);
        assertEquals(2, result.keySet().size());
        assertEquals(1552640400000L, result.get(eventPropertyValue2.getLabel()));


        event = new HashMap<>();
        event.put("value1", 20.0);
        event.put("value2", "2019-03-15 15:23:00");

        result = timestampTranformationRule.transform(event);
        assertEquals(2, result.keySet().size());
        assertEquals(1552659780000L, result.get(eventPropertyValue2.getRuntimeName()));


        event = new HashMap<>();
        event.put("value1", 0.0);
        event.put("value2", "2027-012-15 21:53:50");

        result = timestampTranformationRule.transform(event);
        assertEquals(2, result.keySet().size());
        assertEquals(1828904030000L, result.get(eventPropertyValue2.getRuntimeName()));
    }

    @Test
    public void transformMultiEventTimeUnit() {
        EventSchema eventSchema = new EventSchema();
        EventProperty eventPropertyValue1 = new EventPropertyPrimitive();
        eventPropertyValue1.setLabel("value1");
        eventPropertyValue1.setRuntimeName("value1");
        EventProperty eventPropertyValue2 = new EventPropertyPrimitive();
        eventPropertyValue2.setLabel("value2");
        eventPropertyValue2.setRuntimeName("value2");
        eventSchema.addEventProperty(eventPropertyValue1);
        eventSchema.addEventProperty(eventPropertyValue2);

        List<String> keys = new ArrayList<>();
        keys.add("value2");


        TimestampTranformationRule timestampTranformationRule = new TimestampTranformationRule(keys,
                TimestampTranformationRuleMode.TIME_UNIT, "yyyy-MM-dd HH:mm:ss", 1000);

        Map<String, Object> event = new HashMap<>();
        event.put("value1", 0.0);
        event.put("value2", 1552640400);

        Map result = timestampTranformationRule.transform(event);
        assertEquals(2, result.keySet().size());
        assertEquals(1552640400000L, result.get(eventPropertyValue2.getLabel()));


        event = new HashMap<>();
        event.put("value1", 20.0);
        event.put("value2", 1552659780);

        result = timestampTranformationRule.transform(event);
        assertEquals(2, result.keySet().size());
        assertEquals(1552659780000L, result.get(eventPropertyValue2.getRuntimeName()));


        event = new HashMap<>();
        event.put("value1", 0.0);
        event.put("value2", 1828904030);

        result = timestampTranformationRule.transform(event);
        assertEquals(2, result.keySet().size());
        assertEquals(1828904030000L, result.get(eventPropertyValue2.getRuntimeName()));
    }

}
