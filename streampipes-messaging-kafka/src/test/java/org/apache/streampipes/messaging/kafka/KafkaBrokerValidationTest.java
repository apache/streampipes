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

package org.apache.streampipes.messaging.kafka;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.Node;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class KafkaBrokerValidationTest {
  @Test
  void checksClusterMetadataAndClosesProbeEvenWhenNotReady() {
    var client = mock(AdminClient.class);
    var result = mock(DescribeClusterResult.class);
    when(client.describeCluster()).thenReturn(result);
    var protocol = new KafkaTransportProtocol("broker", 9092, "topic");
    try (var factory = mockStatic(AdminClient.class)) {
      factory.when(() -> AdminClient.create(any(Properties.class))).thenReturn(client);
      when(result.nodes()).thenReturn(KafkaFuture.completedFuture(List.of(new Node(1, "broker", 9092))));
      new SpKafkaProtocol().validateConnection(protocol);
      when(result.nodes()).thenReturn(KafkaFuture.<Collection<Node>>completedFuture(List.of()));
      assertThrows(SpRuntimeException.class, () -> new SpKafkaProtocol().validateConnection(protocol));
      verify(client, times(2)).close(Duration.ofSeconds(1));
    }
  }
}
