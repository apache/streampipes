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

package org.apache.streampipes.service.core.oauth2.util;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Decides whether the client may be redirected to a URI after an OAuth login. The URI must have the
 * same scheme, host and port as the configured OAuth redirect URI.
 */
public class RedirectUriValidator {

  private RedirectUriValidator() {
  }

  public static boolean isAuthorized(String candidate,
                                     String authorizedRedirectUri) {
    if (candidate == null || authorizedRedirectUri == null) {
      return false;
    }
    try {
      var candidateUri = new URI(candidate);
      var authorizedUri = new URI(authorizedRedirectUri);
      return candidateUri.getScheme() != null
          && candidateUri.getHost() != null
          && candidateUri.getScheme().equalsIgnoreCase(authorizedUri.getScheme())
          && candidateUri.getHost().equalsIgnoreCase(authorizedUri.getHost())
          && port(candidateUri) == port(authorizedUri);
    } catch (URISyntaxException e) {
      return false;
    }
  }

  private static int port(URI uri) {
    if (uri.getPort() != -1) {
      return uri.getPort();
    }
    return "https".equalsIgnoreCase(uri.getScheme()) ? 443 : 80;
  }
}
