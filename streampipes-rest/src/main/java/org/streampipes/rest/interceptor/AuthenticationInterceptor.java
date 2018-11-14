/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.streampipes.rest.interceptor;

import org.streampipes.rest.authentication.StreamPipesFilter;
import org.streampipes.rest.shared.annotation.NoAuthenticationRequired;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;

public class AuthenticationInterceptor implements DynamicFeature {


  @Override
  public void configure(ResourceInfo resourceInfo, FeatureContext context) {
    if (!resourceInfo.getResourceMethod().isAnnotationPresent(NoAuthenticationRequired.class)) {
      context.register(StreamPipesFilter.class);
    }
  }
}
