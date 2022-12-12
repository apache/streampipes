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

package org.apache.streampipes.storage.couchdb.utils;

import org.apache.streampipes.svcdiscovery.SpServiceDiscovery;
import org.apache.streampipes.svcdiscovery.api.SpConfig;

public enum CouchDbConfig {

  INSTANCE;

  private static final String COUCHDB_HOST = "SP_COUCHDB_HOST";
  private static final String COUCHDB_PORT = "SP_COUCHDB_PORT";
  private static final String PROTOCOL = "PROTOCOL";
  private SpConfig config;

  CouchDbConfig() {
    config = SpServiceDiscovery.getSpConfig("storage/couchdb");
    config.register(COUCHDB_HOST, "couchdb", "Hostname for the couch db service");
    config.register(COUCHDB_PORT, 5984, "Port for the couch db service");
    config.register(PROTOCOL, "http", "Protocol the couch db service");
  }

  public String getHost() {
    return config.getString(COUCHDB_HOST);
  }

  public void setHost(String host) {
    config.setString(COUCHDB_HOST, host);
  }

  public int getPort() {
    return config.getInteger(COUCHDB_PORT);
  }

  public String getProtocol() {
    return config.getString(PROTOCOL);
  }
}
