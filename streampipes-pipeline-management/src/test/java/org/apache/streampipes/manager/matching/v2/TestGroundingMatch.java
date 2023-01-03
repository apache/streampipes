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
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.grounding.TransportFormat;
import org.apache.streampipes.model.grounding.TransportProtocol;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestGroundingMatch {

  @Test
  public void testPositiveGroundingMatch() {

    TransportProtocol offerProtocol = TestUtils.kafkaProtocol();
    TransportProtocol requirementProtocol = TestUtils.kafkaProtocol();

    TransportFormat offerFormat = TestUtils.jsonFormat();
    TransportFormat requirementFormat = TestUtils.jsonFormat();

    EventGrounding offeredGrounding = new EventGrounding(offerProtocol, offerFormat);
    EventGrounding requiredGrounding = new EventGrounding(requirementProtocol, requirementFormat);

    List<MatchingResultMessage> errorLog = new ArrayList<>();

    boolean matches = new GroundingMatch().match(offeredGrounding, requiredGrounding, errorLog);
    assertTrue(matches);
  }

  @Test
  public void testNegativeGroundingMatchProtocol() {

    TransportProtocol offerProtocol = TestUtils.kafkaProtocol();
    TransportProtocol requirementProtocol = TestUtils.jmsProtocol();

    TransportFormat offerFormat = TestUtils.jsonFormat();
    TransportFormat requirementFormat = TestUtils.jsonFormat();

    EventGrounding offeredGrounding = new EventGrounding(offerProtocol, offerFormat);
    EventGrounding requiredGrounding = new EventGrounding(requirementProtocol, requirementFormat);

    List<MatchingResultMessage> errorLog = new ArrayList<>();

    boolean matches = new GroundingMatch().match(offeredGrounding, requiredGrounding, errorLog);
    assertFalse(matches);
  }

  @Test
  public void testNegativeGroundingMatchFormat() {

    TransportProtocol offerProtocol = TestUtils.kafkaProtocol();
    TransportProtocol requirementProtocol = TestUtils.kafkaProtocol();

    TransportFormat offerFormat = TestUtils.jsonFormat();
    TransportFormat requirementFormat = TestUtils.thriftFormat();

    EventGrounding offeredGrounding = new EventGrounding(offerProtocol, offerFormat);
    EventGrounding requiredGrounding = new EventGrounding(requirementProtocol, requirementFormat);

    List<MatchingResultMessage> errorLog = new ArrayList<>();

    boolean matches = new GroundingMatch().match(offeredGrounding, requiredGrounding, errorLog);
    assertFalse(matches);
  }

  @Test
  public void testNegativeGroundingMatchBoth() {

    TransportProtocol offerProtocol = TestUtils.kafkaProtocol();
    TransportProtocol requirementProtocol = TestUtils.jmsProtocol();

    TransportFormat offerFormat = TestUtils.jsonFormat();
    TransportFormat requirementFormat = TestUtils.thriftFormat();

    EventGrounding offeredGrounding = new EventGrounding(offerProtocol, offerFormat);
    EventGrounding requiredGrounding = new EventGrounding(requirementProtocol, requirementFormat);

    List<MatchingResultMessage> errorLog = new ArrayList<>();

    boolean matches = new GroundingMatch().match(offeredGrounding, requiredGrounding, errorLog);
    assertFalse(matches);
  }

}
