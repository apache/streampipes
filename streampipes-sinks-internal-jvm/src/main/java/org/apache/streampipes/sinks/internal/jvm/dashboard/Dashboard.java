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

package org.apache.streampipes.sinks.internal.jvm.dashboard;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.apache.streampipes.messaging.jms.ActiveMQPublisher;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.sinks.internal.jvm.config.SinksInternalJvmConfig;
import org.apache.streampipes.wrapper.context.EventSinkRuntimeContext;
import org.apache.streampipes.wrapper.runtime.EventSink;

public class Dashboard implements EventSink<DashboardParameters> {

    private ActiveMQPublisher publisher;
    private JsonDataFormatDefinition jsonDataFormatDefinition;

    public Dashboard() {
        this.jsonDataFormatDefinition = new JsonDataFormatDefinition();
    }

    @Override
    public void onInvocation(DashboardParameters parameters,
                             EventSinkRuntimeContext runtimeContext) throws SpRuntimeException {
        this.publisher = new ActiveMQPublisher(
                SinksInternalJvmConfig.INSTANCE.getJmsHost(),
                SinksInternalJvmConfig.INSTANCE.getJmsPort(),
                makeTopic(parameters.getGraph().getInputStreams().get(0), parameters.getVisualizationName()));
    }

    private String makeTopic(SpDataStream inputStream, String visualizationName) {
        return extractTopic(inputStream)
                + "-"
                + visualizationName.replaceAll(" ", "").toLowerCase();
    }

    private String extractTopic(SpDataStream inputStream) {
        return inputStream.getEventGrounding().getTransportProtocol().getTopicDefinition().getActualTopicName();
    }

    @Override
    public void onEvent(Event event) {
        try {
            publisher.publish(jsonDataFormatDefinition.fromMap(event.getRaw()));
        } catch (SpRuntimeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() throws SpRuntimeException {
        this.publisher.disconnect();
    }
}
