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

import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;
import java.util.Arrays;

@Provider
public class JacksonSerializationFeature implements DynamicFeature {
  @Override
  public void configure(ResourceInfo resInfo, FeatureContext ctx) {
    if (Arrays
            .stream(resInfo.getResourceMethod().getDeclaredAnnotations())
            .anyMatch(a -> a.annotationType().equals(JacksonSerialized.class))) {
      ctx.register(JacksonFeature.class);
    }
  }
}
