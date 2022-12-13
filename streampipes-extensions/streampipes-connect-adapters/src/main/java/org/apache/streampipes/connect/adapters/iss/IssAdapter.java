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

package org.apache.streampipes.connect.adapters.iss;


import org.apache.streampipes.connect.adapter.Adapter;
import org.apache.streampipes.connect.adapter.util.PollingSettings;
import org.apache.streampipes.connect.adapters.PullAdapter;
import org.apache.streampipes.connect.adapters.iss.model.IssModel;
import org.apache.streampipes.connect.api.exception.AdapterException;
import org.apache.streampipes.connect.api.exception.ParseException;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.sdk.builder.adapter.GuessSchemaBuilder;
import org.apache.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.vocabulary.Geo;

import com.google.gson.Gson;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.apache.streampipes.sdk.helpers.EpProperties.doubleEp;
import static org.apache.streampipes.sdk.helpers.EpProperties.timestampProperty;

public class IssAdapter extends PullAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(IssAdapter.class);

  public static final String ID = "org.apache.streampipes.connect.adapters.iss";

  private static final String POLLING_INTERVAL_KEY = "polling-interval";
  private static final String ISS_ENDPOINT_URL = "http://api.open-notify.org/iss-now.json";

  private static final String Timestamp = "timestamp";
  private static final String Latitude = "latitude";
  private static final String Longitude = "longitude";

  private Integer pollingIntervalInSeconds;


  public IssAdapter() {
    super();
  }

  public IssAdapter(SpecificAdapterStreamDescription adapterStreamDescription) {
    super(adapterStreamDescription);
  }

  @Override
  protected void before() throws AdapterException {
    this.pollingIntervalInSeconds = 5;
  }

  @Override
  protected void pullData() {
    try {
      adapterPipeline.process(getNextPosition());
    } catch (IOException e) {
      LOG.error("Could not fetch ISS location data", e);
    }
  }

  private Map<String, Object> getNextPosition() throws IOException {
    String response = Request
        .Get(ISS_ENDPOINT_URL)
        .execute()
        .returnContent()
        .asString();

    IssModel issModel = new Gson().fromJson(response, IssModel.class);

    return asMap(issModel);
  }

  private Map<String, Object> asMap(IssModel issModel) {
    Map<String, Object> event = new HashMap<>();
    event.put(Timestamp, issModel.getTimestamp() * 1000);
    event.put(Latitude, issModel.getIssPosition().getLatitude());
    event.put(Longitude, issModel.getIssPosition().getLongitude());

    return event;
  }

  @Override
  protected PollingSettings getPollingInterval() {
    return PollingSettings.from(TimeUnit.SECONDS, this.pollingIntervalInSeconds);
  }

  @Override
  public SpecificAdapterStreamDescription declareModel() {
    SpecificAdapterStreamDescription description = SpecificDataStreamAdapterBuilder.create(ID)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .category(AdapterType.OpenData)
        .build();
    description.setAppId(ID);

    return description;
  }

  @Override
  public Adapter getInstance(SpecificAdapterStreamDescription adapterDescription) {
    return new IssAdapter(adapterDescription);
  }

  @Override
  public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription)
      throws AdapterException, ParseException {
    return GuessSchemaBuilder.create()
        .property(timestampProperty(Timestamp))
        .property(doubleEp(Labels.from(Latitude, "Latitude", "The latitude value of the current ISS location"),
            Latitude, Geo.lat))
        .property(doubleEp(Labels.from(Longitude, "Longitude",
                "The longitude value of the current ISS location"),
            Longitude, Geo.lng))
        .build();
  }

  @Override
  public String getId() {
    return ID;
  }
}
