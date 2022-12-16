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

package org.apache.streampipes.processors.enricher.jvm;

import org.apache.streampipes.container.model.SpServiceDefinition;
import org.apache.streampipes.container.model.SpServiceDefinitionBuilder;
import org.apache.streampipes.container.standalone.init.StandaloneModelSubmitter;
import org.apache.streampipes.dataformat.cbor.CborDataFormatFactory;
import org.apache.streampipes.dataformat.fst.FstDataFormatFactory;
import org.apache.streampipes.dataformat.json.JsonDataFormatFactory;
import org.apache.streampipes.dataformat.smile.SmileDataFormatFactory;
import org.apache.streampipes.messaging.jms.SpJmsProtocolFactory;
import org.apache.streampipes.messaging.kafka.SpKafkaProtocolFactory;
import org.apache.streampipes.messaging.mqtt.SpMqttProtocolFactory;
import org.apache.streampipes.processors.enricher.jvm.processor.jseval.JSEvalController;
import org.apache.streampipes.processors.enricher.jvm.processor.math.MathOpProcessor;
import org.apache.streampipes.processors.enricher.jvm.processor.math.staticmathop.StaticMathOpProcessor;
import org.apache.streampipes.processors.enricher.jvm.processor.sizemeasure.SizeMeasureController;
import org.apache.streampipes.processors.enricher.jvm.processor.trigonometry.TrigonometryProcessor;
import org.apache.streampipes.processors.enricher.jvm.processor.valuechange.ValueChangeProcessor;

public class EnricherJvmInit extends StandaloneModelSubmitter {

  public static void main(String[] args) {
    new EnricherJvmInit().init();
  }

  @Override
  public SpServiceDefinition provideServiceDefinition() {
    return SpServiceDefinitionBuilder.create("org.apache.streampipes.processors.enricher.jvm",
            "Processors Enricher JVM",
            "",
            8090)
        .registerPipelineElements(new SizeMeasureController(),
            new JSEvalController(),
            new MathOpProcessor(),
            new StaticMathOpProcessor(),
            new TrigonometryProcessor(),
            new ValueChangeProcessor())
        .registerMessagingFormats(
            new JsonDataFormatFactory(),
            new CborDataFormatFactory(),
            new SmileDataFormatFactory(),
            new FstDataFormatFactory())
        .registerMessagingProtocols(
            new SpKafkaProtocolFactory(),
            new SpJmsProtocolFactory(),
            new SpMqttProtocolFactory())
        .build();
  }
}
