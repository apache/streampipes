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
package org.apache.streampipes.connect.adapters.wikipedia;

import org.apache.streampipes.connect.adapters.wikipedia.model.WikipediaModel;
import org.apache.streampipes.extensions.api.connect.exception.AdapterException;
import org.apache.streampipes.extensions.api.connect.exception.ParseException;
import org.apache.streampipes.extensions.management.connect.adapter.model.specific.SpecificDataStreamAdapter;
import org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.sdk.builder.adapter.GuessSchemaBuilder;
import org.apache.streampipes.sdk.helpers.Labels;

import com.google.gson.Gson;

import static org.apache.streampipes.sdk.helpers.EpProperties.booleanEp;
import static org.apache.streampipes.sdk.helpers.EpProperties.integerEp;
import static org.apache.streampipes.sdk.helpers.EpProperties.longEp;
import static org.apache.streampipes.sdk.helpers.EpProperties.stringEp;
import static org.apache.streampipes.sdk.helpers.EpProperties.timestampProperty;

public abstract class WikipediaAdapter extends SpecificDataStreamAdapter {

  public static final String TIMESTAMP = "timestamp";
  public static final String TYPE = "type";
  public static final String EVENT_ID = "id";
  public static final String NAMESPACE = "namespace";
  public static final String TITLE = "title";
  public static final String USER = "user";
  public static final String BOT = "bot";
  public static final String MINOR = "minor";
  public static final String OLDLENGTH = "oldlength";
  public static final String NEWLENGTH = "newlength";
  public static final String OLDREVISION = "oldrevision";
  public static final String NEWREVISION = "newrevision";
  public static final String SERVERURL = "serverurl";
  public static final String SERVERNAME = "servername";
  public static final String WIKI = "wiki";
  public static final String URI = "uri";
  public static final String COMMENT = "comment";
  public static final String DOMAIN = "domain";

  private static final String VocabPrefix = "http://wikipedia.org/";
  private static final String WikipediaApiUrl = "https://stream.wikimedia"
      + ".org/v2/stream/recentchange";

  private Thread thread;
  private String type;
  private WikipediaSseConsumer consumer;

  public WikipediaAdapter(SpecificAdapterStreamDescription adapterStreamDescription, String type) {
    super(adapterStreamDescription);
    this.type = type;
  }

  public WikipediaAdapter() {
    super();
  }

  @Override
  public void startAdapter() throws AdapterException {
    Gson gson = new Gson();
    Runnable runnable = () -> {
      this.consumer = new WikipediaSseConsumer();
      try {
        this.consumer.consumeEventStream(WikipediaApiUrl,
            event -> {
              WikipediaModel wikipediaModel = gson.fromJson(event, WikipediaModel.class);
              if (wikipediaModel != null && wikipediaModel.getType() != null) {
                if (wikipediaModel.getType().equals(type)) {
                  adapterPipeline.process(new WikipediaModelConverter(wikipediaModel).makeMap());
                }
              }
            });
      } catch (Exception e) {
        e.printStackTrace();
      }
    };

    this.thread = new Thread(runnable);
    this.thread.start();
  }

  @Override
  public void stopAdapter() throws AdapterException {
    if (this.thread != null) {
      this.consumer.stop();
    }
  }

  @Override
  public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription)
      throws AdapterException, ParseException {
    return GuessSchemaBuilder.create()
        .property(timestampProperty(TIMESTAMP))
        .property(stringEp(Labels.from(EVENT_ID, "ID", ""), EVENT_ID, dp(EVENT_ID)))
        .property(stringEp(Labels.from(TYPE, "Type", "The change type (edit|new)"),
            TYPE, dp(TYPE)))
        .property(integerEp(Labels.from(NAMESPACE, "Namespace",
            "The Wikipedia namespace"), NAMESPACE, dp(NAMESPACE)))
        .property(stringEp(Labels.from(TITLE, "Title", "The article title"),
            TITLE, dp(TITLE)))
        .property(stringEp(Labels.from(USER, "User", "The user ID"),
            USER, dp(USER)))
        .property(booleanEp(Labels.from(BOT, "Bot", "Edited by a bot"),
            BOT, dp(BOT)))
        .property(booleanEp(Labels.from(MINOR, "Minor", "Minor edit"),
            MINOR, dp(MINOR)))
        .property(integerEp(Labels.from(OLDLENGTH, "Old length", ""),
            OLDLENGTH, dp(OLDLENGTH)))
        .property(integerEp(Labels.from(NEWLENGTH, "New length", ""),
            NEWLENGTH, dp(NEWLENGTH)))
        .property(longEp(Labels.from(OLDREVISION, "Old revision ID", ""),
            OLDREVISION, dp(OLDREVISION)))
        .property(longEp(Labels.from(NEWREVISION, "New revision ID", ""),
            NEWREVISION, dp(NEWREVISION)))
        .property(stringEp(Labels.from(SERVERURL, "Server URL", ""),
            SERVERURL, dp(SERVERURL)))
        .property(stringEp(Labels.from(SERVERNAME, "Server Name", ""),
            SERVERNAME, dp(SERVERNAME)))
        .property(stringEp(Labels.from(WIKI, "Wiki Name", ""),
            WIKI, dp(WIKI)))
        .property(stringEp(Labels.from(URI, "Internal URI", ""),
            URI, dp(URI)))
        .property(stringEp(Labels.from(COMMENT, "Comment", "Comment field"),
            COMMENT, dp(COMMENT)))
        .property(stringEp(Labels.from(DOMAIN, "Domain", "Wiki Domain"),
            DOMAIN, dp(DOMAIN)))
        .build();
  }

  public String dp(String domainPropertyName) {
    return VocabPrefix + domainPropertyName;
  }
}
