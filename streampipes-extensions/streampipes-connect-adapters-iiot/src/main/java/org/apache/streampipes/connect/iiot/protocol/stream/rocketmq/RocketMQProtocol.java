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
package org.apache.streampipes.connect.iiot.protocol.stream.rocketmq;


import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.commons.exceptions.connect.ParseException;
import org.apache.streampipes.connect.iiot.protocol.stream.BrokerEventProcessor;
import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.api.connect.IEventCollector;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.connect.context.IAdapterGuessSchemaContext;
import org.apache.streampipes.extensions.api.connect.context.IAdapterRuntimeContext;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.management.connect.adapter.parser.Parsers;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;

import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class RocketMQProtocol implements StreamPipesAdapter {

  public static final String ID = "org.apache.streampipes.connect.iiot.protocol.stream.rocketmq";

  public static final String TOPIC_KEY = "rocketmq-topic";
  public static final String ENDPOINT_KEY = "rocketmq-endpoint";
  public static final String CONSUMER_GROUP_KEY = "rocketmq-consumer-group";

  private String endpoint;
  private String topic;
  private String consumerGroup;

  private Thread thread;
  private RocketMQConsumer rocketMQConsumer;

  public RocketMQProtocol() {
  }

  public void applyConfiguration(IStaticPropertyExtractor extractor) {
    this.endpoint = extractor.singleValueParameter(ENDPOINT_KEY, String.class);
    this.topic = extractor.singleValueParameter(TOPIC_KEY, String.class);
    this.consumerGroup = extractor.singleValueParameter(CONSUMER_GROUP_KEY, String.class);
  }

  @Override
  public IAdapterConfiguration declareConfig() {
    return AdapterConfigurationBuilder
        .create(ID, RocketMQProtocol::new)
        .withSupportedParsers(Parsers.defaultParsers())
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .withCategory(AdapterType.Generic)
        .requiredTextParameter(Labels.withId(ENDPOINT_KEY))
        .requiredTextParameter(Labels.withId(TOPIC_KEY))
        .requiredTextParameter(Labels.withId(CONSUMER_GROUP_KEY))
        .buildConfiguration();
  }

  @Override
  public void onAdapterStarted(IAdapterParameterExtractor extractor,
                               IEventCollector collector,
                               IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException {
    this.applyConfiguration(extractor.getStaticPropertyExtractor());
    this.rocketMQConsumer = new RocketMQConsumer(
        endpoint,
        topic,
        consumerGroup,
        new BrokerEventProcessor(extractor.selectedParser(), collector));

    thread = new Thread(this.rocketMQConsumer);
    thread.start();
  }

  @Override
  public void onAdapterStopped(IAdapterParameterExtractor extractor,
                               IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException {
    try {
      rocketMQConsumer.stop();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public GuessSchema onSchemaRequested(IAdapterParameterExtractor extractor,
                                       IAdapterGuessSchemaContext adapterGuessSchemaContext) throws AdapterException {
    List<byte[]> nEventsByte = new ArrayList<>(1);
    CountDownLatch latch = new CountDownLatch(1);
    this.applyConfiguration(extractor.getStaticPropertyExtractor());

    PushConsumer consumer = null;
    try {
      consumer = RocketMQUtils.createConsumer(endpoint, topic, consumerGroup, messageView -> {
        nEventsByte.add(messageView.getBody().array());

        latch.countDown();
        return ConsumeResult.SUCCESS;
      });
    } catch (ClientException e) {
      e.printStackTrace();
      try {
        if (consumer != null) {
          consumer.close();
        }
      } catch (IOException ignored) {
      }
      throw new ParseException("Failed to fetch messages.", e);
    }

    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    try {
      consumer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return extractor.selectedParser().getGuessSchema(new ByteArrayInputStream(nEventsByte.get(0)));
  }
}
