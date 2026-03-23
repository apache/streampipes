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

package org.apache.streampipes.manager.api.extensions;

import java.nio.charset.StandardCharsets;

public class ExtensionServiceOperationResult {

  private final int statusCode;
  private final byte[] responseBody;

  public ExtensionServiceOperationResult(int statusCode, byte[] responseBody) {
    this.statusCode = statusCode;
    this.responseBody = responseBody;
  }

  public int statusCode() {
    return statusCode;
  }

  public String responseBody() {
    return responseBody == null ? null : new String(responseBody, StandardCharsets.UTF_8);
  }

  public byte[] responseBytes() {
    return responseBody;
  }

  public boolean isSuccess() {
    return statusCode >= 200 && statusCode < 300;
  }
}
