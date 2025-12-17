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
package org.apache.streampipes.loadbalance.service;

import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.loadbalancer.ServiceLoadDataReport;
import org.apache.streampipes.serializers.json.JacksonSerializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExtensionsServiceReportExecutor {

  private static final Logger LOG = LoggerFactory.getLogger(ExtensionsServiceReportExecutor.class);

  private static final String LOG_PATH = "/serviceMonitor";

  private static final Map<String, ServiceLoadDataReport> map = new ConcurrentHashMap<>();

  /**
   * Get service load data report for a service registration.
   *
   * @param serviceRegistration Service registration to get load data for
   * @return Service load data report
   */
  public static ServiceLoadDataReport getServiceLoadDataReport(SpServiceRegistration serviceRegistration) {
    try {
      String response =
          makeRequest(serviceRegistration.getServiceUrl()).execute().returnContent().asString();
      return parseLogResponse(response);
    } catch (IOException e) {
      LOG.info("Could not fetch info from endpoint {}", serviceRegistration.getServiceUrl());
    }
    return new ServiceLoadDataReport();
  }

  /**
   * Create HTTP request for service endpoint.
   *
   * @param serviceEndpointUrl Service endpoint URL
   * @return HTTP request
   */
  private static Request makeRequest(String serviceEndpointUrl) {
    return ExtensionServiceExecutions.extServiceGetRequest(makeLogUrl(serviceEndpointUrl));
  }

  /**
   * Create log URL by appending log path to base URL.
   *
   * @param baseUrl Base service URL
   * @return Complete log URL
   */
  private static String makeLogUrl(String baseUrl) {
    return baseUrl + LOG_PATH;
  }

  /**
   * Parse log response JSON into ServiceLoadDataReport.
   *
   * @param response JSON response string
   * @return Parsed service load data report
   * @throws JsonProcessingException If JSON parsing fails
   */
  private static ServiceLoadDataReport parseLogResponse(String response)
      throws JsonProcessingException {
    return JacksonSerializer.getObjectMapper().readValue(response, ServiceLoadDataReport.class);
  }
}
