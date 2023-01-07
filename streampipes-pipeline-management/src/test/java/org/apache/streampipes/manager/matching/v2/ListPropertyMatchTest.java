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

package org.apache.streampipes.manager.matching.v2;

import org.apache.streampipes.model.client.matching.MatchingResultMessage;
import org.apache.streampipes.model.schema.EventPropertyList;

import org.junit.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class ListPropertyMatchTest {


  @Test
  public void matchSameDomainProperty() {
    List<URI> domainProperties = new ArrayList<>();
    domainProperties.add(URI.create("http://test.org/property"));

    EventPropertyList offer = new EventPropertyList();
    offer.setDomainProperties(domainProperties);
    EventPropertyList requirement = new EventPropertyList();
    requirement.setDomainProperties(domainProperties);

    List<MatchingResultMessage> errorLog = new ArrayList<>();

    boolean result = new ListPropertyMatch().match(offer, requirement, errorLog);

    assertTrue(result);
  }

  @Test
  public void matchListWithNoFurtherRequirements() {

    EventPropertyList offer = new EventPropertyList();
    EventPropertyList requirement = new EventPropertyList();

    List<MatchingResultMessage> errorLog = new ArrayList<>();

    boolean result = new ListPropertyMatch().match(offer, requirement, errorLog);

    assertTrue(result);
  }

}