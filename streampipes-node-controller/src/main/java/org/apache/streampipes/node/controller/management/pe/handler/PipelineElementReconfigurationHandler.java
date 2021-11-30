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
package org.apache.streampipes.node.controller.management.pe.handler;


import org.apache.streampipes.messaging.EventProducer;
import org.apache.streampipes.messaging.jms.ActiveMQPublisher;
import org.apache.streampipes.messaging.kafka.SpKafkaProducer;
import org.apache.streampipes.messaging.mqtt.MqttPublisher;
import org.apache.streampipes.model.Response;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.grounding.*;
import org.apache.streampipes.model.pipeline.PipelineElementReconfigurationEntity;
import org.apache.streampipes.model.staticproperty.CodeInputStaticProperty;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.node.controller.management.IHandler;
import org.apache.streampipes.node.controller.management.pe.storage.RunningInvocableInstances;
import org.apache.streampipes.node.controller.utils.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PipelineElementReconfigurationHandler implements IHandler<Response> {

    private static final Logger LOG = LoggerFactory.getLogger(PipelineElementReconfigurationHandler.class.getCanonicalName());

    private static final String DOT = ".";
    private static final String RECONFIGURATION_TOPIC = "org.apache.streampipes.control.event.reconfigure";

    private final InvocableStreamPipesEntity graph;
    private final PipelineElementReconfigurationEntity reconfigurationEntity;

    public PipelineElementReconfigurationHandler(InvocableStreamPipesEntity graph,
                                                 PipelineElementReconfigurationEntity reconfigurationEntity) {
        this.graph = graph;
        this.reconfigurationEntity = reconfigurationEntity;
    }

    @Override
    public Response handle() {

        Response response = new Response();
        response.setElementId(graph.getElementId());
        response.setSuccess(false);

        EventProducer pub = getReconfigurationEventProducer();

        byte [] reconfigurationEvent = reconfigurationToByteArray();

        LOG.info("Publish reconfiguration event to pipeline element {}", graph.getDeploymentRunningInstanceId());
        pub.publish(reconfigurationEvent);
        pub.disconnect();

        adaptPipelineDescription();

        response.setSuccess(true);
        return response;
    }

    private EventProducer getReconfigurationEventProducer() {
        TransportProtocol tp = getReconfigurationTransportProtocol();
        EventProducer pub;
        if(tp instanceof KafkaTransportProtocol){
            pub = new SpKafkaProducer();
            pub.connect(tp);
        } else if (tp instanceof JmsTransportProtocol){
            pub = new ActiveMQPublisher();
            pub.connect(tp);
        } else{
            pub = new MqttPublisher();
            pub.connect(tp);
        }
        return pub;
    }

    // TODO: Update in case other StaticProperty types will be supported in the future
    private byte[] reconfigurationToByteArray() {
        Map<String, String> reconfigurationEventMap = new HashMap<>();
        reconfigurationEntity.getReconfiguredStaticProperties().forEach(staticProperty -> {
            if (staticProperty instanceof FreeTextStaticProperty) {
                reconfigurationEventMap.put(staticProperty.getInternalName(),
                        ((FreeTextStaticProperty) staticProperty).getValue());
            } else if (staticProperty instanceof CodeInputStaticProperty) {
                reconfigurationEventMap.put(staticProperty.getInternalName(),
                        ((CodeInputStaticProperty) staticProperty).getValue());
            }
        });
        return HttpUtils.serialize(reconfigurationEventMap).getBytes(StandardCharsets.UTF_8);
    }

    private void adaptPipelineDescription(){
        List<StaticProperty> staticProperties = new ArrayList<>();
        graph.getStaticProperties().forEach(sp -> {
            int ind = graph.getStaticProperties().indexOf(sp);
            staticProperties.add(ind, sp);
            reconfigurationEntity.getReconfiguredStaticProperties().forEach(rp -> {
                if(sp.getInternalName().equals(rp.getInternalName())){
                    staticProperties.remove(ind);
                    staticProperties.add(ind, rp);
                }
            });
        });
        graph.setStaticProperties(staticProperties);
        RunningInvocableInstances.INSTANCE.remove(reconfigurationEntity.getDeploymentRunningInstanceId());
        RunningInvocableInstances.INSTANCE.add(reconfigurationEntity.getDeploymentRunningInstanceId(), graph);
    }

    private TransportProtocol getReconfigurationTransportProtocol() {
        TransportProtocol tp = graph.getInputStreams().get(0).getEventGrounding().getTransportProtocol();
        TopicDefinition topic = generateReconfigurationTopicFromId(graph.getDeploymentRunningInstanceId());
        tp.setTopicDefinition(topic);
        return tp;
    }

    private TopicDefinition generateReconfigurationTopicFromId(String runningInstanceId) {
        return new SimpleTopicDefinition( RECONFIGURATION_TOPIC + DOT + runningInstanceId);
    }
}
