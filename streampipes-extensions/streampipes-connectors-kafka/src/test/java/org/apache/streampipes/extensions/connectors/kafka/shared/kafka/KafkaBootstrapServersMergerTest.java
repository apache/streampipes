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

package org.apache.streampipes.extensions.connectors.kafka.shared.kafka;

import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableOneOfStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the implementation of the {@link KafkaBootstrapServersMerger} class.
 */
class KafkaBootstrapServersMergerTest {

  private static final String OLD_HOST_KEY = "host";
  private static final String OLD_PORT_KEY = "port";
  private static final String TOPIC_KEY = "topic";
  private static final String BOOTSTRAP_SERVERS_KEY = "bootstrap-servers";

  @Test
  void testMerge_hostAndPort_returnsTrue() {
    var staticProperties = storedConfigurations("broker1", "9094");
    var actual = KafkaBootstrapServersMerger.merge(staticProperties);
    assertTrue(actual);
  }

  @Test
  void testMerge_hostAndPort_joinsThemIntoOneBroker() {
    var staticProperties = storedConfigurations("broker1", "9094");
    var expected = "broker1:9094";

    KafkaBootstrapServersMerger.merge(staticProperties);
    var actual = bootstrapServersOf(staticProperties);

    assertEquals(expected, actual);
  }

  @Test
  void testMerge_emptyPort_usesTheDefaultPort() {
    var staticProperties = storedConfigurations("broker1", "");
    var expected = "broker1:9092";

    KafkaBootstrapServersMerger.merge(staticProperties);
    var actual = bootstrapServersOf(staticProperties);

    assertEquals(expected, actual);
  }

  @Test
  void testMerge_hostAndPort_putsTheBrokerWhereTheHostWas() {
    var staticProperties = storedConfigurations("broker1", "9094");
    var expected = List.of(TOPIC_KEY, BOOTSTRAP_SERVERS_KEY);

    KafkaBootstrapServersMerger.merge(staticProperties);
    var actual = staticProperties.stream().map(StaticProperty::getInternalName).toList();

    assertEquals(expected, actual);
  }

  @Test
  void testMerge_selectedTopic_leavesItSelected() {
    var staticProperties = new ArrayList<StaticProperty>();
    staticProperties.add(topicWithSelectedOption("test-topic"));
    staticProperties.add(freeText(OLD_HOST_KEY, "broker1"));
    staticProperties.add(freeText(OLD_PORT_KEY, "9094"));

    KafkaBootstrapServersMerger.merge(staticProperties);
    var actual = selectedTopicOf(staticProperties);

    assertEquals("test-topic", actual.getName());
    assertTrue(actual.isSelected());
  }

  @Test
  void testMerge_noHost_returnsFalse() {
    var staticProperties = new ArrayList<StaticProperty>();
    staticProperties.add(freeText(TOPIC_KEY, "test-topic"));

    var actual = KafkaBootstrapServersMerger.merge(staticProperties);

    assertFalse(actual);
  }

  @Test
  void testMerge_noHost_leavesTheConfigurationsUntouched() {
    var staticProperties = new ArrayList<StaticProperty>();
    staticProperties.add(freeText(TOPIC_KEY, "test-topic"));
    var expected = List.of(TOPIC_KEY);

    KafkaBootstrapServersMerger.merge(staticProperties);
    var actual = staticProperties.stream().map(StaticProperty::getInternalName).toList();

    assertEquals(expected, actual);
  }

  /**
   * The configurations of an adapter or sink that was stored before both were merged.
   */
  private List<StaticProperty> storedConfigurations(String host, String port) {
    var staticProperties = new ArrayList<StaticProperty>();
    staticProperties.add(freeText(TOPIC_KEY, "test-topic"));
    staticProperties.add(freeText(OLD_HOST_KEY, host));
    staticProperties.add(freeText(OLD_PORT_KEY, port));
    return staticProperties;
  }

  private FreeTextStaticProperty freeText(String internalName, String value) {
    var staticProperty = new FreeTextStaticProperty(internalName, internalName, "");
    staticProperty.setValue(value);
    return staticProperty;
  }

  private RuntimeResolvableOneOfStaticProperty topicWithSelectedOption(String topic) {
    var staticProperty = new RuntimeResolvableOneOfStaticProperty(TOPIC_KEY, TOPIC_KEY, "");
    staticProperty.setOptions(List.of(new Option(topic, true)));
    return staticProperty;
  }

  private String bootstrapServersOf(List<StaticProperty> staticProperties) {
    return staticProperties
        .stream()
        .filter(staticProperty -> BOOTSTRAP_SERVERS_KEY.equals(staticProperty.getInternalName()))
        .map(staticProperty -> ((FreeTextStaticProperty) staticProperty).getValue())
        .findFirst()
        .orElseThrow();
  }

  private Option selectedTopicOf(List<StaticProperty> staticProperties) {
    return staticProperties
        .stream()
        .filter(staticProperty -> staticProperty instanceof RuntimeResolvableOneOfStaticProperty)
        .map(staticProperty -> ((RuntimeResolvableOneOfStaticProperty) staticProperty).getOptions().get(0))
        .findFirst()
        .orElseThrow();
  }
}
