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

import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.apache.streampipes.messaging.jms.ActiveMQPublisher;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.serializers.json.GsonSerializer;
import org.apache.streampipes.sinks.internal.jvm.config.SinksInternalJvmConfig;
import org.apache.streampipes.wrapper.context.EventSinkRuntimeContext;
import org.apache.streampipes.wrapper.runtime.EventSink;

public class Dashboard implements EventSink<DashboardParameters> {

    private ActiveMQPublisher publisher;
    private JsonDataFormatDefinition jsonDataFormatDefinition;

    private static String DB_NAME = "visualizablepipeline";
    private static int DB_PORT = SinksInternalJvmConfig.INSTANCE.getCouchDbPort();
    private static String DB_HOST = SinksInternalJvmConfig.INSTANCE.getCouchDbHost();
    private static String DB_PROTOCOL = "http";


    private String visualizationId;
    private String visualizationRev;
    private String pipelineId;


    public Dashboard() {
        this.jsonDataFormatDefinition = new JsonDataFormatDefinition();
    }

    @Override
    public void onInvocation(DashboardParameters parameters, EventSinkRuntimeContext runtimeContext) throws SpRuntimeException {
        if (!saveToCouchDB(parameters.getGraph(), parameters)) {
            throw new SpRuntimeException("The schema couldn't be stored in the couchDB");
        }
        this.publisher = new ActiveMQPublisher(
                SinksInternalJvmConfig.INSTANCE.getJmsHost(),
                SinksInternalJvmConfig.INSTANCE.getJmsPort(),
                parameters.getElementId());
        this.pipelineId = parameters.getPipelineId();
    }

    @Override
    public void onEvent(Event event) {
        try {
            publisher.publish(jsonDataFormatDefinition.fromMap(event.getRaw()));
        } catch (SpRuntimeException e) {
            e.printStackTrace();
        }
    }

    private boolean saveToCouchDB(DataSinkInvocation invocationGraph, DashboardParameters params) {
        CouchDbClient dbClient = new CouchDbClient(new CouchDbProperties(DB_NAME, true, DB_PROTOCOL, DB_HOST, DB_PORT, null, null));
        dbClient.setGsonBuilder(GsonSerializer.getGsonBuilder());
        org.lightcouch.Response res = dbClient.save(DashboardModel.from(params));

        if (res.getError() == null) {
            visualizationId = res.getId();
            visualizationRev = res.getRev();
        }

        return res.getError() == null;
    }

    private boolean removeFromCouchDB() {
        CouchDbClient dbClient = new CouchDbClient(new CouchDbProperties(DB_NAME, true, DB_PROTOCOL, DB_HOST, DB_PORT, null, null));
        org.lightcouch.Response res = dbClient.remove(visualizationId, visualizationRev);

        return res.getError() == null;
    }

    @Override
    public void onDetach() throws SpRuntimeException {
        this.publisher.disconnect();
        if (!removeFromCouchDB()) {
            throw new SpRuntimeException("There was an error while deleting pipeline: '" + pipelineId + "'");
        }
    }
}
