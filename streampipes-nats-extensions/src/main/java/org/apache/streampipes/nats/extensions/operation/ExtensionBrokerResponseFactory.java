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

package org.apache.streampipes.nats.extensions.operation;

import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerErrorEnvelope;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerResponseEnvelope;

public final class ExtensionBrokerResponseFactory {

  public static final int HTTP_STATUS_OK = 200;
  public static final int HTTP_STATUS_BAD_REQUEST = 400;
  public static final int HTTP_STATUS_NOT_FOUND = 404;
  public static final int HTTP_STATUS_NOT_IMPLEMENTED = 501;
  public static final int HTTP_STATUS_INTERNAL_SERVER_ERROR = 500;

  private ExtensionBrokerResponseFactory() {
  }

  public static ExtensionServiceBrokerResponseEnvelope ok(String requestId, String payload) {
    return new ExtensionServiceBrokerResponseEnvelope(requestId, HTTP_STATUS_OK, payload, null);
  }

  public static ExtensionServiceBrokerResponseEnvelope okBytes(String requestId, byte[] payloadBytes) {
    return new ExtensionServiceBrokerResponseEnvelope(requestId, HTTP_STATUS_OK, null, payloadBytes, null);
  }

  public static ExtensionServiceBrokerResponseEnvelope badRequest(String requestId,
                                                                  String type,
                                                                  String message) {
    return new ExtensionServiceBrokerResponseEnvelope(
        requestId,
        HTTP_STATUS_BAD_REQUEST,
        null,
        new ExtensionServiceBrokerErrorEnvelope(type, message)
    );
  }

  public static ExtensionServiceBrokerResponseEnvelope notFound(String requestId,
                                                                String type,
                                                                String message) {
    return new ExtensionServiceBrokerResponseEnvelope(
        requestId,
        HTTP_STATUS_NOT_FOUND,
        null,
        new ExtensionServiceBrokerErrorEnvelope(type, message)
    );
  }

  public static ExtensionServiceBrokerResponseEnvelope badRequestInvalidPayload(String requestId,
                                                                                String message) {
    return badRequest(requestId, ExtensionBrokerConstants.ErrorType.INVALID_PAYLOAD, message);
  }

  public static ExtensionServiceBrokerResponseEnvelope badRequestInvalidTopic(String requestId,
                                                                              String message) {
    return badRequest(requestId, ExtensionBrokerConstants.ErrorType.INVALID_TOPIC, message);
  }

  public static ExtensionServiceBrokerResponseEnvelope badRequestInvalidCommand(String requestId,
                                                                                String message) {
    return badRequest(requestId, ExtensionBrokerConstants.ErrorType.INVALID_COMMAND, message);
  }

  public static ExtensionServiceBrokerResponseEnvelope notFound(String requestId,
                                                                String message) {
    return notFound(requestId, ExtensionBrokerConstants.ErrorType.NOT_FOUND, message);
  }

  public static ExtensionServiceBrokerResponseEnvelope unsupportedOperation(String requestId,
                                                                            String message) {
    return new ExtensionServiceBrokerResponseEnvelope(
        requestId,
        HTTP_STATUS_NOT_IMPLEMENTED,
        null,
        new ExtensionServiceBrokerErrorEnvelope(
            ExtensionBrokerConstants.ErrorType.UNSUPPORTED_OPERATION,
            message
        )
    );
  }

  public static ExtensionServiceBrokerResponseEnvelope error(String requestId, int statusCode, Exception e) {
    return new ExtensionServiceBrokerResponseEnvelope(
        requestId,
        statusCode,
        null,
        new ExtensionServiceBrokerErrorEnvelope(e.getClass().getSimpleName(), e.getMessage())
    );
  }

  public static boolean isBlank(String value) {
    return value == null || value.isBlank();
  }
}
