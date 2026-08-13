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

package org.apache.streampipes.client.api.external;

public final class ExternalRequestConfig {

  private static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 10_000;
  private static final int DEFAULT_RESPONSE_TIMEOUT_MILLIS = 30_000;
  private static final long DEFAULT_MAX_RESPONSE_BYTES = 10L * 1024 * 1024;

  private final int connectTimeoutMillis;
  private final int responseTimeoutMillis;
  private final long maxResponseBytes;

  private ExternalRequestConfig(Builder builder) {
    this.connectTimeoutMillis = requirePositiveInt(builder.connectTimeoutMillis, "connectTimeoutMillis");
    this.responseTimeoutMillis = requirePositiveInt(builder.responseTimeoutMillis, "responseTimeoutMillis");
    this.maxResponseBytes = requirePositive(builder.maxResponseBytes, "maxResponseBytes");
  }

  public static ExternalRequestConfig defaults() {
    return builder().build();
  }

  public static Builder builder() {
    return new Builder();
  }

  public int getConnectTimeoutMillis() {
    return connectTimeoutMillis;
  }

  public int getResponseTimeoutMillis() {
    return responseTimeoutMillis;
  }

  public long getMaxResponseBytes() {
    return maxResponseBytes;
  }

  private static long requirePositive(long value, String name) {
    if (value <= 0) {
      throw new IllegalArgumentException(name + " must be greater than zero");
    }
    return value;
  }

  private static int requirePositiveInt(int value, String name) {
    if (value <= 0) {
      throw new IllegalArgumentException(name + " must be greater than zero");
    }
    return value;
  }

  public static final class Builder {

    private int connectTimeoutMillis = DEFAULT_CONNECT_TIMEOUT_MILLIS;
    private int responseTimeoutMillis = DEFAULT_RESPONSE_TIMEOUT_MILLIS;
    private long maxResponseBytes = DEFAULT_MAX_RESPONSE_BYTES;

    private Builder() {
    }

    public Builder connectTimeoutMillis(int connectTimeoutMillis) {
      this.connectTimeoutMillis = connectTimeoutMillis;
      return this;
    }

    public Builder responseTimeoutMillis(int responseTimeoutMillis) {
      this.responseTimeoutMillis = responseTimeoutMillis;
      return this;
    }

    public Builder maxResponseBytes(long maxResponseBytes) {
      this.maxResponseBytes = maxResponseBytes;
      return this;
    }

    public ExternalRequestConfig build() {
      return new ExternalRequestConfig(this);
    }
  }
}
