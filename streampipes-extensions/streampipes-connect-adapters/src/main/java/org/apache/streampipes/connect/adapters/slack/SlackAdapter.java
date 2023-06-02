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

import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.api.connect.IEventCollector;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.connect.context.IAdapterGuessSchemaContext;
import org.apache.streampipes.extensions.api.connect.context.IAdapterRuntimeContext;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
import org.apache.streampipes.sdk.builder.adapter.GuessSchemaBuilder;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.vocabulary.SO;

import static org.apache.streampipes.sdk.helpers.EpProperties.stringEp;
import static org.apache.streampipes.sdk.helpers.EpProperties.timestampProperty;

public class SlackAdapter implements StreamPipesAdapter {

  public static final String ID = "org.apache.streampipes.connect.adapters.slack";

  private static final String SlackToken = "slack-token";
  private static final String Timestamp = "timestamp";
  private static final String Message = "message";
  private static final String Author = "author";
  private static final String Channel = "channel";

  private Thread thread;
  private SlackConsumer consumer;

  @Override
  public IAdapterConfiguration declareConfig() {
    return AdapterConfigurationBuilder.create(ID, SlackAdapter::new)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .withCategory(AdapterType.SocialMedia)
        .requiredTextParameter(Labels.withId(SlackToken))
        .buildConfiguration();
  }

  @Override
  public void onAdapterStarted(IAdapterParameterExtractor extractor,
                               IEventCollector collector,
                               IAdapterRuntimeContext adapterRuntimeContext) {
    String slackApiToken = extractor.getStaticPropertyExtractor().singleValueParameter(SlackToken, String.class);
    this.consumer = new SlackConsumer(collector, slackApiToken);
    this.thread = new Thread(consumer);
    this.thread.start();
  }

  @Override
  public void onAdapterStopped(IAdapterParameterExtractor extractor,
                               IAdapterRuntimeContext adapterRuntimeContext) {
    this.consumer.stop();
    this.thread.stop();
  }

  @Override
  public GuessSchema onSchemaRequested(IAdapterParameterExtractor extractor,
                                       IAdapterGuessSchemaContext adapterGuessSchemaContext) {
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
}
