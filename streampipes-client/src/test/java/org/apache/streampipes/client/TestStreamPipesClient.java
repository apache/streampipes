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
package org.apache.streampipes.client;

import org.apache.commons.collections.MapUtils;
import org.apache.streampipes.dataformat.json.JsonDataFormatFactory;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.template.PipelineElementTemplate;
import org.apache.streampipes.model.template.PipelineElementTemplateConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestStreamPipesClient {

  public static void main(String[] args) {
    StreamPipesCredentials credentials = StreamPipesCredentials
            .from(System.getenv("user"), System.getenv("apiKey"));

    StreamPipesClient client = StreamPipesClient
            .create("localhost", 8082, credentials, true);

    List<Pipeline> pipelines = client.pipelines().all();

    pipelines.forEach(pipeline -> System.out.println(pipeline.getName()));

    PipelineElementTemplate template = new PipelineElementTemplate();
    template.setTemplateName("Test");
    template.setTemplateDescription("description");
    template.setBasePipelineElementAppId("org.apache.streampipes.sinks.internal.jvm.dashboard");
    Map<String, PipelineElementTemplateConfig> configs = new HashMap<>();
    PipelineElementTemplateConfig config = new PipelineElementTemplateConfig();
    config.setValue("test");
    configs.put("visualization-name", config);
    template.setTemplateConfigs(configs);

    //client.pipelineElementTemplates().create(template);

    List<PipelineElementTemplate> templates = client.pipelineElementTemplates().all();

    templates.forEach(t -> System.out.println(t.getTemplateName()));

    List<DataSinkInvocation> dataSinks = client.sinks().all();

//    System.out.println(dataSinks.size());
//    System.out.println(template.getCouchDbId());
//    DataSinkInvocation invocation = client.sinks().getDataSinkForPipelineElement(templates.get(0).getCouchDbId(), dataSinks.get(0));
//    System.out.println(invocation.getName());

    List<SpDataStream> dataStreams = client.streams().all();
    System.out.println(dataStreams.size());

    //SpDataStream myStream = dataStreams.stream().filter(stream -> stream.getCorrespondingAdapterId().equals("org.apache.streampipes.connect.adapters.simulator.randomdatastream")).findFirst().get();

    client.registerDataFormat(new JsonDataFormatFactory());

    client.streams().subscribe(dataStreams.get(1), event -> MapUtils.debugPrint(System.out, "event", event.getRaw()));
  }
}
