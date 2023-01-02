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

package org.apache.streampipes.connect.adapters.netio;

import org.apache.streampipes.connect.adapters.PullAdapter;
import org.apache.streampipes.connect.adapters.netio.model.NetioAllPowerOutputs;
import org.apache.streampipes.connect.adapters.netio.model.NetioPowerOutput;
import org.apache.streampipes.extensions.api.connect.exception.AdapterException;
import org.apache.streampipes.extensions.management.connect.adapter.Adapter;
import org.apache.streampipes.extensions.management.connect.adapter.sdk.ParameterExtractor;
import org.apache.streampipes.extensions.management.connect.adapter.util.PollingSettings;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;

import com.google.gson.Gson;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class NetioRestAdapter extends PullAdapter {

  /**
   * A unique id to identify the adapter type
   */
  public static final String ID = "org.apache.streampipes.connect.iiot.adapters.netio.rest";

  /**
   * Keys of user configuration parameters
   */
  private static final String NETIO_IP = "NETIO_IP";
  private static final String NETIO_USERNAME = "NETIO_USERNAME";
  private static final String NETIO_PASSWORD = "NETIO_PASSWORD";
  private static final String NETIO_POLLING_INTERVAL = "NETIO_POLLING_INTERVAL";

  /**
   * Values of user configuration parameters
   */
  private String ip;
  private String username;
  private String passowrd;
  private int pollingInterval;

  /**
   * Empty constructor and a constructor with SpecificAdapterStreamDescription are mandatory
   */
  public NetioRestAdapter() {
  }

  public NetioRestAdapter(SpecificAdapterStreamDescription adapterDescription) {
    super(adapterDescription);
  }


  /**
   * Describe the adapter and define what user inputs are required.
   * Currently, users can just select one node, this will be extended in the future
   *
   * @return
   */
  @Override
  public SpecificAdapterStreamDescription declareModel() {

    SpecificAdapterStreamDescription description = SpecificDataStreamAdapterBuilder.create(ID)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .category(AdapterType.Energy)
        .requiredTextParameter(Labels.withId(NETIO_IP))
        .requiredIntegerParameter(Labels.withId(NETIO_POLLING_INTERVAL), 2)
        .requiredTextParameter(Labels.withId(NETIO_USERNAME))
        .requiredStaticProperty(StaticProperties.secretValue(Labels.withId(NETIO_PASSWORD)))
        .build();
    description.setAppId(ID);


    return description;
  }

  /**
   * Takes the user input and creates the event schema. The event schema describes the properties of the event stream.
   *
   * @param adapterDescription
   * @return
   */
  @Override
  public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription) throws AdapterException {
    getConfigurations(adapterDescription);

    try {
      requestData();

      return NetioUtils.getNetioSchema();
    } catch (IOException e) {
      if (e instanceof HttpResponseException && ((HttpResponseException) e).getStatusCode() == 401) {
        throw new AdapterException(
            "Unauthorized! Could not connect to NETIO sensor: " + this.ip + " with username " + this.username);
      } else {
        throw new AdapterException("Could not connect to NETIO sensor: " + this.ip + " with username " + this.username);
      }
    }

  }

  /**
   * This method is executed when the adapter is started. A connection to the PLC is initialized
   *
   * @throws AdapterException
   */
  @Override
  protected void before() throws AdapterException {
    // Extract user input
    getConfigurations(adapterDescription);

  }

  /**
   * pullData is called iteratively according to the polling interval defined in getPollInterval.
   */
  @Override
  protected void pullData() {
    try {
      NetioAllPowerOutputs allPowerOutputs = requestData();

      for (NetioPowerOutput output : allPowerOutputs.getPowerOutputs()) {
        Map<String, Object> event = NetioUtils.getEvent(allPowerOutputs.getGobalMeasure(), output);
        this.adapterPipeline.process(event);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private NetioAllPowerOutputs requestData() throws IOException {
    Executor executor = Executor.newInstance()
        .auth(new HttpHost(this.ip, 80), this.username, this.passowrd);

    String response = executor.execute(Request.Get("http://" + this.ip + "/netio.json"))
        .returnContent().asString();

    NetioAllPowerOutputs allPowerOutputs = new Gson().fromJson(response, NetioAllPowerOutputs.class);

    return allPowerOutputs;
  }

  /**
   * Define the polling interval of this adapter. Default is to poll every second
   *
   * @return
   */
  @Override
  protected PollingSettings getPollingInterval() {
    return PollingSettings.from(TimeUnit.SECONDS, this.pollingInterval);
  }

  /**
   * Required by StreamPipes return a new adapter instance by calling the constructor with
   * SpecificAdapterStreamDescription
   *
   * @param adapterDescription
   * @return
   */
  @Override
  public Adapter getInstance(SpecificAdapterStreamDescription adapterDescription) {
    return new NetioRestAdapter(adapterDescription);
  }


  /**
   * Required by StreamPipes. Return the id of the adapter
   *
   * @return
   */
  @Override
  public String getId() {
    return ID;
  }

  /**
   * Extracts the user configuration from the SpecificAdapterStreamDescription and sets the local variales
   *
   * @param adapterDescription
   */
  private void getConfigurations(SpecificAdapterStreamDescription adapterDescription) {
    ParameterExtractor extractor = new ParameterExtractor(adapterDescription.getConfig());

    this.ip = extractor.singleValue(NETIO_IP, String.class);
    this.username = extractor.singleValue(NETIO_USERNAME, String.class);
    this.passowrd = extractor.secretValue(NETIO_PASSWORD);
    this.pollingInterval = extractor.singleValue(NETIO_POLLING_INTERVAL, Integer.class);
  }

}
