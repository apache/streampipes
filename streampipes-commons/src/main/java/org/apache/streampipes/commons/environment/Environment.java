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

import org.apache.streampipes.commons.environment.variable.BooleanEnvironmentVariable;
import org.apache.streampipes.commons.environment.variable.IntEnvironmentVariable;
import org.apache.streampipes.commons.environment.variable.StringEnvironmentVariable;

public interface Environment {

  // Consul env variables
  StringEnvironmentVariable getConsulHost();

  IntEnvironmentVariable getConsulPort();

  @Deprecated(since = "0.90.0", forRemoval = true)
  StringEnvironmentVariable getConsulLocation();

  BooleanEnvironmentVariable getSpDebug();

  // Service base configuration

  StringEnvironmentVariable getServiceHost();

  IntEnvironmentVariable getServicePort();

  StringEnvironmentVariable getSpCoreScheme();
  StringEnvironmentVariable getSpCoreHost();
  IntEnvironmentVariable getSpCorePort();

  // Time series storage env variables

  StringEnvironmentVariable getTsStorageProtocol();

  StringEnvironmentVariable getTsStorageHost();

  IntEnvironmentVariable getTsStoragePort();

  StringEnvironmentVariable getTsStorageToken();

  StringEnvironmentVariable getTsStorageOrg();

  StringEnvironmentVariable getTsStorageBucket();

  // CouchDB env variables

  StringEnvironmentVariable getCouchDbProtocol();

  StringEnvironmentVariable getCouchDbHost();

  IntEnvironmentVariable getCouchDbPort();

  StringEnvironmentVariable getCouchDbUsername();

  StringEnvironmentVariable getCouchDbPassword();


  // JWT & Authentication

  StringEnvironmentVariable getClientUser();

  StringEnvironmentVariable getClientSecret();

  StringEnvironmentVariable getJwtSecret();

  StringEnvironmentVariable getJwtPublicKeyLoc();

  StringEnvironmentVariable getJwtPrivateKeyLoc();

  StringEnvironmentVariable getJwtSigningMode();

  StringEnvironmentVariable getExtensionsAuthMode();

  StringEnvironmentVariable getEncryptionPasscode();

  // Messaging
  StringEnvironmentVariable getKafkaRetentionTimeMs();

  StringEnvironmentVariable getPrioritizedProtocol();


  // Setup
  BooleanEnvironmentVariable getSetupInstallPipelineElements();

  StringEnvironmentVariable getInitialServiceUserSecret();

  StringEnvironmentVariable getInitialServiceUser();

  StringEnvironmentVariable getInitialAdminEmail();

  StringEnvironmentVariable getInitialAdminPassword();

  StringEnvironmentVariable getCoreAssetBaseDir();

  // Flink Wrapper
  StringEnvironmentVariable getFlinkJarFileLoc();

  StringEnvironmentVariable getFlinkJobmanagerHost();

  IntEnvironmentVariable getFlinkJobmanagerPort();

  //prometheus
  StringEnvironmentVariable getPrometheusEndpointInclude();

  BooleanEnvironmentVariable getSetupPrometheusEndpoint();
}
