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
import org.apache.streampipes.model.grounding.TransportProtocol;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestProtocolMatch {

  @Test
  public void testPositiveProtocolMatch() {

    TransportProtocol offer = TestUtils.kafkaProtocol();
    TransportProtocol requirement = TestUtils.kafkaProtocol();

    List<MatchingResultMessage> errorLog = new ArrayList<>();

    boolean matches = new ProtocolMatch().match(offer, requirement, errorLog);
    assertTrue(matches);
  }

  @Test
  public void testNegativeProtocolMatch() {

    TransportProtocol offer = TestUtils.kafkaProtocol();
    TransportProtocol requirement = TestUtils.jmsProtocol();

    List<MatchingResultMessage> errorLog = new ArrayList<>();

    boolean matches = new ProtocolMatch().match(offer, requirement, errorLog);
    assertFalse(matches);
  }
}
