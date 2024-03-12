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

package org.apache.streampipes.sinks.notifications.jvm.migrations;

import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.migration.ModelMigratorConfig;

import static org.apache.streampipes.sinks.notifications.jvm.telegram.TelegramSink.TELEGRAM_NOTIFICATION_SINK_ID;

public class TelegramSinkMigrationV1 implements INotificationDataSinkMigrator {
  @Override
  public ModelMigratorConfig config() {
    return new ModelMigratorConfig(
      TELEGRAM_NOTIFICATION_SINK_ID,
      SpServiceTagPrefix.DATA_SINK,
      0,
      1
    );
  }
}
