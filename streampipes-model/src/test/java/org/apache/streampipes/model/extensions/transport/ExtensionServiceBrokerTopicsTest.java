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

package org.apache.streampipes.model.extensions.transport;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExtensionServiceBrokerTopicsTest {

  @Test
  void shouldEncodeAndDecodeRoundTripForSpecialCharacters() {
    String segment = "org.apache.streampipes/ä/~demo";

    String encoded = ExtensionServiceBrokerTopics.encodeTopicSegment(segment);
    String decoded = ExtensionServiceBrokerTopics.decodeTopicSegment(encoded);

    assertEquals(segment, decoded);
    assertFalse(encoded.contains("."));
    assertFalse(encoded.contains("/"));
  }

  @Test
  void shouldNotEncodeServiceIdButEncodeOperationSegmentsInServiceTopic() {
    String topic = ExtensionServiceBrokerTopics.serviceTopic(
        "/sp/extensions/request/",
        "org.apache.streampipes.demo",
        List.of("pipeline-assets", "org.apache.streampipes.element")
    );

    assertEquals(
        "sp.extensions.request.org.apache.streampipes.demo.pipeline-assets.org~2Eapache~2Estreampipes~2Eelement",
        topic
    );
  }

  @Test
  void shouldCreateWildcardWithPlainServiceId() {
    String wildcard = ExtensionServiceBrokerTopics.serviceWildcard(
        "sp/extensions/request",
        "org.apache.streampipes.demo"
    );

    assertEquals("sp.extensions.request.org.apache.streampipes.demo.>", wildcard);
  }

  @Test
  void shouldReturnOriginalValueForMalformedEscapeSequence() {
    String malformed = "app~2Gid";
    assertEquals(malformed, ExtensionServiceBrokerTopics.decodeTopicSegment(malformed));
  }

  @Test
  void shouldReturnOriginalValueForNonAsciiUnescapedInput() {
    String input = "ä";
    assertEquals(input, ExtensionServiceBrokerTopics.decodeTopicSegment(input));
  }

  @Test
  void shouldEncodeEscapeCharacterItself() {
    String value = "app~id";
    String encoded = ExtensionServiceBrokerTopics.encodeTopicSegment(value);

    assertTrue(encoded.contains("~7E"));
    assertEquals(value, ExtensionServiceBrokerTopics.decodeTopicSegment(encoded));
  }
}
