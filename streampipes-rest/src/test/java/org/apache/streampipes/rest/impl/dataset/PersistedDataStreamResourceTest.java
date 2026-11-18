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

package org.apache.streampipes.rest.impl.dataset;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class PersistedDataStreamResourceTest {

  @Test
  void onlyCollectionLookupIsMapped() throws Exception {
    try (var context = new StaticWebApplicationContext()) {
      context.getBeanFactory().registerSingleton("resource", new PersistedDataStreamResource(null));
      context.refresh();
      var mappings = new RequestMappingHandlerMapping();
      mappings.setApplicationContext(context);
      mappings.afterPropertiesSet();

      assertNotNull(mappings.getHandler(new MockHttpServletRequest("GET", "/api/v3/datalake/pipelines")));
      assertNull(mappings.getHandler(new MockHttpServletRequest("GET", "/api/v3/datalake/pipelines/pipeline/measure")));
    }
  }
}
