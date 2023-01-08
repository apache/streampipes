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


package org.apache.streampipes.service.core.migrations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MigrationsHandler {

  private static final Logger LOG = LoggerFactory.getLogger(MigrationsHandler.class);

  public void performMigrations() {
    LOG.info("Checking for required migrations...");
    var availableMigrations = new AvailableMigrations().getAvailableMigrations();

    availableMigrations.forEach(migration -> {
      if (migration.shouldExecute()) {
        LOG.info("Performing migration: {}", migration.getDescription());
        try {
          migration.executeMigration();
        } catch (IOException e) {
          LOG.error("An error has occurred while executing migration '{}'", migration.getDescription(), e);
        }
      }
    });

    LOG.info("All migrations completed.");
  }
}
