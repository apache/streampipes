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

import org.apache.streampipes.config.backend.BackendConfig;
import org.apache.streampipes.config.backend.model.GeneralConfig;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.io.IOException;

public class MailUtils {

  public static String extractBaseUrl() {
    GeneralConfig config = BackendConfig.INSTANCE.getGeneralConfig();

    return config.getProtocol() + "://" + config.getHostname() + ":" + config.getPort();
  }

  public static String extractAppName() {
    return BackendConfig.INSTANCE.getGeneralConfig().getAppName();
  }

  public static String readResourceFileToString(String filename) throws IOException {
    return Resources.toString(Resources.getResource(filename), Charsets.UTF_8);
  }
}
