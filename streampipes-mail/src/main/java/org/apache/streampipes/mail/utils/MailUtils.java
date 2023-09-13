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
package org.apache.streampipes.mail.utils;

import org.apache.streampipes.commons.resources.Resources;
import org.apache.streampipes.model.configuration.GeneralConfig;
import org.apache.streampipes.model.configuration.SpCoreConfiguration;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MailUtils {

  public static String extractBaseUrl() {
    GeneralConfig config = getSpCoreConfiguration().getGeneralConfig();

    return config.getProtocol() + "://" + config.getHostname() + ":" + config.getPort();
  }

  public static String extractAppName() {
    return getSpCoreConfiguration().getGeneralConfig().getAppName();
  }

  public static String readResourceFileToString(String filename) throws IOException {
    return Resources.asString(filename, StandardCharsets.UTF_8);
  }

  public static SpCoreConfiguration getSpCoreConfiguration() {
    return StorageDispatcher.INSTANCE.getNoSqlStore().getSpCoreConfigurationStorage().get();
  }
}
