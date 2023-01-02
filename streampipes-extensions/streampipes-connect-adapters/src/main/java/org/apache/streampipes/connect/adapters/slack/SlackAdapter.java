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
package org.apache.streampipes.connect.adapters.slack;

import org.apache.streampipes.extensions.api.connect.exception.AdapterException;
import org.apache.streampipes.extensions.api.connect.exception.ParseException;
import org.apache.streampipes.extensions.management.connect.adapter.Adapter;
import org.apache.streampipes.extensions.management.connect.adapter.model.specific.SpecificDataStreamAdapter;
import org.apache.streampipes.extensions.management.connect.adapter.sdk.ParameterExtractor;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.sdk.builder.adapter.GuessSchemaBuilder;
import org.apache.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.vocabulary.SO;

import static org.apache.streampipes.sdk.helpers.EpProperties.stringEp;
import static org.apache.streampipes.sdk.helpers.EpProperties.timestampProperty;

public class SlackAdapter extends SpecificDataStreamAdapter {

  public static final String ID = "org.apache.streampipes.connect.adapters.slack";

  private static final String SlackToken = "slack-token";
  private static final String Timestamp = "timestamp";
  private static final String Message = "message";
  private static final String Author = "author";
  private static final String Channel = "channel";

  private String slackApiToken;
  private Thread thread;
  private SlackConsumer consumer;

  public SlackAdapter() {
    super();
  }

  public SlackAdapter(SpecificAdapterStreamDescription adapterStreamDescription) {
    super(adapterStreamDescription);
    ParameterExtractor extractor = new ParameterExtractor(adapterStreamDescription.getConfig());
    this.slackApiToken = extractor.singleValue(SlackToken);
  }

  @Override
  public SpecificAdapterStreamDescription declareModel() {
    return SpecificDataStreamAdapterBuilder.create(ID)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .category(AdapterType.SocialMedia)
        .requiredTextParameter(Labels.withId(SlackToken))
        .build();
  }

  @Override
  public void startAdapter() throws AdapterException {
    this.consumer = new SlackConsumer(adapterPipeline, slackApiToken);
    this.thread = new Thread(consumer);
    this.thread.start();
  }

  @Override
  public void stopAdapter() throws AdapterException {
    this.consumer.stop();
    this.thread.stop();
  }

  @Override
  public Adapter getInstance(SpecificAdapterStreamDescription adapterDescription) {
    return new SlackAdapter(adapterDescription);
  }

  @Override
  public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription)
      throws AdapterException, ParseException {
    return GuessSchemaBuilder.create()
        .property(timestampProperty(Timestamp))
        .property(stringEp(Labels.from(Author, "Author", "The username of the sender of the "
                + "Slack message"),
            Author, SO.TEXT))
        .property(stringEp(Labels.from(Channel, "Channel", "The Slack channel"), Channel,
            SO.TEXT))
        .property(stringEp(Labels.from(Message, "Message", "The Slack message"),
            Message, SO.TEXT))
        .build();
  }

  @Override
  public String getId() {
    return ID;
  }
}
