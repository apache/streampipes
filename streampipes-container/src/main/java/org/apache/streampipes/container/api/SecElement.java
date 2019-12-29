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

package org.apache.streampipes.container.api;

import org.apache.streampipes.container.declarer.InvocableDeclarer;
import org.apache.streampipes.container.declarer.SemanticEventConsumerDeclarer;
import org.apache.streampipes.container.init.DeclarersSingleton;
import org.apache.streampipes.container.init.RunningInstances;
import org.apache.streampipes.container.util.Util;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.sdk.extractor.DataSinkParameterExtractor;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/sec")
public class SecElement extends InvocableElement<DataSinkInvocation,
        SemanticEventConsumerDeclarer, DataSinkParameterExtractor> {

    public SecElement() {
        super(DataSinkInvocation.class);
    }

    @Override
    protected Map<String, SemanticEventConsumerDeclarer> getElementDeclarers() {
        return DeclarersSingleton.getInstance().getConsumerDeclarers();
    }

    @Override
    protected String getInstanceId(String uri, String elementId) {
        return Util.getInstanceId(uri, "sec", elementId);
    }

    @Override
    protected DataSinkParameterExtractor getExtractor(DataSinkInvocation graph) {
        return new DataSinkParameterExtractor(graph);
    }

    @Override
    protected DataSinkInvocation createGroundingDebugInformation(DataSinkInvocation graph) {
        graph.getInputStreams().forEach(is -> {
            TransportProtocol protocol = is.getEventGrounding().getTransportProtocol();
            protocol.setBrokerHostname("localhost");
            if (protocol instanceof KafkaTransportProtocol) {
                ((KafkaTransportProtocol) protocol).setKafkaPort(9094);
            }

        });

        return graph;
    }

    @GET
    @Path("{elementId}/{runningInstanceId}")
    @Produces(MediaType.TEXT_HTML)
    public Response getHtml(@PathParam("elementId") String elementId, @PathParam("runningInstanceId") String runningInstanceId) {

        InvocableDeclarer runningInstance = RunningInstances.INSTANCE.getInvocation(runningInstanceId);
        NamedStreamPipesEntity description = RunningInstances.INSTANCE.getDescription(runningInstanceId);

        if (runningInstance != null && runningInstance instanceof SemanticEventConsumerDeclarer && description != null
                && description instanceof DataSinkInvocation) {

            SemanticEventConsumerDeclarer instanceDeclarer = (SemanticEventConsumerDeclarer) runningInstance;
            DataSinkInvocation desctionDeclarer = (DataSinkInvocation) description;

            // TODO was previous getHtml, do we still need the whole method?
            return getResponse("HTML removed");


        } else {
            return getResponse("Error in element " + elementId);
       }
    }

    private Response getResponse(String text) {
        return Response.ok() //200
                    .entity(text)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                    .header("Access-Control-Allow-Credentials", "false")
                    .header("Access-Control-Max-Age", "60")
                    .allow("OPTIONS").build();
    }
}
