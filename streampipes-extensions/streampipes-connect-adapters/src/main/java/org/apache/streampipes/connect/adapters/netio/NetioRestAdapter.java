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

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.connect.adapters.netio.model.NetioAllPowerOutputs;
import org.apache.streampipes.connect.adapters.netio.model.NetioPowerOutput;
import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.api.connect.IEventCollector;
import org.apache.streampipes.extensions.api.connect.IPullAdapter;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.connect.context.IAdapterGuessSchemaContext;
import org.apache.streampipes.extensions.api.connect.context.IAdapterRuntimeContext;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.extensions.api.extractor.IParameterExtractor;
import org.apache.streampipes.extensions.management.connect.PullAdapterScheduler;
import org.apache.streampipes.extensions.management.connect.adapter.util.PollingSettings;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
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

public class NetioRestAdapter implements StreamPipesAdapter, IPullAdapter {

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
  private String password;
  private int pollingInterval;

  private PullAdapterScheduler scheduler;
  private IEventCollector collector;

  /**
   * pullData is called iteratively according to the polling interval defined in getPollInterval.
   */
  @Override
  public void pullData() {
    try {
      NetioAllPowerOutputs allPowerOutputs = requestData();

      for (NetioPowerOutput output : allPowerOutputs.getPowerOutputs()) {
        Map<String, Object> event = NetioUtils.getEvent(allPowerOutputs.getGobalMeasure(), output);
        collector.collect(event);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private NetioAllPowerOutputs requestData() throws IOException {
    Executor executor = Executor.newInstance()
        .auth(new HttpHost(this.ip, 80), this.username, this.password);

    String response = executor.execute(Request.Get("http://" + this.ip + "/netio.json"))
        .returnContent().asString();

    return new Gson().fromJson(response, NetioAllPowerOutputs.class);
  }

  @Override
  public PollingSettings getPollingInterval() {
    return PollingSettings.from(TimeUnit.SECONDS, this.pollingInterval);
  }

  /**
   * Extracts the user configuration from the SpecificAdapterStreamDescription and sets the local variales
   *
   * @param extractor StaticPropertyExtractor
   */
  private void applyConfiguration(IParameterExtractor<?> extractor) {

    this.ip = extractor.singleValueParameter(NETIO_IP, String.class);
    this.username = extractor.singleValueParameter(NETIO_USERNAME, String.class);
    this.password = extractor.secretValue(NETIO_PASSWORD);
    this.pollingInterval = extractor.singleValueParameter(NETIO_POLLING_INTERVAL, Integer.class);
  }

  @Override
  public IAdapterConfiguration declareConfig() {
    return AdapterConfigurationBuilder.create(ID, NetioRestAdapter::new)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withCategory(AdapterType.Energy)
        .requiredTextParameter(Labels.withId(NETIO_IP))
        .requiredIntegerParameter(Labels.withId(NETIO_POLLING_INTERVAL), 2)
        .requiredTextParameter(Labels.withId(NETIO_USERNAME))
        .requiredStaticProperty(StaticProperties.secretValue(Labels.withId(NETIO_PASSWORD)))
        .buildConfiguration();
  }

  @Override
  public void onAdapterStarted(IAdapterParameterExtractor extractor,
                               IEventCollector collector,
                               IAdapterRuntimeContext adapterRuntimeContext) {
    this.applyConfiguration(extractor.getStaticPropertyExtractor());
    this.collector = collector;
    this.scheduler = new PullAdapterScheduler();
    scheduler.schedule(this, extractor.getAdapterDescription().getElementId());
  }

  @Override
  public void onAdapterStopped(IAdapterParameterExtractor extractor,
                               IAdapterRuntimeContext adapterRuntimeContext) {
    this.scheduler.shutdown();
  }

  @Override
  public GuessSchema onSchemaRequested(IAdapterParameterExtractor extractor,
                                       IAdapterGuessSchemaContext adapterGuessSchemaContext) throws AdapterException {
    applyConfiguration(extractor.getStaticPropertyExtractor());

    try {
      requestData();
      return NetioUtils.getNetioSchema();
    } catch (IOException e) {
      if (e instanceof HttpResponseException && ((HttpResponseException) e).getStatusCode() == 401) {
        throw new AdapterException(
            "Unauthorized! Could not connect to NETIO sensor: " + this.ip + " with username " + this.username);
      } else {
        throw
            new AdapterException("Could not connect to NETIO sensor: " + this.ip + " with username " + this.username);
      }
    }
  }
}
