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

package org.apache.streampipes.extensions.connectors.kafka.sink;

import org.apache.streampipes.extensions.connectors.kafka.shared.kafka.KafkaConfigExtractor;
import org.apache.streampipes.extensions.connectors.kafka.shared.kafka.KafkaConfigProvider;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.staticproperty.CodeInputStaticProperty;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.sdk.extractor.DataSinkParameterExtractor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KafkaPublishSinkTest {

  @Test
  void testExtractSinkConfig_twoBrokers_keepsBothOfThem() {
    var sink = storedSink("host1:9092,host2:9092");
    var expected = "host1:9092,host2:9092";

    var actual = new KafkaConfigExtractor()
        .extractSinkConfig(DataSinkParameterExtractor.from(sink))
        .getBootstrapServers();

    assertEquals(expected, actual);
  }

  @Test
  void testExtractSinkConfig_singleBroker_keepsIt() {
    var sink = storedSink("host1:9092");
    var expected = "host1:9092";

    var actual = new KafkaConfigExtractor()
        .extractSinkConfig(DataSinkParameterExtractor.from(sink))
        .getBootstrapServers();

    assertEquals(expected, actual);
  }

  private DataSinkInvocation storedSink(String bootstrapServers) {
    var sink = new DataSinkInvocation(new KafkaPublishSink().declareConfig().getDescription());

    setFreeText(sink, KafkaConfigProvider.BOOTSTRAP_SERVERS_KEY, bootstrapServers);
    setFreeText(sink, KafkaConfigProvider.TOPIC_KEY, "test-topic");
    selectFirstAlternative(sink, KafkaConfigProvider.ACCESS_MODE);
    setCodeblock(sink, KafkaConfigProvider.ADDITIONAL_PROPERTIES, "");

    return sink;
  }

  private void setFreeText(DataSinkInvocation sink, String internalName, String value) {
    ((FreeTextStaticProperty) configurationOf(sink, internalName)).setValue(value);
  }

  private void setCodeblock(DataSinkInvocation sink, String internalName, String value) {
    ((CodeInputStaticProperty) configurationOf(sink, internalName)).setValue(value);
  }

  private void selectFirstAlternative(DataSinkInvocation sink, String internalName) {
    ((StaticPropertyAlternatives) configurationOf(sink, internalName))
        .getAlternatives()
        .get(0)
        .setSelected(true);
  }

  private StaticProperty configurationOf(DataSinkInvocation sink, String internalName) {
    return sink.getStaticProperties()
        .stream()
        .filter(staticProperty -> internalName.equals(staticProperty.getInternalName()))
        .findFirst()
        .orElseThrow();
  }
}
