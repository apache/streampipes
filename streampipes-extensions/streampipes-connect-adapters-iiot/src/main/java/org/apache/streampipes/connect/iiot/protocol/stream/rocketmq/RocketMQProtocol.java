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


import org.apache.streampipes.connect.iiot.protocol.stream.BrokerProtocol;
import org.apache.streampipes.extensions.api.connect.IAdapterPipeline;
import org.apache.streampipes.extensions.api.connect.IFormat;
import org.apache.streampipes.extensions.api.connect.IParser;
import org.apache.streampipes.extensions.api.connect.IProtocol;
import org.apache.streampipes.extensions.api.connect.exception.AdapterException;
import org.apache.streampipes.extensions.api.connect.exception.ParseException;
import org.apache.streampipes.extensions.management.connect.SendToPipeline;
import org.apache.streampipes.extensions.management.connect.adapter.sdk.ParameterExtractor;
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.grounding.ProtocolDescription;
import org.apache.streampipes.sdk.builder.adapter.ProtocolDescriptionBuilder;
import org.apache.streampipes.sdk.helpers.AdapterSourceType;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;

import org.apache.commons.io.IOUtils;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class RocketMQProtocol extends BrokerProtocol {

  public static final String ID = "org.apache.streampipes.connect.iiot.protocol.stream.rocketmq";

  public static final String TOPIC_KEY = "rocketmq-topic";
  public static final String ENDPOINT_KEY = "rocketmq-endpoint";
  public static final String CONSUMER_GROUP_KEY = "rocketmq-consumer-group";

  private Logger logger = LoggerFactory.getLogger(RocketMQProtocol.class);

  private String consumerGroup;

  private Thread thread;
  private RocketMQConsumer rocketMQConsumer;

  public RocketMQProtocol() {
  }

  public RocketMQProtocol(IParser parser, IFormat format, String brokerUrl, String topic, String consumerGroup) {
    super(parser, format, brokerUrl, topic);
    this.consumerGroup = consumerGroup;
  }

  @Override
  public IProtocol getInstance(ProtocolDescription protocolDescription, IParser parser, IFormat format) {
    ParameterExtractor extractor = new ParameterExtractor(protocolDescription.getConfig());
    String endpoint = extractor.singleValue(ENDPOINT_KEY, String.class);
    String topic = extractor.singleValue(TOPIC_KEY, String.class);
    String consumerGroup = extractor.singleValue(CONSUMER_GROUP_KEY, String.class);

    return new RocketMQProtocol(parser, format, endpoint, topic, consumerGroup);
  }

  @Override
  public ProtocolDescription declareModel() {
    return ProtocolDescriptionBuilder.create(ID)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .category(AdapterType.Generic)
        .sourceType(AdapterSourceType.STREAM)
        .requiredTextParameter(Labels.withId(ENDPOINT_KEY))
        .requiredTextParameter(Labels.withId(TOPIC_KEY))
        .requiredTextParameter(Labels.withId(CONSUMER_GROUP_KEY))
        .build();
  }

  @Override
  public void run(IAdapterPipeline adapterPipeline) throws AdapterException {
    SendToPipeline stk = new SendToPipeline(format, adapterPipeline);
    this.rocketMQConsumer = new RocketMQConsumer(brokerUrl, topic, consumerGroup, new EventProcessor(stk));

    thread = new Thread(this.rocketMQConsumer);
    thread.start();
  }

  @Override
  public void stop() {
    try {
      rocketMQConsumer.stop();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public String getId() {
    return ID;
  }

  @Override
  protected List<byte[]> getNByteElements(int n) throws ParseException {
    List<byte[]> nEventsByte = new ArrayList<>(n);
    CountDownLatch latch = new CountDownLatch(n);

    PushConsumer consumer = null;
    try {
      consumer = RocketMQUtils.createConsumer(brokerUrl, topic, consumerGroup, messageView -> {
        InputStream inputStream = new ByteArrayInputStream(messageView.getBody().array());
        nEventsByte.addAll(parser.parseNEvents(inputStream, n));

        latch.countDown();
        return ConsumeResult.SUCCESS;
      });
    } catch (ClientException e) {
      e.printStackTrace();
      try {
        if (consumer != null) {
          consumer.close();
        }
      } catch (IOException ex) {
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

    return nEventsByte;
  }

  private class EventProcessor implements InternalEventProcessor<byte[]> {
    private SendToPipeline stk;

    public EventProcessor(SendToPipeline stk) {
      this.stk = stk;
    }

    @Override
    public void onEvent(byte[] payload) {
      try {
        parser.parse(IOUtils.toInputStream(new String(payload), "UTF-8"), stk);
      } catch (ParseException e) {
        logger.error("Error while parsing: " + e.getMessage());
      }
    }
  }
}
