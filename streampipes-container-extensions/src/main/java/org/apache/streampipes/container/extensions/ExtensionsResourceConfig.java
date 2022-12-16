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
package org.apache.streampipes.container.extensions;

import org.apache.streampipes.connect.container.worker.init.AdapterServiceResourceProvider;
import org.apache.streampipes.container.init.BaseExtensionsServiceResourceProvider;
import org.apache.streampipes.container.init.PipelineElementServiceResourceProvider;
import org.apache.streampipes.service.base.rest.BaseResourceConfig;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ExtensionsResourceConfig extends BaseResourceConfig {

  @Override
  public List<List<Class<?>>> getClassesToRegister() {
    return Arrays.asList(
        new BaseExtensionsServiceResourceProvider().getResourceClasses(),
        new AdapterServiceResourceProvider().getResourceClasses(),
        new PipelineElementServiceResourceProvider().getResourceClasses());
  }
}
