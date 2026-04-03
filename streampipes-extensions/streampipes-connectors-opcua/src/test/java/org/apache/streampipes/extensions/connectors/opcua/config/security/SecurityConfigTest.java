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

package org.apache.streampipes.extensions.connectors.opcua.config.security;

import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.ByteString;
import org.eclipse.milo.opcua.stack.core.types.enumerated.ApplicationType;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MessageSecurityMode;
import org.eclipse.milo.opcua.stack.core.types.structured.ApplicationDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.UserTokenPolicy;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ubyte;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SecurityConfigTest {

  @Test
  void updateEndpointUrlUsesConfiguredHostAndPortButKeepsEndpointPath() throws Exception {
    var securityConfig = new SecurityConfig(
        MessageSecurityMode.None,
        SecurityPolicy.None,
        null
    );

    var endpoint = new EndpointDescription(
        "opc.tcp://localhost:4840/milo",
        new ApplicationDescription(
            "urn:test",
            "urn:test:product",
            null,
            ApplicationType.Server,
            null,
            null,
            null
        ),
        ByteString.NULL_VALUE,
        MessageSecurityMode.None,
        SecurityPolicy.None.getUri(),
        new UserTokenPolicy[0],
        null,
        ubyte(0)
    );

    var updated = securityConfig.updateEndpointUrl(
        endpoint,
        new URI("opc.tcp://127.0.0.1:32791/discovery").parseServerAuthority()
    );

    assertEquals("opc.tcp://127.0.0.1:32791/milo", updated.getEndpointUrl());
  }
}
