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

package org.apache.streampipes.service.core.migrations.v093.migrator;

import org.apache.streampipes.model.connect.adapter.migration.IAdapterConverter;
import org.apache.streampipes.model.connect.adapter.migration.MigrationHelpers;
import org.apache.streampipes.model.connect.adapter.migration.SpecificAdapterConverter;

import com.google.gson.JsonObject;
import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpecificAdapterMigrator implements AdapterMigrator {

  private static final Logger LOG = LoggerFactory.getLogger(SpecificAdapterMigrator.class);
  private final MigrationHelpers helpers;
  private final IAdapterConverter converter;

  public SpecificAdapterMigrator(MigrationHelpers helpers) {
    this.helpers = helpers;
    this.converter = new SpecificAdapterConverter(false);
  }

  @Override
  public void migrate(CouchDbClient couchDbClient,
                      JsonObject adapter) {
    var adapterName = helpers.getAdapterName(adapter);
    var convertedAdapter = converter.convert(adapter);

    couchDbClient.update(convertedAdapter);

    LOG.info("Successfully migrated adapter {}", adapterName);
  }
}
