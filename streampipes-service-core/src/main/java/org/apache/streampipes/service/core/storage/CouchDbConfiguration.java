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

package org.apache.streampipes.service.core.storage;

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.couchdb.client.CouchServer;
import org.apache.streampipes.couchdb.client.httpclient5.HttpClient5Transport;
import org.apache.streampipes.storage.couchdb.CouchDbCodecs;
import org.apache.streampipes.storage.couchdb.CouchDbStores;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * The connection to the core's CouchDB, shared by all storages. Uses the HttpClient 5 transport, which held up
 * better than the JDK client under concurrent load (docs/couchdb-client-concept.md, section 6.2).
 */
@Configuration
public class CouchDbConfiguration {

  private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(1);
  private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(100);

  @Bean(destroyMethod = "close")
  public CouchServer couchServer() {
    return couchServer(Environments.getEnvironment());
  }

  @Bean
  public CouchDbCodecs couchDbCodecs() {
    return CouchDbCodecs.defaults();
  }

  @Bean
  public CouchDbStores couchDbStores(CouchServer couchServer, CouchDbCodecs couchDbCodecs) {
    return new CouchDbStores(couchServer, couchDbCodecs);
  }

  static CouchServer couchServer(Environment env) {
    return CouchServer.builder()
        .url(env.getCouchDbProtocol().getValueOrDefault(),
            env.getCouchDbHost().getValueOrDefault(),
            env.getCouchDbPort().getValueOrDefault())
        .credentials(env.getCouchDbUsername().getValueOrDefault(), env.getCouchDbPassword().getValueOrDefault())
        .connectTimeout(CONNECT_TIMEOUT)
        .requestTimeout(REQUEST_TIMEOUT)
        .transport(HttpClient5Transport::new)
        .build();
  }
}
