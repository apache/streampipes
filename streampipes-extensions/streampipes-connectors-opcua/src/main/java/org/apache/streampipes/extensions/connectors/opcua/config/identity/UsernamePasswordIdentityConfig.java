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

package org.apache.streampipes.extensions.connectors.opcua.config.identity;

import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfigBuilder;
import org.eclipse.milo.opcua.sdk.client.api.identity.UsernameProvider;

public class UsernamePasswordIdentityConfig implements IdentityConfig {

  private final String username;
  private final String password;

  public UsernamePasswordIdentityConfig(String username, String password) {
    this.username = username;
    this.password = password;
  }

  @Override
  public void configureIdentity(OpcUaClientConfigBuilder builder) {
    builder.setIdentityProvider(new UsernameProvider(username, password));
  }

  @Override
  public String toString() {
    return String.format("%s-%S", username, DigestUtils.sha256Hex(password));
  }
}
