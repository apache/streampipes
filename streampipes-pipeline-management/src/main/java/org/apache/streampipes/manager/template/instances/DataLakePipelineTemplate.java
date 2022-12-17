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

package org.apache.streampipes.manager.template.instances;

import org.apache.streampipes.commons.exceptions.ElementNotFoundException;
import org.apache.streampipes.manager.template.PipelineTemplateGenerator;
import org.apache.streampipes.model.template.PipelineTemplateDescription;
import org.apache.streampipes.sdk.builder.BoundPipelineElementBuilder;
import org.apache.streampipes.sdk.builder.PipelineTemplateBuilder;

public class DataLakePipelineTemplate extends PipelineTemplateGenerator implements PipelineTemplate {

  private static final String ID = "org.apache.streampipes.manager.template.instances.DataLakePipelineTemplate";

  @Override
  public PipelineTemplateDescription declareModel() throws ElementNotFoundException {
    return new PipelineTemplateDescription(
        PipelineTemplateBuilder.create("http://streampipes.org/DataLakePipelineTemplate", "DataLake",
                "")
            .setAppId(ID)
            .boundPipelineElementTemplate(
                BoundPipelineElementBuilder
                    .create(getSink("org.apache.streampipes.sinks.internal.jvm.datalake"))
                    .build())
            .build());

  }
}
