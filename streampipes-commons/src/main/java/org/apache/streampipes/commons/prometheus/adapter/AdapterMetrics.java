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
package org.apache.streampipes.commons.prometheus.adapter;

import org.apache.streampipes.commons.prometheus.StreamPipesCollectorRegistry;

import java.util.HashMap;
import java.util.NoSuchElementException;

import io.prometheus.client.Gauge;

/**
 * AdapterMetrics class manages monitoring metrics related to StreamPipes adapters. It allows register and remove
 * adapter instances as well as updating the values of metrics. Currently, the following metrics are supported:
 * <ul>
 * <li><strong>adapter_events_published_total:</strong> The total number of events published per adapter</li>
 * </ul>
 * <p>
 * Usage Example:
 * 
 * <pre>{@code
 * AdapterMetrics adapterMetrics = new AdapterMetrics();
 * adapterMetrics.register("adapter-1", "Sample Adapter");
 * adapterMetrics.updateTotalEventsPublished("adapter-1", "Sample Adapter", 100);
 * adapterMetrics.remove("adapter-1", "Sample Adapter");
 * }</pre>
 */
public class AdapterMetrics {

  // Map to keep book about the registered adapters and to serve as mapping between adapter id and adapter name
  // Both are used as dimensions for the metric
  private final HashMap<String, String> registeredAdapters;

  private static final String ELEMENT_NOT_FOUND_TEMPLATE = "No entry for adapter '%s' found. Please register it first.";

  private final Gauge totalAdapterEventsPublishedMetric;

  public AdapterMetrics() {
    this.totalAdapterEventsPublishedMetric = StreamPipesCollectorRegistry.registerGauge(
            "adapter_events_published_total", "Total amount of events published per adapter", "adapterId",
            "adapterName");
    this.registeredAdapters = new HashMap<>();
  }

  /**
   * Registers a new adapter with the given adapterId and adapterName.
   *
   * @param adapterId
   *          The unique identifier for the adapter
   * @param adapterName
   *          The name of the adapter
   */
  public void register(String adapterId, String adapterName) {
    if (!contains(adapterId)) {
      this.registeredAdapters.put(adapterId, adapterName);
    }
  }

  /**
   * Checks if an adapter with the specified adapterId is registered.
   *
   * @param adapterId
   *          The unique identifier for the adapter
   * @return True if the adapter is registered, false otherwise
   */
  public boolean contains(String adapterId) {
    return this.registeredAdapters.containsKey(adapterId);
  }

  /**
   * Updates the total number of events published for a specific adapter.
   *
   * @param adapterId
   *          The unique identifier for the adapter
   * @param adapterName
   *          The name of the adapter
   * @param totalEventsPublished
   *          The total number of events published by the adapter
   * @throws NoSuchElementException
   *           If the adapter is not registered yet
   */
  public void updateTotalEventsPublished(String adapterId, String adapterName, long totalEventsPublished) {
    if (contains(adapterId)) {
      // Order of labels needs to be preserved and match the one in AdapterMetrics#register
      totalAdapterEventsPublishedMetric.labels(adapterId, adapterName).set(totalEventsPublished);
    } else {
      throw new NoSuchElementException(ELEMENT_NOT_FOUND_TEMPLATE.formatted(adapterId));
    }
  }

  /**
   * Removes the specified adapter from the registry. This results in no more metrics being published for this adapter.
   *
   * @param adapterId
   *          The unique identifier for the adapter
   * @param adapterName
   *          The name of the adapter
   * @throws NoSuchElementException
   *           If the adapter is not found in the registry
   */
  public void remove(String adapterId, String adapterName) {
    if (contains(adapterId)) {
      this.totalAdapterEventsPublishedMetric.remove(adapterId, adapterName);
      this.registeredAdapters.remove(adapterId);
    } else {
      throw new NoSuchElementException(ELEMENT_NOT_FOUND_TEMPLATE.formatted(adapterId));
    }
  }

  /**
   * Returns the number of registered adapters in the registry.
   *
   * @return The number of registered adapters
   */
  public int size() {
    return registeredAdapters.size();
  }
}
