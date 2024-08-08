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

package org.apache.streampipes.commons.environment;

import org.apache.streampipes.commons.constants.Envs;
import org.apache.streampipes.commons.environment.model.OAuthConfiguration;
import org.apache.streampipes.commons.environment.parser.OAuthConfigurationParser;
import org.apache.streampipes.commons.environment.variable.BooleanEnvironmentVariable;
import org.apache.streampipes.commons.environment.variable.IntEnvironmentVariable;
import org.apache.streampipes.commons.environment.variable.StringEnvironmentVariable;

import java.util.List;

public class DefaultEnvironment implements Environment {

  @Override
  public BooleanEnvironmentVariable getSpDebug() {
    return new BooleanEnvironmentVariable(Envs.SP_DEBUG);
  }

  @Override
  public StringEnvironmentVariable getServiceHost() {
    return new StringEnvironmentVariable(Envs.SP_HOST);
  }

  @Override
  public IntEnvironmentVariable getServicePort() {
    return new IntEnvironmentVariable(Envs.SP_PORT);
  }

  @Override
  public StringEnvironmentVariable getSpCoreScheme() {
    return new StringEnvironmentVariable(Envs.SP_CORE_SCHEME);
  }

  @Override
  public StringEnvironmentVariable getSpCoreHost() {
    return new StringEnvironmentVariable(Envs.SP_CORE_HOST);
  }

  @Override
  public IntEnvironmentVariable getSpCorePort() {
    return new IntEnvironmentVariable(Envs.SP_CORE_PORT);
  }

  @Override
  public StringEnvironmentVariable getTsStorage() {
    return new StringEnvironmentVariable(Envs.SP_TS_STORAGE);
  }

  @Override
  public StringEnvironmentVariable getTsStorageProtocol() {
    return new StringEnvironmentVariable(Envs.SP_TS_STORAGE_PROTOCOL);
  }

  @Override
  public StringEnvironmentVariable getTsStorageHost() {
    return new StringEnvironmentVariable(Envs.SP_TS_STORAGE_HOST);
  }

  @Override
  public IntEnvironmentVariable getTsStoragePort() {
    return new IntEnvironmentVariable(Envs.SP_TS_STORAGE_PORT);
  }

  @Override
  public StringEnvironmentVariable getTsStorageToken() {
    return new StringEnvironmentVariable(Envs.SP_TS_STORAGE_TOKEN);
  }

  @Override
  public StringEnvironmentVariable getTsStorageOrg() {
    return new StringEnvironmentVariable(Envs.SP_TS_STORAGE_ORG);
  }

  @Override
  public StringEnvironmentVariable getTsStorageBucket() {
    return new StringEnvironmentVariable(Envs.SP_TS_STORAGE_BUCKET);
  }

  @Override
  public IntEnvironmentVariable getIotDbSessionPoolSize(){
    return new IntEnvironmentVariable(Envs.SP_TS_STORAGE_IOT_DB_SESSION_POOL_SIZE);
  }

  @Override
  public BooleanEnvironmentVariable getIotDbSessionEnableCompression(){
    return new BooleanEnvironmentVariable(Envs.SP_TS_STORAGE_IOT_DB_SESSION_POOL_ENABLE_COMPRESSION);
  }

  @Override
  public StringEnvironmentVariable getIotDbUser(){
    return new StringEnvironmentVariable(Envs.SP_TS_STORAGE_IOT_DB_USER);
  }

  @Override
  public StringEnvironmentVariable getIotDbPassword(){
    return new StringEnvironmentVariable(Envs.SP_TS_STORAGE_IOT_DB_PASSWORD);
  }

  @Override
  public StringEnvironmentVariable getCouchDbProtocol() {
    return new StringEnvironmentVariable(Envs.SP_COUCHDB_PROTOCOL);
  }
  @Override
  public StringEnvironmentVariable getCouchDbHost() {
    return new StringEnvironmentVariable(Envs.SP_COUCHDB_HOST);
  }

  @Override
  public IntEnvironmentVariable getCouchDbPort() {
    return new IntEnvironmentVariable(Envs.SP_COUCHDB_PORT);
  }

  @Override
  public StringEnvironmentVariable getCouchDbUsername() {
    return new StringEnvironmentVariable(Envs.SP_COUCHDB_USER);
  }

  @Override
  public StringEnvironmentVariable getCouchDbPassword() {
    return new StringEnvironmentVariable(Envs.SP_COUCHDB_PASSWORD);
  }

  @Override
  public StringEnvironmentVariable getClientUser() {
    return new StringEnvironmentVariable(Envs.SP_CLIENT_USER);
  }

  @Override
  public StringEnvironmentVariable getClientSecret() {
    return new StringEnvironmentVariable(Envs.SP_CLIENT_SECRET);
  }

  @Override
  public StringEnvironmentVariable getJwtSecret() {
    return new StringEnvironmentVariable(Envs.SP_JWT_SECRET);
  }

  @Override
  public StringEnvironmentVariable getJwtPublicKeyLoc() {
    return new StringEnvironmentVariable(Envs.SP_JWT_PUBLIC_KEY_LOC);
  }

  @Override
  public StringEnvironmentVariable getJwtPrivateKeyLoc() {
    return new StringEnvironmentVariable(Envs.SP_JWT_PRIVATE_KEY_LOC);
  }

  @Override
  public StringEnvironmentVariable getJwtSigningMode() {
    return new StringEnvironmentVariable(Envs.SP_JWT_SIGNING_MODE);
  }

  @Override
  public StringEnvironmentVariable getExtensionsAuthMode() {
    return new StringEnvironmentVariable(Envs.SP_EXT_AUTH_MODE);
  }

