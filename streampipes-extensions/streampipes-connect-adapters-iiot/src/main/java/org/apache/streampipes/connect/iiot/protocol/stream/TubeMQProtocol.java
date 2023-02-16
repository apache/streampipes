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

package org.apache.streampipes.connect.iiot.protocol.stream;

import org.apache.streampipes.extensions.api.connect.IAdapterPipeline;
import org.apache.streampipes.extensions.api.connect.IFormat;
import org.apache.streampipes.extensions.api.connect.IParser;
import org.apache.streampipes.extensions.api.connect.IProtocol;
import org.apache.streampipes.extensions.api.connect.exception.AdapterException;
import org.apache.streampipes.extensions.api.connect.exception.ParseException;
import org.apache.streampipes.extensions.management.connect.SendToPipeline;
import org.apache.streampipes.extensions.management.connect.adapter.sdk.ParameterExtractor;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.grounding.ProtocolDescription;
import org.apache.streampipes.sdk.builder.adapter.ProtocolDescriptionBuilder;
import org.apache.streampipes.sdk.helpers.AdapterSourceType;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;

import org.apache.commons.io.IOUtils;
import org.apache.inlong.tubemq.client.common.PeerInfo;
import org.apache.inlong.tubemq.client.config.ConsumerConfig;
import org.apache.inlong.tubemq.client.consumer.ConsumePosition;
import org.apache.inlong.tubemq.client.consumer.MessageListener;
import org.apache.inlong.tubemq.client.consumer.PushMessageConsumer;
import org.apache.inlong.tubemq.client.exception.TubeClientException;
import org.apache.inlong.tubemq.client.factory.MessageSessionFactory;
import org.apache.inlong.tubemq.client.factory.TubeSingleSessionFactory;
import org.apache.inlong.tubemq.corebase.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

public class TubeMQProtocol extends BrokerProtocol {

  private static final Logger LOGGER = LoggerFactory.getLogger(TubeMQProtocol.class);

  public static final String ID = "org.apache.streampipes.connect.iiot.protocol.stream.tubemq";

  public static final String TOPIC_KEY = "tubemq-topic";
  public static final String MASTER_HOST_AND_PORT_KEY = "tubemq-master-host-and-port";
  public static final String CONSUMER_GROUP_KEY = "tubemq-consumer-group";

  private String consumerGroup;

  private MessageSessionFactory messageSessionFactory;
  private PushMessageConsumer pushConsumer;

  public TubeMQProtocol() {
  }

  private TubeMQProtocol(IParser parser, IFormat format, String masterHostAndPort, String topic, String consumerGroup) {
    super(parser, format, masterHostAndPort, topic);
    this.consumerGroup = consumerGroup;
  }

  @Override
  public IProtocol getInstance(ProtocolDescription protocolDescription, IParser parser, IFormat format) {
    final ParameterExtractor extractor = new ParameterExtractor(protocolDescription.getConfig());

    final String masterHostAndPort = extractor.singleValue(MASTER_HOST_AND_PORT_KEY, String.class);
    final String topic = extractor.singleValue(TOPIC_KEY, String.class);
    final String consumerGroup = extractor.singleValue(CONSUMER_GROUP_KEY, String.class);

    return new TubeMQProtocol(parser, format, masterHostAndPort, topic, consumerGroup);
  }

  @Override
  public ProtocolDescription declareModel() {
    return ProtocolDescriptionBuilder.create(ID).withAssets(Assets.DOCUMENTATION, Assets.ICON).withLocales(Locales.EN)
        .category(AdapterType.Generic).sourceType(AdapterSourceType.STREAM)
        .requiredTextParameter(Labels.withId(MASTER_HOST_AND_PORT_KEY)).requiredTextParameter(Labels.withId(TOPIC_KEY))
        .requiredTextParameter(Labels.withId(CONSUMER_GROUP_KEY)).build();
  }

  @Override
  public void run(IAdapterPipeline adapterPipeline) throws AdapterException {
    final SendToPipeline sendToPipeline = new SendToPipeline(format, adapterPipeline);

    final ConsumerConfig consumerConfig = new ConsumerConfig(brokerUrl, consumerGroup);
    consumerConfig.setConsumePosition(ConsumePosition.CONSUMER_FROM_LATEST_OFFSET);

    try {
      messageSessionFactory = new TubeSingleSessionFactory(consumerConfig);
      pushConsumer = messageSessionFactory.createPushConsumer(consumerConfig);

      pushConsumer.subscribe(topic, null, new MessageListener() {
        @Override
        public void receiveMessages(PeerInfo peerInfo, List<Message> messages) {
          for (final Message message : messages) {
            try {
              parser.parse(IOUtils.toInputStream(new String(message.getData()), "UTF-8"), sendToPipeline);
            } catch (ParseException e) {
              LOGGER.error("Error while parsing: " + e.getMessage());
              e.printStackTrace();
            }
          }
        }

        @Override
        public Executor getExecutor() {
          return null;
        }

        @Override
        public void stop() {
        }
      });
      pushConsumer.completeSubscribe();
    } catch (TubeClientException e) {
      shutdown(messageSessionFactory, pushConsumer);
      throw new AdapterException("Failed to create TubeMQ adapter.", e);
    }
  }

  @Override
  public void stop() {
    shutdown(messageSessionFactory, pushConsumer);
  }

  @Override
  public String getId() {
    return ID;
  }

  @Override
  protected List<byte[]> getNByteElements(int n) throws ParseException {
    final List<byte[]> elements = new ArrayList<>();

    final ConsumerConfig consumerConfig = new ConsumerConfig(brokerUrl, consumerGroup);
    consumerConfig.setConsumePosition(ConsumePosition.CONSUMER_FROM_FIRST_OFFSET);

    MessageSessionFactory messageSessionFactory = null;
    PushMessageConsumer pushConsumer = null;
    try {
      messageSessionFactory = new TubeSingleSessionFactory(consumerConfig);
      pushConsumer = messageSessionFactory.createPushConsumer(consumerConfig);

      final CountDownLatch countDownLatch = new CountDownLatch(n);
      pushConsumer.subscribe(topic, null, new MessageListener() {
        @Override
        public void receiveMessages(PeerInfo peerInfo, List<Message> messages) {
          for (final Message message : messages) {
            if (countDownLatch.getCount() == 0) {
              return;
            }
            elements.add(message.getData());
            countDownLatch.countDown();
          }
        }

        @Override
        public Executor getExecutor() {
          return null;
        }

        @Override
        public void stop() {
        }
      });
      pushConsumer.completeSubscribe();
      countDownLatch.await();
    } catch (TubeClientException | InterruptedException e) {
      throw new ParseException("Failed to getNByteElements", e);
    } finally {
      shutdown(messageSessionFactory, pushConsumer);
    }

    return elements;
  }

  private static void shutdown(MessageSessionFactory messageSessionFactory, PushMessageConsumer pushConsumer) {
    if (pushConsumer != null && !pushConsumer.isShutdown()) {
      try {
        pushConsumer.shutdown();
      } catch (Throwable ex) {
        LOGGER.error("Failed to stop pushConsumer when TubeClientException occurred.");
      }
    }

    if (messageSessionFactory != null) {
      try {
        messageSessionFactory.shutdown();
      } catch (TubeClientException ex) {
        LOGGER.error("Failed to stop messageSessionFactory when TubeClientException occurred.");
      }
    }
  }
}
