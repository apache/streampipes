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
package org.streampipes.manager.pipeline;

import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.serializers.json.GsonSerializer;
import org.streampipes.storage.management.StorageDispatcher;

import java.util.List;
import java.util.stream.Collectors;

public class TestSerializer {

  public static void main(String[] args) {
    List<DataSourceDescription> sep = StorageDispatcher.INSTANCE.getTripleStore().getPipelineElementStorage().getAllDataSources().stream
            ().map(m -> new DataSourceDescription(m)).collect(Collectors.toList());

    String json = GsonSerializer.getGson().toJson(sep.get(0));
    System.out.println(json);

    List<DataProcessorDescription> processors = StorageDispatcher.INSTANCE.getTripleStore().getPipelineElementStorage()
            .getAllDataProcessors().stream().map(m -> new DataProcessorDescription(m)).collect(Collectors.toList());
    String json2 = GsonSerializer.getGson().toJson(processors.get(0));
    System.out.println(json2);

    DataSourceDescription description = GsonSerializer.getGson().fromJson(json, DataSourceDescription.class);
    System.out.println(description.getName());

    DataProcessorDescription processor2 = GsonSerializer.getGson().fromJson(json2, DataProcessorDescription.class);
    System.out.println(processor2.getName());
  }
}
