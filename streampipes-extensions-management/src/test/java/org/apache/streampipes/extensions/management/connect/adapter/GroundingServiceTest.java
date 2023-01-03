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

package org.apache.streampipes.extensions.management.connect.adapter;

import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.GenericAdapterSetDescription;
import org.apache.streampipes.model.connect.adapter.GenericAdapterStreamDescription;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.SimpleTopicDefinition;
import org.apache.streampipes.model.grounding.TopicDefinition;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GroundingServiceTest {

  @Test
  public void extractTopicForGenericAdapterSetTest() {
    AdapterDescription adapterDescription = getGenericAdapterSetDescription();

    String result = GroundingService.extractTopic(adapterDescription);

    assertEquals("test.topic", result);
  }

  @Test
  public void extractTopicForStreamTest() {
    AdapterDescription adapterDescription = getAdapterStreamDescription();

    String result = GroundingService.extractTopic(adapterDescription);

    assertEquals("test.topic", result);
  }

  private AdapterDescription getAdapterStreamDescription() {
    AdapterDescription adapterDescription = new GenericAdapterStreamDescription();

    adapterDescription.setEventGrounding(getEventGrounding());

    return adapterDescription;
  }

  private AdapterDescription getGenericAdapterSetDescription() {
    GenericAdapterSetDescription adapterDescription = new GenericAdapterSetDescription();
    SpDataSet set = new SpDataSet();
    adapterDescription.setDataSet(set);

    set.setEventGrounding(getEventGrounding());
    return adapterDescription;
  }

  private EventGrounding getEventGrounding() {
    EventGrounding eventGrounding = new EventGrounding();
    KafkaTransportProtocol transportProtocol = new KafkaTransportProtocol();
    transportProtocol.setBrokerHostname("localhost");
    transportProtocol.setKafkaPort(1111);
    eventGrounding.setTransportProtocol(transportProtocol);

    TopicDefinition topicDefinition = new SimpleTopicDefinition("test.topic");
    transportProtocol.setTopicDefinition(topicDefinition);

    return eventGrounding;
  }


}
