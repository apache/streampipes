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

package ${package}.pe.${packageName};

import org.apache.streampipes.container.declarer.DataStreamDeclarer;
import org.apache.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.apache.streampipes.model.graph.DataSourceDescription;
import org.apache.streampipes.sdk.builder.DataSourceBuilder;

import java.util.Arrays;
import java.util.List;


public class DataSource implements SemanticEventProducerDeclarer {

  public DataSourceDescription declareModel() {
    return DataSourceBuilder.create("${package}.${packageName}.source", "${classNamePrefix} " +
        "Source", "")
            .build();
  }

  public List<DataStreamDeclarer> getEventStreams() {
    return Arrays.asList(new ${classNamePrefix}Stream());
  }
}