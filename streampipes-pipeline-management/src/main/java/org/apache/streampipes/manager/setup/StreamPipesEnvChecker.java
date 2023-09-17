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

package org.apache.streampipes.manager.setup;

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.model.configuration.JwtSigningMode;
import org.apache.streampipes.model.configuration.LocalAuthConfig;
import org.apache.streampipes.model.configuration.SpCoreConfiguration;
import org.apache.streampipes.storage.api.ISpCoreConfigurationStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class StreamPipesEnvChecker {

  private static final Logger LOG = LoggerFactory.getLogger(StreamPipesEnvChecker.class);

  private ISpCoreConfigurationStorage configStorage;
  private SpCoreConfiguration coreConfig;

  private final Environment env;

  public StreamPipesEnvChecker() {
    this.env = Environments.getEnvironment();
  }

  public void updateEnvironmentVariables() {
    this.configStorage = StorageDispatcher
        .INSTANCE
        .getNoSqlStore()
        .getSpCoreConfigurationStorage();

    if (configStorage.getAll().size() > 0) {
      this.coreConfig = configStorage.get();

      LOG.info("Checking and updating environment variables...");
      updateJwtSettings();
    }
  }

  private void updateJwtSettings() {
    LocalAuthConfig localAuthConfig = coreConfig.getLocalAuthConfig();
    boolean incompleteConfig = false;
    var signingMode = env.getJwtSigningMode();
    var jwtSecret = env.getJwtSecret();
    var publicKeyLoc = env.getJwtPublicKeyLoc();
    var privateKeyLoc = env.getJwtPrivateKeyLoc();

    if (signingMode.exists()) {
      localAuthConfig.setJwtSigningMode(JwtSigningMode.valueOf(signingMode.getValue()));
    } else {
      if (localAuthConfig.getJwtSigningMode() != JwtSigningMode.HMAC) {
        localAuthConfig.setJwtSigningMode(JwtSigningMode.HMAC);
      }
    }

    if (jwtSecret.exists()) {
      localAuthConfig.setTokenSecret(jwtSecret.getValue());
    }
    if (publicKeyLoc.exists()) {
      try {
        localAuthConfig.setPublicKey(readPublicKey(publicKeyLoc.getValue()));
      } catch (IOException e) {
        incompleteConfig = true;
        LOG.warn("Could not read public key at location " + publicKeyLoc.getValue());
      }
    }

    if (!signingMode.exists()) {
      LOG.info(
          "No JWT signing mode provided (using default settings), "
              + "consult the docs to learn how to provide JWT settings");
    } else if (localAuthConfig.getJwtSigningMode() == JwtSigningMode.HMAC && !jwtSecret.exists()) {
      LOG.warn(
          "JWT signing mode set to HMAC but no secret provided (falling back to auto-generated secret), "
              + "provide a {} variable",
          jwtSecret.getEnvVariableName());
    } else if (localAuthConfig.getJwtSigningMode() == JwtSigningMode.RSA
        && ((!publicKeyLoc.exists() || !privateKeyLoc.exists()) || incompleteConfig)) {
      LOG.warn(
          "JWT signing mode set to RSA but no public or private key location provided, "
              + "do you provide {} and {} variables?",
          privateKeyLoc.getEnvVariableName(),
          publicKeyLoc.getEnvVariableName());
    }
    if (!incompleteConfig) {
      LOG.info("Updating local auth config with signing mode {}", localAuthConfig.getJwtSigningMode().name());
      coreConfig.setLocalAuthConfig(localAuthConfig);
      configStorage.updateElement(coreConfig);
    }
  }

  private String readPublicKey(String publicKeyLocation) throws IOException {
    return Files.readString(Paths.get(publicKeyLocation));
  }
}
