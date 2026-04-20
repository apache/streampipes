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

import java.util.Objects;

public record ExtensionServiceRequest(ExtensionServiceRequestTarget target,
                                      ExtensionServiceRequestMethod method,
                                      String payload,
                                      String authToken,
                                      boolean acceptJsonResponse) {

  public ExtensionServiceRequest {
    Objects.requireNonNull(target);
    Objects.requireNonNull(method);
  }

  public static ExtensionServiceRequest get(ExtensionServiceRequestTarget target, String authToken) {
    return get(target, authToken, true);
  }

  public static ExtensionServiceRequest get(ExtensionServiceRequestTarget target,
                                            String authToken,
                                            boolean acceptJsonResponse) {
    return new ExtensionServiceRequest(target, ExtensionServiceRequestMethod.GET, null, authToken, acceptJsonResponse);
  }

  public static ExtensionServiceRequest post(ExtensionServiceRequestTarget target,
                                             String payload,
                                             String authToken) {
    return new ExtensionServiceRequest(target, ExtensionServiceRequestMethod.POST, payload, authToken, true);
  }

  public static ExtensionServiceRequest delete(ExtensionServiceRequestTarget target, String authToken) {
    return new ExtensionServiceRequest(target, ExtensionServiceRequestMethod.DELETE, null, authToken, true);
  }
}
