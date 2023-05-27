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

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.runtime.Event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

public class Redis {

  private static final String EVENT_PREFIX = "sp:event:";

  private static final String EVENT_COUNT = "sp:events";

  private static JedisPool jedisPool = null;

  private String primaryKey;

  private Boolean autoIncrement;

  private String password;

  private String clientName;

  private Integer index;

  private Integer ttl;

  public void onInvocation(RedisParameters parameters) {
    if (jedisPool == null) {
      initialPool(parameters);
    }
    primaryKey = parameters.getPrimaryKey();
    autoIncrement = parameters.isAutoIncrement();
    password = parameters.getRedisPassword();
    clientName = parameters.getRedisClient();
    index = parameters.getRedisIndex();
    ttl = parameters.getTTL();
  }

  public void onEvent(Event event) throws SpRuntimeException {
    try (Jedis jedis = getJedis()) {
      final String eventKey = getEventKey(event, autoIncrement ? jedis.incr(EVENT_COUNT) : 0L);
      jedis.set(eventKey, getEventValue(event));
      if (ttl > -1) {
        jedis.expire(eventKey, ttl);
      }
    } catch (SpRuntimeException e) {
      throw e;
    } catch (Exception ex) {
      throw new SpRuntimeException("Could not persist event to redis", ex);
    }
  }

  public void onDetach() throws SpRuntimeException {
    if (jedisPool != null && !jedisPool.isClosed()) {
      jedisPool.close();
    }
    jedisPool = null;
  }

  private void initialPool(RedisParameters parameters) {
    JedisPoolConfig config = new JedisPoolConfig();
    config.setMaxTotal(parameters.getRedisPoolMaxActive());
    config.setMaxIdle(parameters.getRedisPoolMaxIdle());
    config.setMaxWaitMillis(parameters.getRedisPoolMaxWait());
    config.setTestWhileIdle(false);
    config.setTestOnBorrow(false);
    config.setTestOnReturn(false);
    jedisPool = new JedisPool(config, parameters.getRedisHost(),
        parameters.getRedisPort(), parameters.getRedisPoolTimeout());
  }

  private Jedis getJedis() throws SpRuntimeException {
    Jedis jedis = jedisPool.getResource();
    try {
      jedis.connect();
      if (StringUtils.isNotBlank(password)) {
        jedis.auth(password);
      }
      if (StringUtils.isNotBlank(clientName)) {
        jedis.clientSetname(clientName);
      }
      if (index > -1) {
        jedis.select(index);
      }
    } catch (JedisException e) {
      jedis.close();
      throw new SpRuntimeException("Could not connect to redis", e);
    }
    return jedis;
  }

  private String getEventKey(Event event, Long count) {
    if (autoIncrement) {
      return EVENT_PREFIX + count;
    } else {
      String value = event.getFieldBySelector(primaryKey).getAsPrimitive().getAsString();
      return EVENT_PREFIX + value;
    }
  }

  private String getEventValue(Event event) throws SpRuntimeException {
    try {
      return new ObjectMapper().writeValueAsString(event.getRaw());
    } catch (JsonProcessingException e) {
      throw new SpRuntimeException("Could not convert event to JSON", e);
    }
  }
}
