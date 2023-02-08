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

package org.apache.streampipes.integration.svcdiscovery;

import org.apache.streampipes.svcdiscovery.SpServiceDiscovery;
import org.apache.streampipes.svcdiscovery.api.ISpServiceDiscovery;
import org.apache.streampipes.svcdiscovery.api.model.DefaultSpServiceGroups;
import org.apache.streampipes.svcdiscovery.api.model.DefaultSpServiceTags;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceRegistrationRequest;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceTag;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceTagPrefix;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ConsulSpServiceDiscoveryTest extends AbstractConsulTest {

  private ISpServiceDiscovery serviceDiscovery;

  @Before
  public void before() {
    var env = mockEnvironment();
    this.serviceDiscovery = SpServiceDiscovery
        .getServiceDiscovery(env);
  }

  @Test
  public void testServiceRegistration() {

    serviceDiscovery.registerService(makeRequest(makeServiceTag(), "abc"));

    var endpoints = getEndpoints(false);
    assertEquals(1, endpoints.size());

    serviceDiscovery.deregisterService("abc");

    endpoints = getEndpoints(false);
    assertEquals(0, endpoints.size());
  }

  @Test
  public void testGetExtensionsServiceGroups() {
    serviceDiscovery.registerService(makeRequest(List.of(DefaultSpServiceTags.PE, groupTag()), "abc"));
    serviceDiscovery.registerService(makeRequest(
        List.of(DefaultSpServiceTags.CORE, groupTag()), "def"));

    var serviceGroups = serviceDiscovery.getExtensionsServiceGroups();

    assertEquals(1, serviceGroups.size());
    assertTrue(serviceGroups.containsKey("my-group"));

    serviceDiscovery.deregisterService("abc");
    serviceDiscovery.deregisterService("def");
  }

  private List<String> getEndpoints(boolean healthy) {
    return serviceDiscovery.getServiceEndpoints(
        DefaultSpServiceGroups.EXT,
        healthy,
        List.of(makeServiceTag().get(0).asString()));
  }

  private SpServiceRegistrationRequest makeRequest(List<SpServiceTag> serviceTags,
                                                   String serviceId) {
    var req = new SpServiceRegistrationRequest(
        DefaultSpServiceGroups.EXT,
        serviceId,
        "localhost",
        80,
        serviceTags,
        "/"
    );

    return req;
  }

  private SpServiceTag groupTag() {
    return SpServiceTag.create(SpServiceTagPrefix.SP_GROUP, "my-group");
  }

  private List<SpServiceTag> makeServiceTag() {
    return List.of(DefaultSpServiceTags.PE);
  }
}
