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

package org.apache.streampipes.manager.loadbalance;

import org.apache.streampipes.model.loadbalancer.LoadBalanceResourceUnitStats;
import org.apache.streampipes.model.loadbalancer.ServiceLoadDataReport;

import java.util.List;
import java.util.Map;

/**
 * Container for load balancing data including service usage and resource unit statistics.
 */
public class LoadData {

  private final Map<String, ServiceLoadDataReport> serviceLoadData;
  private final Map<String, List<LoadBalanceResourceUnitStats>> resourceUnitStats;

  /**
   * Constructor.
   *
   * @param serviceLoadData Service load data map
   * @param resourceUnitStats Resource unit statistics map
   */
  public LoadData(Map<String, ServiceLoadDataReport> serviceLoadData,
                  Map<String, List<LoadBalanceResourceUnitStats>> resourceUnitStats) {
    this.serviceLoadData = serviceLoadData;
    this.resourceUnitStats = resourceUnitStats;
  }

  /**
   * Get service usage data by service ID.
   *
   * @param serviceId Service ID
   * @return Service load data report
   */
  public ServiceLoadDataReport getServiceUsage(String serviceId) {
    return serviceLoadData.get(serviceId);
  }

  /**
   * Get resource unit statistics by service ID.
   *
   * @param serviceId Service ID
   * @return List of resource unit statistics
   */
  public List<LoadBalanceResourceUnitStats> getResourceUnitStats(String serviceId) {
    return resourceUnitStats.get(serviceId);
  }
}
