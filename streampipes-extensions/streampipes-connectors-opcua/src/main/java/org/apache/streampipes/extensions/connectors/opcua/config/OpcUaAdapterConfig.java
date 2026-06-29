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

package org.apache.streampipes.extensions.connectors.opcua.config;

import org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaNamingStrategy;

public class OpcUaAdapterConfig extends OpcUaConfig {

  public static final int DEFAULT_SUBSCRIPTION_PUBLISHING_INTERVAL_MS = 1000;
  public static final int DEFAULT_SUBSCRIPTION_SAMPLING_INTERVAL_MS = 1000;
  public static final int DEFAULT_SUBSCRIPTION_QUEUE_SIZE = 10;
  public static final boolean DEFAULT_SUBSCRIPTION_DISCARD_OLDEST = true;

  private Integer pullIntervalMilliSeconds;
  private String incompleteEventStrategy;
  private OpcUaNamingStrategy namingStrategy;
  private int subscriptionPublishingIntervalMs = DEFAULT_SUBSCRIPTION_PUBLISHING_INTERVAL_MS;
  private int subscriptionSamplingIntervalMs = DEFAULT_SUBSCRIPTION_SAMPLING_INTERVAL_MS;
  private int subscriptionQueueSize = DEFAULT_SUBSCRIPTION_QUEUE_SIZE;
  private boolean subscriptionDiscardOldest = DEFAULT_SUBSCRIPTION_DISCARD_OLDEST;

  public Integer getPullIntervalMilliSeconds() {
    return pullIntervalMilliSeconds;
  }

  public void setPullIntervalMilliSeconds(Integer pullIntervalMilliSeconds) {
    this.pullIntervalMilliSeconds = pullIntervalMilliSeconds;
  }

  public boolean inPullMode() {
    return pullIntervalMilliSeconds != null;
  }

  public String getIncompleteEventStrategy() {
    return incompleteEventStrategy;
  }

  public void setIncompleteEventStrategy(String incompleteEventStrategy) {
    this.incompleteEventStrategy = incompleteEventStrategy;
  }

  public OpcUaNamingStrategy getNamingStrategy() {
    return namingStrategy;
  }

  public void setNamingStrategy(OpcUaNamingStrategy namingStrategy) {
    this.namingStrategy = namingStrategy;
  }

  public int getSubscriptionPublishingIntervalMs() {
    return subscriptionPublishingIntervalMs;
  }

  public void setSubscriptionPublishingIntervalMs(int subscriptionPublishingIntervalMs) {
    this.subscriptionPublishingIntervalMs = subscriptionPublishingIntervalMs;
  }

  public int getSubscriptionSamplingIntervalMs() {
    return subscriptionSamplingIntervalMs;
  }

  public void setSubscriptionSamplingIntervalMs(int subscriptionSamplingIntervalMs) {
    this.subscriptionSamplingIntervalMs = subscriptionSamplingIntervalMs;
  }

  public int getSubscriptionQueueSize() {
    return subscriptionQueueSize;
  }

  public void setSubscriptionQueueSize(int subscriptionQueueSize) {
    this.subscriptionQueueSize = subscriptionQueueSize;
  }

  public boolean isSubscriptionDiscardOldest() {
    return subscriptionDiscardOldest;
  }

  public void setSubscriptionDiscardOldest(boolean subscriptionDiscardOldest) {
    this.subscriptionDiscardOldest = subscriptionDiscardOldest;
  }

}
