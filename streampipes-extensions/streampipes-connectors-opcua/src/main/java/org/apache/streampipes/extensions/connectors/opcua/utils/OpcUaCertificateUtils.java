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

import org.apache.streampipes.client.api.IStreamPipesClient;
import org.apache.streampipes.extensions.connectors.opcua.config.security.CompositeCertificateValidator;
import org.apache.streampipes.model.opcua.CertificateUsage;

import org.eclipse.milo.opcua.stack.core.UaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

public class OpcUaCertificateUtils {

  private static final Logger LOG = LoggerFactory.getLogger(OpcUaCertificateUtils.class);

  public static String getCoreCertificatePath() {
    return "/api/v2/admin/certificates";
  }

  public static String getCoreCertificateUsagePath() {
    return getCoreCertificatePath() + "/usage";
  }

  public static String getCoreTrustedCertificatePath() {
    return getCoreCertificatePath() + "/trusted";
  }

  public static boolean isCertificateException(ExecutionException e) {
    Throwable cause = e.getCause();

    if (cause instanceof UaException uaException) {
      return checkAndLogCertificateException(uaException);
    }

    Throwable nestedCause = cause != null ? cause.getCause() : null;
    if (nestedCause instanceof UaException uaException) {
      return checkAndLogCertificateException(uaException);
    }

    return false;
  }

  private static boolean checkAndLogCertificateException(UaException e) {
    var containsRejectedStatusCode = CompositeCertificateValidator.REJECTED_STATUS_CODES
        .contains(e.getStatusCode().getValue());

    if (containsRejectedStatusCode) {
      var statusCode = CompositeCertificateValidator.REJECTED_STATUS_CODES.stream().filter(code -> code.equals(e.getStatusCode().getValue())).findFirst();
      statusCode.ifPresent(sc -> LOG.warn("Status Code: {}", sc));
    }
    return containsRejectedStatusCode;
  }

  public static String makeExceptionMessage(ExecutionException e) {
    StringBuilder message = new StringBuilder(
        "The provided certificate could not be trusted. Administrators can accept this certificate in the settings. "
    );
    Throwable cause = e.getCause();
    if (cause != null) {
      message.append("Reason: ").append(cause.getMessage());
    }
    return message.toString();
  }

  public static void sendUsageToCore(String thumbprint,
                                     String associatedResourceId,
                                     IStreamPipesClient streamPipesClient) {
    try {
      var usage = new CertificateUsage(
          associatedResourceId,
          thumbprint
      );

      streamPipesClient
          .customRequest()
          .sendPost(OpcUaCertificateUtils.getCoreCertificateUsagePath(), usage);

    } catch (Exception ex) {
      LOG.error("Failed to report certificate usage to API", ex);
    }
  }
}
