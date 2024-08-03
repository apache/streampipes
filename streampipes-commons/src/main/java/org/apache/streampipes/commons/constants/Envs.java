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

  SP_CORE_ASSET_BASE_DIR("SP_CORE_ASSET_BASE_DIR"),

  SP_CORE_SCHEME("SP_CORE_SCHEME", "http", "http"),
  SP_CORE_HOST("SP_CORE_HOST", "backend", "localhost"),
  SP_CORE_PORT("SP_CORE_PORT", "8030", "8030"),
  SP_KAFKA_RETENTION_MS("SP_KAFKA_RETENTION_MS", DefaultEnvValues.SP_KAFKA_RETENTION_MS_DEFAULT),
  SP_PRIORITIZED_PROTOCOL("SP_PRIORITIZED_PROTOCOL", "kafka"),
  SP_JWT_SECRET("SP_JWT_SECRET"),
  SP_JWT_SIGNING_MODE("SP_JWT_SIGNING_MODE"),
  SP_JWT_PRIVATE_KEY_LOC("SP_JWT_PRIVATE_KEY_LOC"),
  SP_JWT_PUBLIC_KEY_LOC("SP_JWT_PUBLIC_KEY_LOC"),
  SP_INITIAL_ADMIN_EMAIL("SP_INITIAL_ADMIN_EMAIL", DefaultEnvValues.INITIAL_ADMIN_EMAIL_DEFAULT),
  SP_INITIAL_ADMIN_PASSWORD("SP_INITIAL_ADMIN_PASSWORD", DefaultEnvValues.INITIAL_ADMIN_PW_DEFAULT),
  SP_INITIAL_SERVICE_USER("SP_INITIAL_SERVICE_USER", DefaultEnvValues.INITIAL_CLIENT_USER_DEFAULT),
  SP_INITIAL_SERVICE_USER_SECRET("SP_INITIAL_SERVICE_USER_SECRET", DefaultEnvValues.INITIAL_CLIENT_SECRET_DEFAULT),
  SP_SETUP_INSTALL_PIPELINE_ELEMENTS("SP_SETUP_INSTALL_PIPELINE_ELEMENTS", DefaultEnvValues.INSTALL_PIPELINE_ELEMENTS),
  SP_EXT_AUTH_MODE("SP_EXT_AUTH_MODE"),
  SP_CLIENT_USER("SP_CLIENT_USER", DefaultEnvValues.INITIAL_CLIENT_USER_DEFAULT),
  SP_CLIENT_SECRET("SP_CLIENT_SECRET", DefaultEnvValues.INITIAL_CLIENT_SECRET_DEFAULT),
  SP_ENCRYPTION_PASSCODE("SP_ENCRYPTION_PASSCODE", DefaultEnvValues.DEFAULT_ENCRYPTION_PASSCODE),
  SP_OAUTH_ENABLED("SP_OAUTH_ENABLED", "false"),
  SP_OAUTH_REDIRECT_URI("SP_OAUTH_REDIRECT_URI"),
  SP_DEBUG("SP_DEBUG", "false"),
  SP_MAX_WAIT_TIME_AT_SHUTDOWN("SP_MAX_WAIT_TIME_AT_SHUTDOWN"),

  // CouchDB Storage
  SP_COUCHDB_PROTOCOL("SP_COUCHDB_PROTOCOL", "http"),
  SP_COUCHDB_HOST("SP_COUCHDB_HOST", "couchdb", DefaultEnvValues.LOCALHOST),
  SP_COUCHDB_PORT("SP_COUCHDB_PORT", "5984"),
  SP_COUCHDB_USER("SP_COUCHDB_USER", "admin"),
  SP_COUCHDB_PASSWORD("SP_COUCHDB_PASSWORD", "admin"),


  // Time Series Storage
  SP_TS_STORAGE("SP_TS_STORAGE", "influxdb"),
  SP_TS_STORAGE_PROTOCOL("SP_TS_STORAGE_PROTOCOL", "http"),
  SP_TS_STORAGE_HOST("SP_TS_STORAGE_HOST", "influxdb", DefaultEnvValues.LOCALHOST),
  SP_TS_STORAGE_PORT("SP_TS_STORAGE_PORT", "8086"),

  SP_TS_STORAGE_TOKEN("SP_TS_STORAGE_TOKEN", "sp-admin"),

  SP_TS_STORAGE_ORG("SP_TS_STORAGE_ORG", "sp"),

  SP_TS_STORAGE_BUCKET("SP_TS_STORAGE_BUCKET", "sp"),
  SP_TS_STORAGE_IOT_DB_SESSION_POOL_SIZE("SP_TS_STORAGE_IOT_DB_SESSION_POOL_SIZE", "10"),
  SP_TS_STORAGE_IOT_DB_SESSION_POOL_ENABLE_COMPRESSION("SP_TS_STORAGE_IOT_DB_SESSION_POOL_ENABLE_COMPRESSION", "false"),
  SP_TS_STORAGE_IOT_DB_USER("SP_TS_STORAGE_IOT_DB_USER", "root"),
  SP_TS_STORAGE_IOT_DB_PASSWORD("SP_TS_STORAGE_IOT_DB_PASSWORD", "root"),

  SP_FLINK_JAR_FILE_LOC(
      "SP_FLINK_JAR_FILE_LOC",
      "./streampipes-processing-element-container.jar"),

  SP_FLINK_JOBMANAGER_HOST("SP_FLINK_JOBMANAGER_HOST", "jobmanager"),

  SP_FLINK_JOBMANAGER_PORT("SP_FLINK_JOBMANAGER_PORT", "8081"),

  SP_PROMETHEUS_ENDPOINT_INCLUDE("SP_PROMETHEUS_ENDPOINT_INCLUDE", "health,prometheus"),

  SP_SETUP_PROMETHEUS_ENDPOINT("SP_SETUP_PROMETHEUS_ENDPOINT", "false"),

  SP_HEALTH_CHECK_INTERVAL_MS("SP_HEALTH_CHECK_INTERVAL_MS", "30000"),

  SP_HEALTH_CHECK_INITIAL_DELAY_MS("SP_HEALTH_CHECK_INITIAL_DELAY", "10000"),

  SP_LOG_FETCH_INTERVAL_MS("SP_LOG_FETCH_INTERVAL_MS", "60000"),

  SP_HEALTH_SERVICE_MAX_UNHEALTHY_TIME_MS("SP_HEALTH_SERVICE_MAX_UNHEALTHY_TIME_MS", "60000"),

  SP_INITIAL_WAIT_BEFORE_INSTALLATION_MS("SP_INITIAL_WAIT_BEFORE_INSTALLATION_MS", "5000"),

  // Broker defaults

  SP_KAFKA_HOST("SP_KAFKA_HOST", "kafka"),
  SP_KAFKA_PORT("SP_KAFKA_PORT", "9092"),

  SP_MQTT_HOST("SP_MQTT_HOST", "mosquitto"),
  SP_MQTT_PORT("SP_MQTT_PORT", "1883"),

  SP_NATS_HOST("SP_NATS_HOST", "nats"),
  SP_NATS_PORT("SP_NATS_PORT", "4222"),

  SP_PULSAR_URL("SP_PULSAR_URL", "pulsar://localhost:6650"),

  // expects a comma separated string of service names
  SP_SERVICE_TAGS("SP_SERVICE_TAGS", "");

  private final String envVariableName;
  private String defaultValue;

  private String devDefaultValue;

  Envs(String envVariableName, String defaultValue, String devDefaultValue) {
    this(envVariableName, defaultValue);
    this.devDefaultValue = devDefaultValue;
  }

  Envs(String envVariableName, String defaultValue) {
    this(envVariableName);
    this.defaultValue = defaultValue;
    this.devDefaultValue = defaultValue;
  }

  Envs(String envVariableName) {
    this.envVariableName = envVariableName;
  }

  public String getEnvVariableName() {
    return envVariableName;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public String getDevDefaultValue() {
    return devDefaultValue;
  }
}
