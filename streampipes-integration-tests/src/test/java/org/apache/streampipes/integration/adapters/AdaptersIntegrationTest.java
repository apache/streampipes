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
package org.apache.streampipes.integration.adapters;


import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AdaptersIntegrationTest {


  @Test
  @Order(1)
  public void testMqttAdapter() throws Exception {
    try (MqttAdapterTester mqttAdapterTester = new MqttAdapterTester()) {
      mqttAdapterTester.run();
    }
  }

    @Test
      @Order(2)
  public void testMqttTLSAdapter() throws Exception {

try (MqttAdapterTLSTester mqttAdapterTLSTester = new MqttAdapterTLSTester()) {
      mqttAdapterTLSTester.run();
    }
  }
  
  @Test
  @Order(3)
  public void testPulsarAdapter() throws Exception {
    try (PulsarAdapterTester pulsarAdapterTester = new PulsarAdapterTester()) {
      pulsarAdapterTester.run();
    }
  }





  @Test
  @Order(4)
  public void testKafkaAdapter() throws Exception {

    try (KafkaAdapterTester kafkaAdapterTester = new KafkaAdapterTester()) {
      kafkaAdapterTester.run();
    }

   
  }
}
