package org.streampipes.rest.impl.datalake;

import org.junit.Before;
import org.junit.Test;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyList;
import org.streampipes.model.schema.EventPropertyNested;
import org.streampipes.model.schema.EventPropertyPrimitive;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/*
Copyright 2019 FZI Forschungszentrum Informatik

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
public class DataLakeNoUserManagementV3Test {

    Method privateMethod;
    DataLakeNoUserManagementV3 management;

    @Before
    public void setPrivateMethod() throws NoSuchMethodException {
        this.management = new DataLakeNoUserManagementV3();
        this.privateMethod = DataLakeNoUserManagementV3.class.getDeclaredMethod("compareEventProperties",
                List.class , List.class);
        privateMethod.setAccessible(true);
    }

    @Test
    public void testCompareEventProperties() throws InvocationTargetException, IllegalAccessException {
        EventProperty property = createEventPropertyPrimitive("A", "type");

        boolean result = (boolean) privateMethod.invoke(management,
                Collections.singletonList(property),
                Collections.singletonList(property));
        assertEquals(true, result);
    }

    @Test
    public void testCompareEventProperties2() throws InvocationTargetException, IllegalAccessException {
        EventProperty property = createEventPropertyPrimitive("A", "type");

        EventProperty property2 = createEventPropertyPrimitive("A", "typeB");

        boolean result = (boolean) privateMethod.invoke(management,
                Collections.singletonList(property),
                Collections.singletonList(property2));
        assertEquals(false, result);
    }


    @Test
    public void testCompareEventProperties3() throws InvocationTargetException, IllegalAccessException {
        EventProperty property = createEventPropertyPrimitive("A", "type");

        EventPropertyPrimitive property2 = createEventPropertyPrimitive("C", "type");


        boolean result = (boolean) privateMethod.invoke(management,
                Collections.singletonList(property),
                Collections.singletonList(property2));
        assertEquals(false, result);
    }

    @Test
    public void testCompareEventProperties4() throws InvocationTargetException, IllegalAccessException {
        EventProperty property = createEventPropertyPrimitive("A", "type");

        EventProperty property2 = createEventPropertyPrimitive("A", "type");
        EventProperty property3 = createEventPropertyPrimitive("F", "type");


        boolean result = (boolean) privateMethod.invoke(management,
                Collections.singletonList(property),
                Arrays.asList(property2, property3));
        assertEquals(false, result);
    }

    @Test
    public void testCompareEventProperties5() throws InvocationTargetException, IllegalAccessException {
        EventProperty property = createEventPropertyPrimitive("A", "type");
        EventProperty property2 = createEventPropertyPrimitive("F", "type");

        EventProperty property3 = createEventPropertyPrimitive("A", "type");
        EventProperty property4 = createEventPropertyPrimitive("F", "type");


        boolean result = (boolean) privateMethod.invoke(management,
                Arrays.asList(property, property2),
                Arrays.asList(property3, property4));
        assertEquals(true, result);
    }

    @Test
    public void testCompareEventProperties6() throws InvocationTargetException, IllegalAccessException {
        EventProperty property = new EventPropertyList();
        property.setRuntimeName("A");
        ((EventPropertyList) property).setEventProperty(createEventPropertyPrimitive("K", "B"));



        boolean result = (boolean) privateMethod.invoke(management,
                Arrays.asList(property),
                Arrays.asList(property));
        assertEquals(true, result);
    }

    @Test
    public void testCompareEventProperties7() throws InvocationTargetException, IllegalAccessException {
        EventProperty property = new EventPropertyList();
        property.setRuntimeName("A");
        ((EventPropertyList) property).setEventProperty(createEventPropertyPrimitive("K", "B"));

        EventProperty property2 = new EventPropertyList();
        property2.setRuntimeName("A");
        ((EventPropertyList) property2).setEventProperty(createEventPropertyPrimitive("Z", "B"));


        boolean result = (boolean) privateMethod.invoke(management,
                Arrays.asList(property),
                Arrays.asList(property2));
        assertEquals(false, result);
    }

    @Test
    public void testCompareEventProperties8() throws InvocationTargetException, IllegalAccessException {
        EventProperty property = new EventPropertyNested();
        property.setRuntimeName("A");
       ((EventPropertyNested) property).setEventProperties(Arrays.asList(createEventPropertyPrimitive("A", "Type")));


        boolean result = (boolean) privateMethod.invoke(management,
                Arrays.asList(property),
                Arrays.asList(property));
        assertEquals(true, result);
    }

    @Test
    public void testCompareEventProperties9() throws InvocationTargetException, IllegalAccessException {
        EventProperty property = new EventPropertyNested();
        property.setRuntimeName("A");
        ((EventPropertyNested) property).setEventProperties(Arrays.asList(createEventPropertyPrimitive("A", "Type")));


        EventProperty property2 = new EventPropertyNested();
        property2.setRuntimeName("A");
        ((EventPropertyNested) property2).setEventProperties(Arrays.asList(createEventPropertyPrimitive("A", "P")));

        boolean result = (boolean) privateMethod.invoke(management,
                Arrays.asList(property),
                Arrays.asList(property2));
        assertEquals(false, result);
    }

    @Test
    public void testCompareEventProperties10() throws InvocationTargetException, IllegalAccessException {
        EventProperty property = new EventPropertyNested();
        property.setRuntimeName("A");
        ((EventPropertyNested) property).setEventProperties(Arrays.asList(createEventPropertyPrimitive("A", "Type")));
        EventProperty property2 = createEventPropertyPrimitive("A", "Type");;

        boolean result = (boolean) privateMethod.invoke(management,
                Arrays.asList(property, property2),
                Arrays.asList(property, property2));
        assertEquals(true, result);
    }

    @Test
    public void testCompareEventProperties11() throws InvocationTargetException, IllegalAccessException {
        EventProperty property = new EventPropertyNested();
        property.setRuntimeName("A");
        ((EventPropertyNested) property).setEventProperties(Arrays.asList(createEventPropertyPrimitive("A", "Type")));
        EventProperty property2 = createEventPropertyPrimitive("A", "Type");;

        boolean result = (boolean) privateMethod.invoke(management,
                Arrays.asList(property, property2),
                Arrays.asList(property));
        assertEquals(false, result);
    }


    private EventPropertyPrimitive createEventPropertyPrimitive(String rutimeName, String RuntimeType) {
        EventPropertyPrimitive eventPropertyPrimitive = new EventPropertyPrimitive();
        eventPropertyPrimitive.setRuntimeName(rutimeName);
        eventPropertyPrimitive.setRuntimeType(RuntimeType);
        return eventPropertyPrimitive;
    }

}