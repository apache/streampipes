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

package org.apache.streampipes.service.core;

import org.apache.streampipes.commons.constants.Envs;
import org.apache.streampipes.config.backend.BackendConfig;
import org.apache.streampipes.config.backend.model.JwtSigningMode;
import org.apache.streampipes.config.backend.model.LocalAuthConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class StreamPipesEnvChecker {

  private static final Logger LOG = LoggerFactory.getLogger(StreamPipesEnvChecker.class);

  BackendConfig coreConfig;

  public void updateEnvironmentVariables() {
    this.coreConfig = BackendConfig.INSTANCE;

    LOG.info("Checking and updating environment variables...");
    updateJwtSettings();
  }

  private void updateJwtSettings() {
    LocalAuthConfig localAuthConfig = coreConfig.getLocalAuthConfig();
    boolean incompleteConfig = false;
    if (Envs.SP_JWT_SIGNING_MODE.exists()) {
      localAuthConfig.setJwtSigningMode(JwtSigningMode.valueOf(Envs.SP_JWT_SIGNING_MODE.getValue()));
    }
    if (Envs.SP_JWT_SECRET.exists()) {
      localAuthConfig.setTokenSecret(Envs.SP_JWT_SECRET.getValue());
    }
    if (Envs.SP_JWT_PUBLIC_KEY_LOC.exists()) {
      try {
        localAuthConfig.setPublicKey(readPublicKey(Envs.SP_JWT_PUBLIC_KEY_LOC.getValue()));
      } catch (IOException e) {
        incompleteConfig = true;
        LOG.warn("Could not read public key at location " + Envs.SP_JWT_PUBLIC_KEY_LOC);
      }
    }

    if (!Envs.SP_JWT_SIGNING_MODE.exists()) {
      LOG.info(
          "No JWT signing mode provided (using default settings), "
              + "consult the docs to learn how to provide JWT settings");
    } else if (localAuthConfig.getJwtSigningMode() == JwtSigningMode.HMAC && !Envs.SP_JWT_SECRET.exists()) {
      LOG.warn(
          "JWT signing mode set to HMAC but no secret provided (falling back to auto-generated secret), "
              + "provide a {} variable",
          Envs.SP_JWT_SECRET.getEnvVariableName());
    } else if (localAuthConfig.getJwtSigningMode() == JwtSigningMode.RSA
        && ((!Envs.SP_JWT_PUBLIC_KEY_LOC.exists() || !Envs.SP_JWT_PRIVATE_KEY_LOC.exists()) || incompleteConfig)) {
      LOG.warn(
          "JWT signing mode set to RSA but no public or private key location provided, "
              + "do you provide {} and {} variables?",
          Envs.SP_JWT_PRIVATE_KEY_LOC.getEnvVariableName(),
          Envs.SP_JWT_PUBLIC_KEY_LOC.getEnvVariableName());
    }
    if (!incompleteConfig) {
      LOG.info("Updating local auth config with signing mode {}", localAuthConfig.getJwtSigningMode().name());
      coreConfig.updateLocalAuthConfig(localAuthConfig);
    }
  }

  private String readPublicKey(String publicKeyLocation) throws IOException {
    return Files.readString(Paths.get(publicKeyLocation));
  }
}
