package org.apache.streampipes.connect.adapter.preprocessing.elements;/*
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

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.connect.adapter.model.pipeline.AdapterPipelineElement;
import org.apache.streampipes.connect.adapter.util.TransportFormatSelector;
import org.apache.streampipes.dataformat.SpDataFormatDefinition;
import org.apache.streampipes.messaging.jms.ActiveMQPublisher;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.grounding.JmsTransportProtocol;
import org.apache.streampipes.model.grounding.TransportFormat;
import org.apache.streampipes.model.grounding.TransportProtocol;

import java.util.Map;

public class SendToJmsAdapterSink implements AdapterPipelineElement {
    private ActiveMQPublisher activeMQPublisher;
    private SpDataFormatDefinition dataFormatDefinition;

    public SendToJmsAdapterSink(AdapterDescription adapterDescription) {
        activeMQPublisher = new ActiveMQPublisher();

        JmsTransportProtocol jmsProtocol = (JmsTransportProtocol) adapterDescription
                .getEventGrounding()
                .getTransportProtocol();

        TransportFormat transportFormat = adapterDescription
                .getEventGrounding()
                .getTransportFormats()
                .get(0);

        this.dataFormatDefinition =
                new TransportFormatSelector(transportFormat).getDataFormatDefinition();

        try {
            activeMQPublisher.connect(jmsProtocol);
        } catch (SpRuntimeException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Map<String, Object> process(Map<String, Object> event) {
        try {
            if (event != null) {
                activeMQPublisher.publish(dataFormatDefinition.fromMap(event));
            }
        } catch (SpRuntimeException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void changeTransportProtocol(TransportProtocol transportProtocol) {
        try {
            activeMQPublisher.disconnect();
            activeMQPublisher.connect((JmsTransportProtocol) transportProtocol);
        } catch (SpRuntimeException e) {
            e.printStackTrace();
        }
    }
}
