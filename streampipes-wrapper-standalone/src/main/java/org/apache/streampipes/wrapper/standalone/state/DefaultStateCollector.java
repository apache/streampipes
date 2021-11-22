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
package org.apache.streampipes.wrapper.standalone.state;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.streampipes.wrapper.state.serializer.JacksonStateSerializer;
import org.apache.streampipes.wrapper.state.serializer.StateSerializer;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DefaultStateCollector {

    private ArrayList<Field> fields;
    private HashMap<String, Field> fieldsMap;
    private Object obj;
    private StateSerializer serializer;


    public DefaultStateCollector(Object o){
        this(o, new JacksonStateSerializer());
    }

    public DefaultStateCollector(Object o, StateSerializer serializer){
        this.obj = o;
        this.fields = new ArrayList<>(Arrays.asList(o.getClass().getDeclaredFields()));
        this.serializer = serializer;
        //Only keep marked fields as part of the State
        for(Field f : o.getClass().getDeclaredFields()){
            if(f.getAnnotation(StateObject.class) == null){
                this.fields.remove(f);
            }
        }

        this.fieldsMap = new HashMap<>();
        //Make a map of all fields with their respective Names
        for(Field f: fields){
            f.setAccessible(true);
            this.fieldsMap.put(f.getName(), f);
        }
    }

    public void setState(String state){
        Type t = new TypeReference<HashMap<String, ObjectTypeTuple>>(){}.getType();
        String mapTypeSignature = serializer.getTypeSignature(t);
        HashMap<String, ObjectTypeTuple> map = serializer.deserialize(state, mapTypeSignature);
        for(Map.Entry<String, ObjectTypeTuple> entry : map.entrySet()){
            try {
                this.fieldsMap.get(entry.getKey()).set(this.obj, serializer.deserialize(
                        entry.getValue().getObjectSerialization(), entry.getValue().getGenericTypeSignature()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }

    public String getState()  {
        Map<String, ObjectTypeTuple> list = new HashMap<>();
        for(Field f : this.fields){
            try {
                if(f.get(this.obj) != null){
                    ObjectTypeTuple objectTypeTuple = new ObjectTypeTuple(f.get(this.obj), this.serializer, f.getGenericType());
                    list.put(f.getName(), objectTypeTuple);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return serializer.serialize(list);
    }

}