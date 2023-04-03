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

//public class NatsProtocol extends BrokerProtocol {
//
//  public static final String ID = "org.apache.streampipes.connect.iiot.protocol.stream.nats";
//  private NatsConfig natsConfig;
//  private NatsConsumer natsConsumer;
//
//  private static final int MAX_TIMEOUT = 8000;
//  private static final int TIMEOUT = 100;
//
//  public NatsProtocol() {
//
//  }
//
//  public NatsProtocol(NatsConfig natsConfig,
//                      IParser parser,
//                      IFormat format) {
//    super(parser, format, natsConfig.getNatsUrls(), natsConfig.getSubject());
//    this.natsConfig = natsConfig;
//  }
//
//  @Override
//  public IProtocol getInstance(ProtocolDescription protocolDescription,
//                               IParser parser,
//                               IFormat format) {
//    StaticPropertyExtractor extractor =
//        StaticPropertyExtractor.from(protocolDescription.getConfig(), new ArrayList<>());
//
//    var natsConfig = NatsConfigUtils.from(extractor);
//
//    return new NatsProtocol(natsConfig, parser, format);
//  }
//
//  @Override
//  public ProtocolDescription declareModel() {
//    return ProtocolDescriptionBuilder.create(ID)
//        .category(AdapterType.Generic)
//        .withLocales(Locales.EN)
//        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
//        .sourceType(AdapterSourceType.STREAM)
//        .requiredTextParameter(Labels.withId(URLS_KEY), false, false)
//        .requiredTextParameter(Labels.withId(SUBJECT_KEY), false, false)
//        .requiredAlternatives(Labels.withId(ACCESS_MODE), getAccessModeAlternativesOne(),
//            getAccessModeAlternativesTwo())
//        .requiredAlternatives(Labels.withId(CONNECTION_PROPERTIES), getConnectionPropertiesAlternativesOne(),
//            getConnectionPropertiesAlternativesTwo())
//        .build();
//  }
//
//  public static StaticPropertyAlternative getAccessModeAlternativesOne() {
//    return Alternatives.from(Labels.withId(ANONYMOUS_ACCESS));
//
//  }
//
//  public static StaticPropertyAlternative getAccessModeAlternativesTwo() {
//    return Alternatives.from(Labels.withId(USERNAME_ACCESS),
//        StaticProperties.group(Labels.withId(USERNAME_GROUP),
//            StaticProperties.stringFreeTextProperty(Labels.withId(USERNAME_KEY)),
//            StaticProperties.secretValue(Labels.withId(PASSWORD_KEY))));
//
//  }
//
//  public static StaticPropertyAlternative getConnectionPropertiesAlternativesOne() {
//    return Alternatives.from(Labels.withId(NONE_PROPERTIES));
//
//  }
//
//  public static StaticPropertyAlternative getConnectionPropertiesAlternativesTwo() {
//    return Alternatives.from(Labels.withId(CUSTOM_PROPERTIES),
//        StaticProperties.group(Labels.withId(CONNECTION_PROPERTIES_GROUP),
//            StaticProperties.stringFreeTextProperty(Labels.withId(PROPERTIES_KEY))));
//
//  }
//
//  @Override
//  public void run(IAdapterPipeline adapterPipeline) throws AdapterException {
//    SendToPipeline stk = new SendToPipeline(format, adapterPipeline);
//    this.natsConsumer = new NatsConsumer();
//    try {
//      this.natsConsumer.connect(natsConfig, new BrokerEventProcessor(stk, parser));
//    } catch (IOException | InterruptedException e) {
//      throw new AdapterException("Error when connecting to the Nats broker on "
//      + natsConfig.getNatsUrls() + " . ", e);
//    }
//  }
//
//  @Override
//  public void stop() {
//    this.natsConsumer.disconnect();
//  }
//
//  @Override
//  public String getId() {
//    return ID;
//  }
//
//  @Override
//  protected List<byte[]> getNByteElements(int n) throws ParseException {
//    List<byte[]> elements = new ArrayList<>();
//    this.natsConsumer = new NatsConsumer();
//    final boolean[] completed = {false};
//    InternalEventProcessor<byte[]> processor = event -> {
//      elements.add(event);
//      if (elements.size() >= n) {
//        completed[0] = true;
//      }
//    };
//
//    try {
//      this.natsConsumer.connect(natsConfig, processor);
//    } catch (IOException | InterruptedException e) {
//      throw new ParseException("Could not connect to Nats broker", e);
//    }
//
//    int totalTimeout = 0;
//    while (!completed[0] && totalTimeout < MAX_TIMEOUT) {
//      try {
//        TimeUnit.MILLISECONDS.sleep(TIMEOUT);
//        totalTimeout += TIMEOUT;
//      } catch (InterruptedException e) {
//        e.printStackTrace();
//      }
//    }
//    if (elements.size() > 0) {
//      return elements;
//    } else {
//      throw new ParseException("Did not receive any data within " + MAX_TIMEOUT / 1000
//          + " seconds, is this subjects currently providing data?");
//    }
//  }
//}
