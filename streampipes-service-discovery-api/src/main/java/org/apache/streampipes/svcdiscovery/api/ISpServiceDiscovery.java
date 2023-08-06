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
package org.apache.streampipes.svcdiscovery.api;

import java.util.List;

public interface ISpServiceDiscovery {

  /**
   * Get active pipeline element service endpoints
   *
   * @return list of pipeline element endpoints
   */
  List<String> getActivePipelineElementEndpoints();

  /**
   * Get service endpoints
   *
   * @param svcGroup          service group for registered service
   * @param restrictToHealthy retrieve healthy or all registered services for a service group
   * @param filterByTags      filter param to filter list of registered services
   * @return list of services
   */
  List<String> getServiceEndpoints(String svcGroup,
                                   boolean restrictToHealthy,
                                   List<String> filterByTags);
}
