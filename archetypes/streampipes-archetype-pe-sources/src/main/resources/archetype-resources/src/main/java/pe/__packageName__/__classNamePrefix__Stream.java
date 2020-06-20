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

#set($symbol_pound='#')
#set($symbol_dollar='$')
#set($symbol_escape='\' )

package ${package}.pe.${packageName};

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.graph.DataSourceDescription;
import org.apache.streampipes.sdk.builder.DataStreamBuilder;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.Formats;
import org.apache.streampipes.sdk.helpers.Protocols;
import org.apache.streampipes.sources.AbstractAdapterIncludedStream;


public class ${classNamePrefix}Stream extends AbstractAdapterIncludedStream {

  @Override
  public SpDataStream declareModel(DataSourceDescription sep) {
    return DataStreamBuilder.create("${package}-${packageName}", "${classNamePrefix}", "")
            .property(EpProperties.timestampProperty("timestamp"))

            // configure your stream here

            .format(Formats.jsonFormat())
            .protocol(Protocols.kafka("localhost", 9092, "TOPIC_SHOULD_BE_CHANGED"))
            .build();
  }

  @Override
  public void executeStream() {

  }
}
