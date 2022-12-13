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
package org.apache.streampipes.dataexplorer.commons.configs;

import org.apache.streampipes.svcdiscovery.api.model.ConfigItem;

import java.util.Arrays;
import java.util.List;

public class CouchDbConfigurations {

  public static List<ConfigItem> getDefaults() {
    return Arrays.asList(
        ConfigItem.from(CouchDbEnvKeys.COUCHDB_HOST, "couchdb", "Hostname for CouchDB to store image blobs"),
        ConfigItem.from(CouchDbEnvKeys.COUCHDB_PORT, 5984, ""),
        ConfigItem.from(CouchDbEnvKeys.COUCHDB_PROTOCOL, "http", "")
    );
  }

}