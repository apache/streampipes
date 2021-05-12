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

import org.apache.streampipes.container.model.node.InvocableRegistration;
import org.apache.streampipes.node.controller.management.IHandler;
import org.apache.streampipes.node.controller.management.pe.PipelineElementLifeCycleState;
import org.apache.streampipes.node.controller.utils.HttpUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class ConsulInteractionHandler implements IHandler<Boolean> {

    private static final String HTTP_PROTOCOL = "http";
    private static final String SLASH = "/";
    private static final String SP_URL = "SP_URL";
    private static final String CONSUL_LOCATION = "CONSUL_LOCATION";
    private static final String CONSUL_REGISTRATION_ROUTE = "v1/agent/service/register";
    private static final String CONSUL_DEREGISTRATION_ROUTE = "v1/agent/service/deregister";
    private static final int CONSUL_DEFAULT_PORT = 8500;

    private final InvocableRegistration registration;
    private final PipelineElementLifeCycleState type;

    public ConsulInteractionHandler(InvocableRegistration registration, PipelineElementLifeCycleState type) {
        this.registration = registration;
        this.type = type;
    }

    @Override
    public Boolean handle() {
        String endpoint = consulURL().toString();
        switch(type) {
            case REGISTER:
                 endpoint = endpoint + SLASH + CONSUL_REGISTRATION_ROUTE;
                return HttpUtils.put(endpoint, registration.getConsulServiceRegistrationBody());
            case DEREGISTER:
                String svdId = registration.getConsulServiceRegistrationBody().getID();
                endpoint = endpoint + SLASH + CONSUL_DEREGISTRATION_ROUTE + SLASH + svdId;
                return HttpUtils.put(endpoint);
            default:
                return false;
        }
    }

    private static URL consulURL() {
        Map<String, String> env = System.getenv();
        URL url = null;

        if (env.containsKey(SP_URL)) {
            try {
                URL coreServerUrl = new URL(env.get(SP_URL));
                url = new URL(HTTP_PROTOCOL, coreServerUrl.getHost(), CONSUL_DEFAULT_PORT, "");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else if (env.containsKey(CONSUL_LOCATION)) {
            try {
                url = new URL(HTTP_PROTOCOL, env.get(CONSUL_LOCATION), CONSUL_DEFAULT_PORT, "");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                url = new URL(HTTP_PROTOCOL, "localhost", CONSUL_DEFAULT_PORT, "");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return url;
    }
}
