/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.streampipes.sources.random;

import org.streampipes.container.declarer.DataSetDeclarer;
import org.streampipes.container.model.DataSetReplayFinishedNotifier;
import org.streampipes.messaging.kafka.SpKafkaProducer;
import org.streampipes.model.SpDataSet;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.model.grounding.KafkaTransportProtocol;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.sdk.builder.DataSetBuilder;
import org.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.sdk.utils.Datatypes;

import java.net.URI;

public class RandomNumberDataSet implements DataSetDeclarer {

  private SpKafkaProducer kafkaPublisher;

  @Override
  public SpDataSet declareModel(DataSourceDescription sep) {
    return DataSetBuilder.create("random-data-set", "Random Data Set", "")
            .property(EpProperties.timestampProperty("timestamp"))
            .property(PrimitivePropertyBuilder
                    .create(Datatypes.String, "sensorId")
                    .label("Sensor ID")
                    .description("The ID of the sensor")
                    .domainProperty("http://domain.prop/sensorId")
                    .scope(PropertyScope.DIMENSION_PROPERTY)
                    .build())
            .property(PrimitivePropertyBuilder
                    .create(Datatypes.Float, "pressure")
                    .label("Pressure")
                    .description("Measures the current pressure")
                    .domainProperty("http://domain.prop/pressure")
                    .measurementUnit(URI.create("http://qudt.org/vocab/unit#Kilogram"))
                    .scope(PropertyScope.MEASUREMENT_PROPERTY)
                    .build())
            .supportedFormat(SupportedFormats.jsonFormat())
            .supportedProtocol(SupportedProtocols.kafka())
            .build();
  }

  @Override
  public void executeStream() {

  }

  @Override
  public boolean isExecutable() {
    return false;
  }


  @Override
  public boolean invokeRuntime(SpDataSet dataSetDescription, DataSetReplayFinishedNotifier replayFinishedNotifier) {

    String brokerUrl = dataSetDescription.getEventGrounding().getTransportProtocol().getBrokerHostname();
    KafkaTransportProtocol ktp = (KafkaTransportProtocol) dataSetDescription.getEventGrounding().getTransportProtocol();
    brokerUrl = brokerUrl + ":" + ktp.getKafkaPort();
    String topic = dataSetDescription.getEventGrounding().getTransportProtocol().getTopicDefinition()
            .getActualTopicName();

    this.kafkaPublisher = new SpKafkaProducer(brokerUrl, topic);
    Runnable replay = () -> {
      for(int i = 0; i < 100; i++) {
        sendDummyMessage("{\"count\":" + i + "}");
      }
    };

    new Thread(replay).start();

    return true;
  }

  private void sendDummyMessage(String message) {
    System.out.println(message);
    this.kafkaPublisher.publish(message);
  }

  @Override
  public boolean detachRuntime(String pipelineId) {
    return true;
  }
}
