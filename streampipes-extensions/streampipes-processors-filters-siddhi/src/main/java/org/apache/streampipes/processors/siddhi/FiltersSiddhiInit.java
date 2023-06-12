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

package org.apache.streampipes.processors.siddhi;

import org.apache.streampipes.dataformat.cbor.CborDataFormatFactory;
import org.apache.streampipes.dataformat.fst.FstDataFormatFactory;
import org.apache.streampipes.dataformat.json.JsonDataFormatFactory;
import org.apache.streampipes.dataformat.smile.SmileDataFormatFactory;
import org.apache.streampipes.extensions.management.model.SpServiceDefinition;
import org.apache.streampipes.extensions.management.model.SpServiceDefinitionBuilder;
import org.apache.streampipes.messaging.jms.SpJmsProtocolFactory;
import org.apache.streampipes.messaging.kafka.SpKafkaProtocolFactory;
import org.apache.streampipes.messaging.mqtt.SpMqttProtocolFactory;
import org.apache.streampipes.messaging.nats.SpNatsProtocolFactory;
import org.apache.streampipes.processors.siddhi.count.CountAggregation;
import org.apache.streampipes.processors.siddhi.filter.NumericalFilterSiddhiProcessor;
import org.apache.streampipes.processors.siddhi.listcollector.ListCollector;
import org.apache.streampipes.processors.siddhi.listfilter.ListFilter;
import org.apache.streampipes.processors.siddhi.topk.TopK;
import org.apache.streampipes.processors.siddhi.trend.TrendProcessor;
import org.apache.streampipes.service.extensions.ExtensionsModelSubmitter;

public class FiltersSiddhiInit extends ExtensionsModelSubmitter {

  public static void main(String[] args) {
    new FiltersSiddhiInit().init();
  }

  @Override
  public SpServiceDefinition provideServiceDefinition() {
    return SpServiceDefinitionBuilder.create("org.apache.streampipes.processors.filters.siddhi",
            "Processors Filters Siddhi",
            "",
            8090)
        .registerPipelineElements(
            new TrendProcessor(),
            new NumericalFilterSiddhiProcessor(),
            new ListFilter(),
            new ListCollector(),
            new CountAggregation(),
            new TopK())
        // Currently not working: StreamStopSiddhiProcessor, FrequencyChangeSiddhiProcessor, FrequencySiddhiProcessor
        .registerMessagingFormats(
            new JsonDataFormatFactory(),
            new CborDataFormatFactory(),
            new SmileDataFormatFactory(),
            new FstDataFormatFactory())
        .registerMessagingProtocols(
            new SpKafkaProtocolFactory(),
            new SpJmsProtocolFactory(),
            new SpMqttProtocolFactory(),
            new SpNatsProtocolFactory())
        .build();
  }
}
