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

import org.apache.streampipes.extensions.connectors.opcua.config.OpcUaConfig;

import org.eclipse.milo.opcua.stack.core.StatusCodes;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class OpcUaCertificateUtilsTest {

  private static final String SECURITY_REASON =
      "status=Bad_SecurityChecksFailed, description=An error occurred verifying security.";

  @Test
  void makeExceptionMessagePointsToUiApprovalWhenServerCertificateIsPending() {
    var config = new OpcUaConfig();
    config.setServerCertificateRejectedByClient(true);
    var exception = new UaException(new UaException(StatusCodes.Bad_SecurityChecksFailed, SECURITY_REASON));

    var result = OpcUaCertificateUtils.makeExceptionMessage(exception, config);

    assertTrue(result.contains("server certificate is not yet trusted by StreamPipes"));
    assertTrue(result.contains("Administrators can accept this certificate in the settings"));
    assertTrue(result.contains(SECURITY_REASON));
  }

  @Test
  void makeExceptionMessagePointsToServerTrustStoreWhenClientCertificateWasRejected() {
    var config = new OpcUaConfig();
    config.setServerCertificateValidated(true);
    var exception = new UaException(new UaException(StatusCodes.Bad_SecurityChecksFailed, SECURITY_REASON));

    var result = OpcUaCertificateUtils.makeExceptionMessage(exception, config);

    assertTrue(result.contains("server rejected the StreamPipes client certificate"));
    assertTrue(result.contains("server's trusted certificate store"));
    assertTrue(result.contains(SECURITY_REASON));
  }

  @Test
  void makeExceptionMessageFallsBackToNeutralGuidanceWhenDirectionIsUnknown() {
    var config = new OpcUaConfig();
    var exception = new UaException(new UaException(StatusCodes.Bad_SecurityChecksFailed, SECURITY_REASON));

    var result = OpcUaCertificateUtils.makeExceptionMessage(exception, config);

    assertTrue(result.contains("certificate-related OPC UA security error occurred"));
    assertTrue(result.contains("accept it in the settings"));
    assertTrue(result.contains("trusted certificate store"));
  }
}
