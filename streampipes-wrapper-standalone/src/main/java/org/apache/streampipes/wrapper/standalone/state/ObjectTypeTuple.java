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

import org.apache.streampipes.wrapper.state.serializer.StateSerializer;
import java.lang.reflect.Type;

public class ObjectTypeTuple {

    private String genericTypeSignature;
    private String objectSerialization;

    //Default constructor needed for Jackson deserialization
    public ObjectTypeTuple(){}

    public ObjectTypeTuple(Object obj, StateSerializer serializer, Type type){
        this.genericTypeSignature = serializer.getTypeSignature(type);
        this.objectSerialization = serializer.serialize(obj);
    }

    public String getGenericTypeSignature(){
        return this.genericTypeSignature;
    }

    public String getObjectSerialization(){
        return this.objectSerialization;
    }



}
