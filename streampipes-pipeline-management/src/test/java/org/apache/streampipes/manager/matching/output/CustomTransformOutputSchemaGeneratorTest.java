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

package org.apache.streampipes.manager.matching.output;

import org.apache.streampipes.manager.api.extensions.ExtensionServiceOperationResult;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.output.CustomTransformOutputStrategy;
import org.apache.streampipes.model.staticproperty.SecretStaticProperty;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.svcdiscovery.api.ISpServiceDiscovery;
import org.apache.streampipes.user.management.encryption.SecretEncryptionManager;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomTransformOutputSchemaGeneratorTest {

  @Test
  void makeRequestBodyDecryptsSecretWithoutMutatingInvocation() throws IOException {
    var plainValue = "my-secret-value";
    var encryptedValue = SecretEncryptionManager.encrypt(plainValue);
    var secretProperty = makeSecretProperty(encryptedValue, true);
    var invocation = new DataProcessorInvocation();
    invocation.setStaticProperties(List.of(secretProperty));

    var generator = new CustomTransformOutputSchemaGenerator(
        mock(ISpServiceDiscovery.class),
        new CustomTransformOutputStrategy(),
        invocation,
        mock(ExtensionServiceRequestManager.class)
    );

    var requestBody = generator.makeRequestBody();

    var requestInvocation = JacksonSerializer.getObjectMapper()
                                             .readValue(requestBody, DataProcessorInvocation.class);
    var requestSecret = (SecretStaticProperty) requestInvocation.getStaticProperties().get(0);
    assertEquals(plainValue, requestSecret.getValue());
    assertFalse(requestSecret.getEncrypted());

    assertEquals(encryptedValue, secretProperty.getValue());
    assertTrue(secretProperty.getEncrypted());
  }

  @Test
  void throwsWhenTheExtensionCannotComputeTheOutputSchema() {
    var invocation = invocation();
    var generator = new CustomTransformOutputSchemaGenerator(
        mock(ISpServiceDiscovery.class),
        new CustomTransformOutputStrategy(),
        invocation,
        mock(ExtensionServiceRequestManager.class)
    );

    var exception = assertThrows(OutputSchemaUnavailableException.class,
        () -> generator.buildFromOneStream(null));

    assertEquals(invocation.getAppId(), exception.getAppId());
  }

  @Test
  void throwsWhenTheExtensionReturnsAnErrorResponse() throws IOException {
    var invocation = invocation();
    var serviceDiscovery = mock(ISpServiceDiscovery.class);
    var requestManager = mock(ExtensionServiceRequestManager.class);
    when(serviceDiscovery.getService(anyString(), eq(true), anyList())).thenReturn(List.of(service()));
    when(requestManager.request(any())).thenReturn(new ExtensionServiceOperationResult(500, null));
    var generator = new CustomTransformOutputSchemaGenerator(
        serviceDiscovery,
        new CustomTransformOutputStrategy(),
        invocation,
        requestManager
    );

    var exception = assertThrows(IllegalStateException.class,
        () -> generator.buildFromOneStream(null));

    assertTrue(exception.getMessage().contains("status code: 500"));
  }

  @Test
  void treatsGatewayUnavailableResponsesAsOutputSchemaUnavailable() throws IOException {
    var invocation = invocation();
    var serviceDiscovery = mock(ISpServiceDiscovery.class);
    var requestManager = mock(ExtensionServiceRequestManager.class);
    when(serviceDiscovery.getService(anyString(), eq(true), anyList())).thenReturn(List.of(service()));
    when(requestManager.request(any())).thenReturn(new ExtensionServiceOperationResult(503, null));
    var generator = new CustomTransformOutputSchemaGenerator(
        serviceDiscovery,
        new CustomTransformOutputStrategy(),
        invocation,
        requestManager
    );

    var exception = assertThrows(OutputSchemaUnavailableException.class,
        () -> generator.buildFromOneStream(null));

    assertEquals(invocation.getAppId(), exception.getAppId());
  }

  @Test
  void throwsWhenTheExtensionReturnsAnInvalidSchema() throws IOException {
    var invocation = invocation();
    var serviceDiscovery = mock(ISpServiceDiscovery.class);
    var requestManager = mock(ExtensionServiceRequestManager.class);
    when(serviceDiscovery.getService(anyString(), eq(true), anyList())).thenReturn(List.of(service()));
    when(requestManager.request(any())).thenReturn(new ExtensionServiceOperationResult(
        200, "not-an-event-schema".getBytes(StandardCharsets.UTF_8)));
    var generator = new CustomTransformOutputSchemaGenerator(
        serviceDiscovery,
        new CustomTransformOutputStrategy(),
        invocation,
        requestManager
    );

    var exception = assertThrows(IllegalStateException.class,
        () -> generator.buildFromOneStream(null));

    assertTrue(exception.getMessage().contains("invalid output schema"));
  }

  private DataProcessorInvocation invocation() {
    var invocation = new DataProcessorInvocation();
    invocation.setAppId("org.apache.streampipes.test.processor");
    invocation.setStaticProperties(List.of());
    return invocation;
  }

  private SpServiceRegistration service() {
    var service = new SpServiceRegistration();
    service.setHost("localhost");
    service.setPort(8080);
    service.setSvcId("test-service");
    return service;
  }

  private SecretStaticProperty makeSecretProperty(String value,
                                                  boolean encrypted) {
    var secretProperty = new SecretStaticProperty("secret", "Secret", "");
    secretProperty.setValue(value);
    secretProperty.setEncrypted(encrypted);
    return secretProperty;
  }
}
