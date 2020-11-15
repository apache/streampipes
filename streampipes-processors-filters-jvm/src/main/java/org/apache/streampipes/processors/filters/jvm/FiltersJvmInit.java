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

package org.apache.streampipes.processors.filters.jvm;

import org.apache.streampipes.container.init.DeclarersSingleton;
import org.apache.streampipes.container.standalone.init.StandaloneModelSubmitter;
import org.apache.streampipes.dataformat.cbor.CborDataFormatFactory;
import org.apache.streampipes.dataformat.fst.FstDataFormatFactory;
import org.apache.streampipes.dataformat.json.JsonDataFormatFactory;
import org.apache.streampipes.dataformat.smile.SmileDataFormatFactory;
import org.apache.streampipes.messaging.jms.SpJmsProtocolFactory;
import org.apache.streampipes.messaging.kafka.SpKafkaProtocolFactory;
import org.apache.streampipes.messaging.mqtt.SpMqttProtocolFactory;
import org.apache.streampipes.processors.filters.jvm.config.FiltersJvmConfig;
import org.apache.streampipes.processors.filters.jvm.processor.compose.ComposeController;
import org.apache.streampipes.processors.filters.jvm.processor.enrich.MergeByEnrichController;
import org.apache.streampipes.processors.filters.jvm.processor.limit.RateLimitController;
import org.apache.streampipes.processors.filters.jvm.processor.merge.MergeByTimeController;
import org.apache.streampipes.processors.filters.jvm.processor.numericalfilter.NumericalFilterController;
import org.apache.streampipes.processors.filters.jvm.processor.numericaltextfilter.NumericalTextFilterController;
import org.apache.streampipes.processors.filters.jvm.processor.projection.ProjectionController;
import org.apache.streampipes.processors.filters.jvm.processor.textfilter.TextFilterController;
import org.apache.streampipes.processors.filters.jvm.processor.threshold.ThresholdDetectionController;

public class FiltersJvmInit extends StandaloneModelSubmitter {

  public static void main(String[] args) {
    DeclarersSingleton.getInstance()
            .add(new NumericalFilterController())
            .add(new ThresholdDetectionController())
            .add(new TextFilterController())
            .add(new ProjectionController())
            .add(new MergeByEnrichController())
            .add(new MergeByTimeController())
            .add(new ComposeController())
            .add(new NumericalTextFilterController())
            .add(new RateLimitController());

    DeclarersSingleton.getInstance().registerDataFormats(
            new JsonDataFormatFactory(),
            new CborDataFormatFactory(),
            new SmileDataFormatFactory(),
            new FstDataFormatFactory());

    DeclarersSingleton.getInstance().registerProtocols(
            new SpKafkaProtocolFactory(),
            new SpMqttProtocolFactory(),
            new SpJmsProtocolFactory());

    new FiltersJvmInit().init(FiltersJvmConfig.INSTANCE);
  }
}
