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

package org.apache.streampipes.sinks.databases.jvm.redis;

import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.wrapper.params.binding.EventSinkBindingParams;

public class RedisParameters extends EventSinkBindingParams {

  private String primaryKey;
  private Boolean autoIncrement;
  private Integer ttl;
  private String redisHost;
  private Integer redisPort;
  private String redisPassword;
  private String redisClient;
  private Integer redisIndex;
  private Integer redisPoolMaxActive;
  private Integer redisPoolMaxIdle;
  private Integer redisPoolMaxWait;
  private Integer redisPoolTimeout;

  public RedisParameters(DataSinkInvocation graph,
                         String primaryKey,
                         Boolean autoIncrement,
                         Integer ttl,
                         String redisHost,
                         Integer redisPort,
                         String redisPassword,
                         String redisClient,
                         Integer redisIndex,
                         Integer redisPoolMaxActive,
                         Integer redisPoolMaxIdle,
                         Integer redisPoolMaxWait,
                         Integer redisPoolTimeout) {
    super(graph);
    this.primaryKey = primaryKey;
    this.autoIncrement = autoIncrement;
    this.ttl = ttl;
    this.redisHost = redisHost;
    this.redisPort = redisPort;
    this.redisPassword = redisPassword;
    this.redisClient = redisClient;
    this.redisIndex = redisIndex;
    this.redisPoolMaxActive = redisPoolMaxActive;
    this.redisPoolMaxIdle = redisPoolMaxIdle;
    this.redisPoolMaxWait = redisPoolMaxWait;
    this.redisPoolTimeout = redisPoolTimeout;
  }

  public String getPrimaryKey() {
    return primaryKey;
  }

  public Boolean isAutoIncrement() {
    return autoIncrement;
  }

  public Integer getTTL() {
    return ttl;
  }

  public String getRedisHost() {
    return redisHost;
  }

  public Integer getRedisPort() {
    return redisPort;
  }

  public String getRedisPassword() {
    return redisPassword;
  }

  public String getRedisClient() {
    return redisClient;
  }

  public Integer getRedisIndex() {
    return redisIndex;
  }

  public Integer getRedisPoolMaxActive() {
    return redisPoolMaxActive;
  }

  public Integer getRedisPoolMaxIdle() {
    return redisPoolMaxIdle;
  }

  public Integer getRedisPoolMaxWait() {
    return redisPoolMaxWait;
  }

  public Integer getRedisPoolTimeout() {
    return redisPoolTimeout;
  }
}
