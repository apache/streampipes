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

import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;

import java.util.List;

/**
 * Interface for load balancing operations. Provides methods for service allocation and load
 * shedding.
 */
public interface LoadBalancer {

  /**
   * Allocate a service for pipeline processing.
   *
   * @param serviceRegistrations available service registrations
   * @param label labels for service selection
   * @return selected service registration
   */
  SpServiceRegistration allocation(List<SpServiceRegistration> serviceRegistrations,
                                   List<String> label);

  /**
   * Perform load shedding operations.
   */
  void doLoadShedding();
}