  @Override
  public StringEnvironmentVariable getEncryptionPasscode() {
    return new StringEnvironmentVariable(Envs.SP_ENCRYPTION_PASSCODE);
  }

  @Override
  public BooleanEnvironmentVariable getOAuthEnabled() {
    return new BooleanEnvironmentVariable(Envs.SP_OAUTH_ENABLED);
  }

  @Override
  public StringEnvironmentVariable getOAuthRedirectUri() {
    return new StringEnvironmentVariable(Envs.SP_OAUTH_REDIRECT_URI);
  }

  @Override
  public List<OAuthConfiguration> getOAuthConfigurations() {
    return new OAuthConfigurationParser().parse(System.getenv());
  }

  @Override
  public StringEnvironmentVariable getKafkaRetentionTimeMs() {
    return new StringEnvironmentVariable(Envs.SP_KAFKA_RETENTION_MS);
  }

  @Override
  public StringEnvironmentVariable getPrioritizedProtocol() {
    return new StringEnvironmentVariable(Envs.SP_PRIORITIZED_PROTOCOL);
  }

  @Override
  public BooleanEnvironmentVariable getSetupInstallPipelineElements() {
    return new BooleanEnvironmentVariable(Envs.SP_SETUP_INSTALL_PIPELINE_ELEMENTS);
  }

  @Override
  public StringEnvironmentVariable getInitialServiceUserSecret() {
    return new StringEnvironmentVariable(Envs.SP_INITIAL_SERVICE_USER_SECRET);
  }

  @Override
  public StringEnvironmentVariable getInitialServiceUser() {
    return new StringEnvironmentVariable(Envs.SP_INITIAL_SERVICE_USER);
  }

  @Override
  public StringEnvironmentVariable getInitialAdminEmail() {
    return new StringEnvironmentVariable(Envs.SP_INITIAL_ADMIN_EMAIL);
  }

  @Override
  public StringEnvironmentVariable getInitialAdminPassword() {
    return new StringEnvironmentVariable(Envs.SP_INITIAL_ADMIN_PASSWORD);
  }

  @Override
  public StringEnvironmentVariable getCoreAssetBaseDir() {
    return new StringEnvironmentVariable(Envs.SP_CORE_ASSET_BASE_DIR);
  }

  @Override
  public StringEnvironmentVariable getFlinkJarFileLoc() {
    return new StringEnvironmentVariable(Envs.SP_FLINK_JAR_FILE_LOC);
  }

  @Override
  public StringEnvironmentVariable getFlinkJobmanagerHost() {
    return new StringEnvironmentVariable(Envs.SP_FLINK_JOBMANAGER_HOST);
  }

  @Override
  public IntEnvironmentVariable getFlinkJobmanagerPort() {
    return new IntEnvironmentVariable(Envs.SP_FLINK_JOBMANAGER_PORT);
  }

  @Override
  public StringEnvironmentVariable getPrometheusEndpointInclude() {
    return new StringEnvironmentVariable(Envs.SP_PROMETHEUS_ENDPOINT_INCLUDE);
  }

  @Override
  public BooleanEnvironmentVariable getSetupPrometheusEndpoint() {
    return new BooleanEnvironmentVariable(Envs.SP_SETUP_PROMETHEUS_ENDPOINT);
  }

  @Override
  public IntEnvironmentVariable getHealthCheckIntervalInMillis() {
    return new IntEnvironmentVariable(Envs.SP_HEALTH_CHECK_INTERVAL_MS);
  }

  @Override
  public IntEnvironmentVariable getInitialHealthCheckDelayInMillis() {
    return new IntEnvironmentVariable(Envs.SP_HEALTH_CHECK_INITIAL_DELAY_MS);
  }

  @Override
  public IntEnvironmentVariable getLogFetchIntervalInMillis() {
    return new IntEnvironmentVariable(Envs.SP_LOG_FETCH_INTERVAL_MS);
  }

  @Override
  public IntEnvironmentVariable getUnhealthyTimeBeforeServiceDeletionInMillis() {
    return new IntEnvironmentVariable(Envs.SP_HEALTH_SERVICE_MAX_UNHEALTHY_TIME_MS);
  }

  @Override
  public IntEnvironmentVariable getInitialWaitTimeBeforeInstallationInMillis() {
    return new IntEnvironmentVariable(Envs.SP_INITIAL_WAIT_BEFORE_INSTALLATION_MS);
  }

  @Override
  public StringEnvironmentVariable getKafkaHost() {
    return new StringEnvironmentVariable(Envs.SP_KAFKA_HOST);
  }

  @Override
  public IntEnvironmentVariable getKafkaPort() {
    return new IntEnvironmentVariable(Envs.SP_KAFKA_PORT);
  }

  @Override
  public StringEnvironmentVariable getMqttHost() {
    return new StringEnvironmentVariable(Envs.SP_MQTT_HOST);
  }

  @Override
  public IntEnvironmentVariable getMqttPort() {
    return new IntEnvironmentVariable(Envs.SP_MQTT_PORT);
  }

  @Override
  public StringEnvironmentVariable getNatsHost() {
    return new StringEnvironmentVariable(Envs.SP_NATS_HOST);
  }

  @Override
  public IntEnvironmentVariable getNatsPort() {
    return new IntEnvironmentVariable(Envs.SP_NATS_PORT);
  }

  @Override
  public StringEnvironmentVariable getPulsarUrl() {
    return new StringEnvironmentVariable(Envs.SP_PULSAR_URL);
  }

  @Override
  public StringEnvironmentVariable getCustomServiceTags() {
    return new StringEnvironmentVariable(Envs.SP_SERVICE_TAGS);
  }

}
