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

package org.apache.streampipes.model.extensions.transport;

public class ExtensionServiceBrokerResponseEnvelope {

  private String requestId;
  private int statusCode;
  private String payload;
  private byte[] payloadBytes;
  private ExtensionServiceBrokerErrorEnvelope error;

  public ExtensionServiceBrokerResponseEnvelope() {
  }

  public ExtensionServiceBrokerResponseEnvelope(String requestId,
                                                int statusCode,
                                                String payload,
                                                ExtensionServiceBrokerErrorEnvelope error) {
    this(requestId, statusCode, payload, null, error);
  }

  public ExtensionServiceBrokerResponseEnvelope(String requestId,
                                                int statusCode,
                                                String payload,
                                                byte[] payloadBytes,
                                                ExtensionServiceBrokerErrorEnvelope error) {
    this.requestId = requestId;
    this.statusCode = statusCode;
    this.payload = payload;
    this.payloadBytes = payloadBytes;
    this.error = error;
  }

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

  public String getPayload() {
    return payload;
  }

  public void setPayload(String payload) {
    this.payload = payload;
  }

  public byte[] getPayloadBytes() {
    return payloadBytes;
  }

  public void setPayloadBytes(byte[] payloadBytes) {
    this.payloadBytes = payloadBytes;
  }

  public ExtensionServiceBrokerErrorEnvelope getError() {
    return error;
  }

  public void setError(ExtensionServiceBrokerErrorEnvelope error) {
    this.error = error;
  }
}
