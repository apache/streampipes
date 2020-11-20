package org.apache.streampipes.node.controller.container.rest;/*
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
import org.apache.streampipes.node.controller.container.management.relay.EventRelayManager;
import org.apache.streampipes.node.controller.container.management.relay.RunningRelayInstances;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

public class DebugRelayResource extends AbstractNodeContainerResource {

    // TODO: Debug-only.
    @POST
    @Path("/relay/start")
    public Response debugRelayEventStream(String msg) throws SpRuntimeException {
        // TODO implement

        System.out.println(msg);
        EventRelayManager eventRelayManager = new EventRelayManager();
        eventRelayManager.start();
        RunningRelayInstances.INSTANCE.addRelay(eventRelayManager.getRelayedTopic(), eventRelayManager);

        return ok();
    }

    @POST
    @Path("/relay/stop")
    public Response debugStopRelayEventStream(String msg) throws SpRuntimeException {
        // TODO implement

        System.out.println(msg);
        EventRelayManager eventRelayManager = RunningRelayInstances.INSTANCE.get("org.apache.streampipes.flowrate01");
        assert eventRelayManager != null;
        eventRelayManager.stop();

        return ok();
    }
}
