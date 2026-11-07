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

package org.apache.streampipes.couchdb.client;

import java.net.URI;
import java.time.Duration;
import java.util.Objects;

/**
 * Connection settings of a {@link CouchServer}.
 *
 * @param baseUrl        server URL without trailing slash, for example {@code http://localhost:5984}
 * @param username       user for Basic authentication, {@code null} for none
 * @param password       password for Basic authentication
 * @param connectTimeout timeout for establishing a connection
 * @param requestTimeout timeout for a complete request
 */
public record CouchServerConfig(String baseUrl,
                                String username,
                                String password,
                                Duration connectTimeout,
                                Duration requestTimeout) {

  public CouchServerConfig {
    Objects.requireNonNull(baseUrl, "baseUrl");
    URI.create(baseUrl);
    while (baseUrl.endsWith("/")) {
      baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
    }
    Objects.requireNonNull(connectTimeout, "connectTimeout");
    Objects.requireNonNull(requestTimeout, "requestTimeout");
  }

  @Override
  public String toString() {
    return "CouchServerConfig[baseUrl=" + baseUrl + ", username=" + username + "]";
  }
}
