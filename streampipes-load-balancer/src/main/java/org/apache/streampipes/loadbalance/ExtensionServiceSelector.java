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
package org.apache.streampipes.loadbalance;

import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;

import java.util.List;
import java.util.Map;

/**
 * Service selector interface for load balancing. Provides methods to allocate pipeline elements and
 * adapters to services.
 */
public interface ExtensionServiceSelector {

  /**
   * Select a single service for an element.
   *
   * @param availableServices List of available services
   * @param labels Labels for service selection
   * @return Selected service registration
   */
  SpServiceRegistration select(List<SpServiceRegistration> availableServices, List<String> labels);

  /**
   * Allocate sinks and processors to services.
   *
   * @param sinksAndProcessors List of sinks and processors to allocate
   * @param availableServices List of available services
   * @return Map of service registration to list of allocated elements
   */
  Map<SpServiceRegistration, List<InvocableStreamPipesEntity>> allocateSinksAndProcessors(List<InvocableStreamPipesEntity> sinksAndProcessors,
                                                                                          List<SpServiceRegistration> availableServices);

  /**
   * Allocate adapters to services.
   *
   * @param adapters List of adapters to allocate
   * @param availableServices List of available services
   * @return Map of service registration to list of allocated adapters
   */
  Map<SpServiceRegistration, List<AdapterDescription>> allocateAdapters(List<AdapterDescription> adapters,
                                                                        List<SpServiceRegistration> availableServices);
}
