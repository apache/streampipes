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
import org.apache.streampipes.storage.couchdb.serializer.GsonSerializer;

import org.lightcouch.CouchDbClient;



public class CouchDbClientFactory {

  private final CouchDbPropertiesFactory propertiesFactory;

  public CouchDbClientFactory(CouchDbPropertiesFactory propertiesFactory) {
    this.propertiesFactory = propertiesFactory;
  }

  public CouchDbClient createGsonClient(String dbName) {
    CouchDbClient client = new CouchDbClient(propertiesFactory.create(dbName));
    client.setGsonBuilder(GsonSerializer.getGsonBuilder());
    return client;
  }

  public CouchDbClient createPrincipalClient(String dbName) {
    CouchDbClient client = new CouchDbClient(propertiesFactory.create(dbName));
    client.setGsonBuilder(GsonSerializer.getPrincipalGsonBuilder());
    return client;
  }

  public CouchDbClient createAdapterClient(String dbName) {
    CouchDbClient client = new CouchDbClient(propertiesFactory.create(dbName));
    client.setGsonBuilder(GsonSerializer.getAdapterGsonBuilder());
    return client;
  }

  public CouchDbClient createDefaultClient(String dbName) {
    return new CouchDbClient(propertiesFactory.create(dbName));
  }
}

