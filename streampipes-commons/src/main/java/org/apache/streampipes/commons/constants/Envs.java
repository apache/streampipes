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
package org.apache.streampipes.commons.constants;

public enum Envs {

  SP_HOST("SP_HOST"),
  SP_PORT("SP_PORT"),

  @Deprecated(since = "0.90.0", forRemoval = true)
  SP_CONSUL_LOCATION("CONSUL_LOCATION"),

  SP_CONSUL_HOST("SP_CONSUL_HOST"),
  SP_CONSUL_PORT("SP_CONSUL_PORT"),
  SP_KAFKA_RETENTION_MS("SP_KAFKA_RETENTION_MS"),
  SP_JWT_SECRET("JWT_SECRET"),
  SP_JWT_SIGNING_MODE("SP_JWT_SIGNING_MODE"),
  SP_JWT_PRIVATE_KEY_LOC("SP_JWT_PRIVATE_KEY_LOC"),
  SP_JWT_PUBLIC_KEY_LOC("SP_JWT_PUBLIC_KEY_LOC"),
  SP_INITIAL_ADMIN_EMAIL("SP_INITIAL_ADMIN_EMAIL"),
  SP_INITIAL_ADMIN_PASSWORD("SP_INITIAL_ADMIN_PASSWORD"),
  SP_INITIAL_SERVICE_USER("SP_INITIAL_SERVICE_USER"),
  SP_INITIAL_SERVICE_USER_SECRET("SP_INITIAL_SERVICE_USER_SECRET"),
  SP_SETUP_INSTALL_PIPELINE_ELEMENTS("SP_SETUP_INSTALL_PIPELINE_ELEMENTS"),
  SP_EXT_AUTH_MODE("SP_EXT_AUTH_MODE"),
  SP_CLIENT_USER("SP_CLIENT_USER"),
  SP_CLIENT_SECRET("SP_CLIENT_SECRET"),
  SP_ENCRYPTION_PASSCODE("SP_ENCRYPTION_PASSCODE"),
  SP_DEBUG("SP_DEBUG"),
  SP_MAX_WAIT_TIME_AT_SHUTDOWN("SP_MAX_WAIT_TIME_AT_SHUTDOWN");

  private final String envVariableName;

  Envs(String envVariableName) {
    this.envVariableName = envVariableName;
  }

  public boolean exists() {
    return CustomEnvs.exists(this.envVariableName);
  }

  public String getValue() {
    return CustomEnvs.getEnv(this.envVariableName);
  }

  public Integer getValueAsInt() {
    return CustomEnvs.getEnvAsInt(this.envVariableName);
  }

  public Integer getValueAsIntOrDefault(int defaultValue) {
    return exists() ? getValueAsInt() : defaultValue;
  }

  public Boolean getValueAsBoolean() {
    return CustomEnvs.getEnvAsBoolean(this.envVariableName);
  }

  public boolean getValueAsBooleanOrDefault(boolean defaultValue) {
    return this.exists() ? this.getValueAsBoolean() : defaultValue;
  }

  public String getEnvVariableName() {
    return envVariableName;
  }

  public String getValueOrDefault(String defaultValue) {
    return this.exists() ? this.getValue() : defaultValue;
  }

}
