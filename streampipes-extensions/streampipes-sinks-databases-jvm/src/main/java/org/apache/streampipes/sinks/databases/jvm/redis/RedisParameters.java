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

public class RedisParameters {

  private final String primaryKey;

  private final Boolean autoIncrement;

  private final Integer ttl;

  private final String redisHost;

  private final Integer redisPort;

  private final String redisPassword;

  private final String redisClient;

  private final Integer redisIndex;

  private final Integer redisPoolMaxActive;

  private final Integer redisPoolMaxIdle;

  private final Integer redisPoolMaxWait;

  private final Integer redisPoolTimeout;

  public RedisParameters(String primaryKey,
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
