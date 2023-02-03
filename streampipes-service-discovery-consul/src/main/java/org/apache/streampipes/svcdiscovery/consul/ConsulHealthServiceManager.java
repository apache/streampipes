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
package org.apache.streampipes.svcdiscovery.consul;

import org.apache.streampipes.commons.environment.Environment;

import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.health.HealthServicesRequest;
import com.ecwid.consul.v1.health.model.Check;
import com.ecwid.consul.v1.health.model.HealthService;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ConsulHealthServiceManager extends AbstractConsulService {

  private static final int MAX_RETRIES = 3;

  public ConsulHealthServiceManager(Environment environment) {
    super(environment);
    initializeCache();
  }

  public void initializeCache() {

  }

  public List<String> getServiceEndpoints(String serviceGroup,
                                          boolean restrictToHealthy,
                                          List<String> filterByTags) {
    List<HealthService> activeServices = findService(serviceGroup, 0);

    return activeServices
        .stream()
        .filter(service -> allFiltersSupported(service, filterByTags))
        .filter(service -> !restrictToHealthy
            || service.getChecks().stream().allMatch(check -> check.getStatus() == Check.CheckStatus.PASSING))
        .map(this::makeServiceUrl)
        .collect(Collectors.toList());
  }

  private String makeServiceUrl(HealthService service) {
    return service.getService().getAddress() + ":" + service.getService().getPort();
  }

  private boolean allFiltersSupported(HealthService service,
                                      List<String> filterByTags) {
    return new HashSet<>(service.getService().getTags()).containsAll(filterByTags);
  }

  private List<HealthService> findService(String serviceGroup, int retryCount) {
    HealthServicesRequest request = HealthServicesRequest.newBuilder()
        .setPassing(true)
        .setQueryParams(QueryParams.DEFAULT)
        .build();
    Response<List<HealthService>> healthyServicesResp = consulInstance().getHealthServices(serviceGroup, request);
    var healthyServices = healthyServicesResp.getValue();
    if (healthyServices.size() == 0) {
      if (retryCount < MAX_RETRIES) {
        try {
          retryCount++;
          TimeUnit.SECONDS.sleep(1);
          return findService(serviceGroup, retryCount);
        } catch (InterruptedException e) {
          e.printStackTrace();
          return Collections.emptyList();
        }
      } else {
        return Collections.emptyList();
      }
    } else {
      return healthyServices;
    }
  }


}
