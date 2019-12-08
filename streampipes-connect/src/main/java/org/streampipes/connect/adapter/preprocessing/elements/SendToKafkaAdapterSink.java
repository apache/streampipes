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

package org.streampipes.connect.adapter.preprocessing.elements;

import org.apache.commons.io.IOUtils;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.connect.adapter.model.pipeline.AdapterPipelineElement;
import org.streampipes.connect.adapter.util.TransportFormatSelector;
import org.streampipes.dataformat.SpDataFormatDefinition;
import org.streampipes.messaging.kafka.SpKafkaProducer;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.grounding.KafkaTransportProtocol;
import org.streampipes.model.grounding.TransportFormat;
import org.streampipes.model.grounding.TransportProtocol;

import java.io.IOException;
import java.util.Map;

public class SendToKafkaAdapterSink implements AdapterPipelineElement  {
    private SpKafkaProducer producer;
    private SpDataFormatDefinition dataFormatDefinition;

    // TODO Handle multiple Event Groundings and define what happens when none is provided
    public SendToKafkaAdapterSink(AdapterDescription adapterDescription) {
        producer = new SpKafkaProducer();

        KafkaTransportProtocol kafkaTransportProtocol = (KafkaTransportProtocol) adapterDescription
                .getEventGrounding()
                .getTransportProtocol();

        if ("true".equals(System.getenv("SP_DEBUG"))) {
            kafkaTransportProtocol.setBrokerHostname("localhost");
            kafkaTransportProtocol.setKafkaPort(9094);
        }

        TransportFormat transportFormat =
                adapterDescription.getEventGrounding().getTransportFormats().get(0);

        this.dataFormatDefinition =
                new TransportFormatSelector(transportFormat).getDataFormatDefinition();

        producer.connect(kafkaTransportProtocol);
    }

    @Override
    public Map<String, Object> process(Map<String, Object> event) {
        try {
            if (event != null) {

                // TODO remove, just for performance tests
                if ("true".equals(System.getenv("SP_DEBUG_CONNECT"))) {
                    event.put("internal_t2", System.currentTimeMillis());
                }

                producer.publish(dataFormatDefinition.fromMap(event));
            }
        } catch (SpRuntimeException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void changeTransportProtocol(TransportProtocol transportProtocol) {
        producer.disconnect();
        producer.connect((KafkaTransportProtocol) transportProtocol);
    }
}
