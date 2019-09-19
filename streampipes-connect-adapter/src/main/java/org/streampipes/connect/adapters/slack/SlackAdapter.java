/*
Copyright 2019 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.connect.adapters.slack;

import org.streampipes.connect.adapter.Adapter;
import org.streampipes.connect.adapter.exception.AdapterException;
import org.streampipes.connect.adapter.exception.ParseException;
import org.streampipes.connect.adapter.model.specific.SpecificDataStreamAdapter;
import org.streampipes.connect.adapter.sdk.ParameterExtractor;
import org.streampipes.model.AdapterType;
import org.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.streampipes.model.connect.guess.GuessSchema;
import org.streampipes.sdk.builder.adapter.GuessSchemaBuilder;
import org.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.vocabulary.SO;

import static org.streampipes.sdk.helpers.EpProperties.stringEp;
import static org.streampipes.sdk.helpers.EpProperties.timestampProperty;

public class SlackAdapter extends SpecificDataStreamAdapter {

  public static final String ID = "http://streampipes.org/adapter/specific/slack";

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
    return SpecificDataStreamAdapterBuilder.create(ID, "Slack", "Subscribes to a Slack channel")
            .category(AdapterType.SocialMedia)
            .iconUrl("slack.png")
            .requiredTextParameter(Labels.from(SlackToken, "Slack API Token", "The API token of " +
                    "your Slack workspace"))
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
  public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription) throws AdapterException, ParseException {
    return GuessSchemaBuilder.create()
            .property(timestampProperty(Timestamp))
            .property(stringEp(Labels.from(Author, "Author", "The username of the sender of the " +
                            "Slack message"),
                    Author, SO.Text))
            .property(stringEp(Labels.from(Channel, "Channel", "The Slack channel"), Channel,
                    SO.Text))
            .property(stringEp(Labels.from(Message, "Message", "The Slack message"),
                    Message, SO.Text))
            .build();
  }

  @Override
  public String getId() {
    return ID;
  }
}
