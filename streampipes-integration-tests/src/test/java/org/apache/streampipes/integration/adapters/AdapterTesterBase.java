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
package org.apache.streampipes.integration.adapters;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.sdk.extractor.AdapterParameterExtractor;

import org.testcontainers.shaded.com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class AdapterTesterBase implements AutoCloseable {
  private StreamPipesAdapter adapter;

  private List<Map<String, Object>> expectedEvents;

  private int counter;

  /**
   Executes the test procedure for the adapter integration test.
   This method performs the necessary actions to test the full integraion of adapters with third party services.
   <ul>
   <li>First, it prepares the environment for the integration test by setting up a service (e.g., a Docker container)
   that the adapter will interact with. This ensures a controlled and consistent test environment.</li>
   <li>Next, it prepares the adapter being tested, configuring it with the necessary parameters and dependencies.</li>
   <li>Then, it generates sample data that simulates real-world scenarios to be processed by the adapter.</li>
   <li>Finally, it validates that the sample data is correctly processed by the adapter, ensuring that the integration
   is functioning as expected. This may involve verifying the transformed output or checking the interactions
   with the service.</li>
   </ul>
   * @throws Exception exception when data can not be produced
   */
  public void run() throws Exception {
    // prepare the third party docker service for the test (e.g. MQTT Broker)
    startAdapterService();

    // generate the AdapterConfiguration for the test adapter
    IAdapterConfiguration adapterConfiguration = prepareAdapter();

    // start the adapter instance
    adapter = startAdapter(adapterConfiguration);

    // wait a second to make sure the consumer is ready
    TimeUnit.MILLISECONDS.sleep(1000);

    // get events for broker
    expectedEvents = getTestEvents();

    // send events to thrird party service
    publishEvents(expectedEvents);

    // validate that events where send correctly
    validate(expectedEvents);
  }

  /**
   * Start the adapter and inject a collector that validates that the events are created correctly by the adapter
   * under test
   * @param adapterConfiguration configuration for the final adapter
   * @return an instance of the adapter
   * @throws AdapterException when adapter can not be started
   */
  public StreamPipesAdapter startAdapter(IAdapterConfiguration adapterConfiguration) throws AdapterException {
    var adapter = getAdapterInstance();

    var registeredParsers = adapterConfiguration.getSupportedParsers();
    var extractor = AdapterParameterExtractor.from(adapterConfiguration.getAdapterDescription(), registeredParsers);


    adapter.onAdapterStarted(extractor, (event -> {
      // This collector validates that the events are sent correctly and within the right order
      assertTrue(Maps.difference(event, expectedEvents.get(counter)).areEqual());
      counter++;
    }), null);
    return adapter;

  }

  /**
   * Starts the third party service to test the adapter (e.g. MQTT broker in docker service)
   * @throws Exception when service can not be started
   */
  public abstract void startAdapterService() throws Exception;

  /**
   * Create the AdapterConfiguration that initializes the adapter instance
   * @return AdapterConfiguration
   */
  public abstract IAdapterConfiguration prepareAdapter() throws AdapterException;

  /**
   * Create an instance of the adpater
   * @return AdapterInterface
   */
  public abstract StreamPipesAdapter getAdapterInstance();

  /**
   * Create the list of events that should be emitted by the adapter
   * @return List of events
   */
  public abstract List<Map<String, Object>> getTestEvents();

  /**
   * Publish the events to the third party service (e.g. message broker)
   * @param events to publish
   * @throws Exception when events can not be published to service
   */
  public abstract void publishEvents(List<Map<String, Object>> events) throws Exception;

  /**
   * Validates that the total amount of events is equal to the expected amount within a specific timeframe.
   * If the expected amount is not reached within the specified timeframe, the test fails.
   * The correctness of the events itself is already validated within the collector
   * @param expectedEvents list of the expected events
   * @throws InterruptedException when there is an error on wait
   */
  public void validate(List<Map<String, Object>> expectedEvents) throws InterruptedException {
    int retry = 0;
    while (counter != expectedEvents.size() && retry < 5) {
      Thread.sleep(1000);
      retry++;
    }

    assertEquals(expectedEvents.size(), counter);
  }

  /**
   * Used to stop the adapter in case there was an error
   * @throws AdapterException when adapter can not be stopped
   */
  public void stopAdapter() throws AdapterException {
    if (adapter != null) {
      adapter.onAdapterStopped(null, null);
    }
  }
}
