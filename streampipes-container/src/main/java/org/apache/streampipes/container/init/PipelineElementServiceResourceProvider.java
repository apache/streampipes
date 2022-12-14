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
package org.apache.streampipes.container.init;

import org.apache.streampipes.container.api.DataProcessorPipelineElementResource;
import org.apache.streampipes.container.api.DataSinkPipelineElementResource;
import org.apache.streampipes.container.api.DataStreamPipelineElementResource;
import org.apache.streampipes.container.api.PipelineTemplateResource;
import org.apache.streampipes.container.api.WelcomePage;

import java.util.Arrays;
import java.util.List;

public class PipelineElementServiceResourceProvider implements ExtensionsResourceProvider {
  @Override
  public List<Class<?>> getResourceClasses() {
    return Arrays.asList(
        DataSinkPipelineElementResource.class,
        DataProcessorPipelineElementResource.class,
        DataStreamPipelineElementResource.class,
        WelcomePage.class,
        PipelineTemplateResource.class);
  }
}
