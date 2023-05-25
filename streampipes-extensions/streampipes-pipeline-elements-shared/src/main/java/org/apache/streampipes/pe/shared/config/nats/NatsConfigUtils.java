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

package org.apache.streampipes.pe.shared.config.nats;

import org.apache.streampipes.extensions.api.extractor.IParameterExtractor;
import org.apache.streampipes.model.nats.NatsConfig;

public class NatsConfigUtils {

  public static final String SUBJECT_KEY = "subject";
  public static final String URLS_KEY = "natsUrls";

  public static final String ACCESS_MODE = "access-mode";
  public static final String ANONYMOUS_ACCESS = "anonymous-alternative";
  public static final String USERNAME_ACCESS = "username-alternative";
  public static final String USERNAME_GROUP = "username-group";
  public static final String USERNAME_KEY = "username";
  public static final String PASSWORD_KEY = "password";

  public static final String CONNECTION_PROPERTIES = "connection-properties";
  public static final String NONE_PROPERTIES = "none-properties-alternative";
  public static final String CUSTOM_PROPERTIES = "custom-properties-alternative";
  public static final String CONNECTION_PROPERTIES_GROUP = "connection-group";
  public static final String PROPERTIES_KEY = "properties";

  public static NatsConfig from(IParameterExtractor<?> extractor) {
    String subject = extractor.singleValueParameter(SUBJECT_KEY, String.class);
    String natsUrls = extractor.singleValueParameter(URLS_KEY, String.class);
    String authentication = extractor.selectedAlternativeInternalId(ACCESS_MODE);
    String connectionProperties = extractor.selectedAlternativeInternalId(CONNECTION_PROPERTIES);
    String username = null;
    String password = null;
    String properties = null;

    if (authentication.equals(USERNAME_ACCESS)) {
      username = extractor.singleValueParameter(USERNAME_KEY, String.class);
      password = extractor.secretValue(PASSWORD_KEY);
    }

    if (connectionProperties.equals(CONNECTION_PROPERTIES)) {
      properties = extractor.singleValueParameter(PROPERTIES_KEY, String.class);
    }

    NatsConfig natsConfig = new NatsConfig();
    natsConfig.setSubject(subject);
    natsConfig.setNatsUrls(natsUrls);
    natsConfig.setUsername(username);
    natsConfig.setPassword(password);
    natsConfig.setProperties(properties);

    return natsConfig;
  }


}
