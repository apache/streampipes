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
package org.apache.streampipes.commons.security;

import org.apache.streampipes.commons.constants.DefaultEnvValues;

import java.nio.charset.StandardCharsets;

public final class ServiceAccountSecret {

  private ServiceAccountSecret() {
  }

  public static boolean isLegacyDefault(String secret) {
    return DefaultEnvValues.INITIAL_CLIENT_SECRET_DEFAULT.equals(secret);
  }

  public static boolean isValid(String secret) {
    return secret != null && !secret.isBlank() && !isLegacyDefault(secret)
        && secret.getBytes(StandardCharsets.UTF_8).length >= 32;
  }

  public static String requireValid(String secret, String setting) {
    if (!isValid(secret)) {
      throw new IllegalArgumentException(setting
          + " must contain a non-default service secret of at least 32 bytes. "
          + "Run the Compose configure helper or provision the same random secret to backend and extensions.");
    }
    return secret;
  }
}
