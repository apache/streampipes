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
package org.apache.streampipes.svcdiscovery;

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.svcdiscovery.api.ISpKvManagement;
import org.apache.streampipes.svcdiscovery.api.ISpServiceDiscovery;
import org.apache.streampipes.svcdiscovery.api.SpConfig;
import org.apache.streampipes.svcdiscovery.consul.ConsulSpConfig;
import org.apache.streampipes.svcdiscovery.consul.SpConsulKvManagement;
import org.apache.streampipes.svcdiscovery.consul.SpConsulServiceDiscovery;

public class SpServiceDiscovery {

  public static ISpServiceDiscovery getServiceDiscovery() {
    return new SpConsulServiceDiscovery(Environments.getEnvironment());
  }

  public static ISpServiceDiscovery getServiceDiscovery(Environment environment) {
    return new SpConsulServiceDiscovery(environment);
  }

  public static ISpKvManagement getKeyValueStore() {
    return new SpConsulKvManagement(Environments.getEnvironment());
  }

  public static ISpKvManagement getKeyValueStore(Environment environment) {
    return new SpConsulKvManagement(environment);
  }

  public static SpConfig getSpConfig(String serviceGroup) {
    return getSpConfig(serviceGroup, Environments.getEnvironment());
  }

  public static SpConfig getSpConfig(String serviceGroup,
                                     Environment environment) {
    return new ConsulSpConfig(serviceGroup, environment);
  }

}
