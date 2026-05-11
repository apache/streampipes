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

package org.apache.streampipes.extensions.connectors.opcua.utils;

import org.apache.streampipes.commons.exceptions.SpConfigurationException;
import org.apache.streampipes.extensions.connectors.opcua.config.OpcUaConfig;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.util.concurrent.ExecutionException;

public class OpcUaUtilTest {

  private static final String SERVER_ADDRESS_WITH_OPC_PREFIX = "opc.tcp://example.com";

  @Test
  public void testAddOpcPrefixIfNotExistsWithPrefix() {
    var result = OpcUaUtils.addOpcPrefixIfNotExists(SERVER_ADDRESS_WITH_OPC_PREFIX);
    Assertions.assertEquals(SERVER_ADDRESS_WITH_OPC_PREFIX, result);
  }

  @Test
  public void testAddOpcPrefixIfNotExistsNoPrefix() {
    var result = OpcUaUtils.addOpcPrefixIfNotExists("example.com");
    Assertions.assertEquals(SERVER_ADDRESS_WITH_OPC_PREFIX, result);
  }

  @Test
  public void testExtractDescriptionFromNestedConnectionException() {
    var exception = new ExecutionException(
        new ConnectException("Connection refused: localhost/127.0.0.1:4840")
    );

    var result = ExceptionMessageExtractor.getDescription(exception);

    Assertions.assertEquals("Connection refused: localhost/127.0.0.1:4840", result);
  }

  @Test
  public void testCreateMeaningfulConnectionExceptionWithoutCauseChain() throws Exception {
    var method = OpcUaUtils.class.getDeclaredMethod(
        "makeConnectionException",
        org.apache.streampipes.extensions.connectors.opcua.config.OpcUaConfig.class,
        Throwable.class
    );
    method.setAccessible(true);

    var config = new OpcUaConfig();
    config.setOpcServerURL("opc.tcp://localhost:4840");

    var result = (SpConfigurationException) method.invoke(
        null,
        config,
        new ExecutionException(new ConnectException("Connection refused: localhost/127.0.0.1:4840"))
    );

    Assertions.assertEquals(
        "Could not connect to the OPC UA server at opc.tcp://localhost:4840: "
            + "Connection refused: localhost/127.0.0.1:4840",
        result.getMessage()
    );
    Assertions.assertNull(result.getCause());
  }
}
