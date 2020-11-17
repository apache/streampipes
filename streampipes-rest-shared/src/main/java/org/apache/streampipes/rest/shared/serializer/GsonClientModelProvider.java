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

package org.apache.streampipes.rest.shared.serializer;

import com.google.gson.Gson;
import org.apache.streampipes.rest.shared.annotation.GsonClientModel;
import org.apache.streampipes.serializers.json.Utils;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.util.Arrays;

@Provider
@GsonClientModel
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GsonClientModelProvider extends GsonJerseyProvider {

    @Override
    protected Gson getGsonSerializer() {
        return Utils.getGson();
    }

    @Override
    protected boolean requiredAnnotationsPresent(Annotation[] annotations) {
        return Arrays.stream(annotations).anyMatch(a -> a.annotationType().equals(GsonClientModel.class));
    }
}
