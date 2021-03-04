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
package org.apache.streampipes.manager.execution.http;
import org.apache.streampipes.model.eventrelay.SpDataStreamRelayContainer;

public class StreamRelayEndpointUrlGenerator extends EndpointUrlGenerator<SpDataStreamRelayContainer> {

    private static final String BASE_ROUTE = "api/v2/node/stream/relay";
    private static final String INVOKE_ROUTE = "/invoke";
    private static final String DETACH_ROUTE = "/detach";

    private final SpDataStreamRelayContainer streamRelay;

    public StreamRelayEndpointUrlGenerator(SpDataStreamRelayContainer streamRelay) {
        super(streamRelay);
        this.streamRelay = streamRelay;
    }

    @Override
    public String generateInvokeEndpoint() {
        return generateEndpoint(INVOKE_ROUTE);
    }

    @Override
    public String generateDetachEndpoint() {
        return generateEndpoint(DETACH_ROUTE)
                + SLASH
                + streamRelay.getRunningStreamRelayInstanceId();
    }

    private String generateEndpoint(String path) {
        return HTTP_PROTOCOL
                + streamRelay.getDeploymentTargetNodeHostname()
                + COLON
                + streamRelay.getDeploymentTargetNodePort()
                + SLASH
                + BASE_ROUTE
                + path;
    }
}
