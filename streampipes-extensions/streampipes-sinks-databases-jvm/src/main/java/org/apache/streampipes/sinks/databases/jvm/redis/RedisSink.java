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
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.SinkParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataSink;

public class RedisSink extends StreamPipesDataSink {

  private static final String EVENT_PRIMARY_KEY = "event_pk";

  private static final String EVENT_KEY_AUTO_INCREMENT = "event_key_ai";

  private static final String EVENT_TTL_KEY = "event_ttl";

  private static final String REDIS_HOST_KEY = "redis_host";

  private static final String REDIS_PORT_KEY = "redis_port";

  private static final String REDIS_PASSWORD_KEY = "redis_pw";

  private static final String REDIS_CLIENT_KEY = "redis_client";

  private static final String REDIS_INDEX_KEY = "redis_index";

  private static final String REDIS_POOL_MAX_ACTIVE_KEY = "redis_pool_max_active";

  private static final String REDIS_POOL_MAX_IDLE_KEY = "redis_pool_max_idle";

  private static final String REDIS_POOL_MAX_WAIT_KEY = "redis_pool_max_wait";

  private static final String REDIS_POOL_TIMEOUT_KEY = "redis_pool_timeout";

  private Redis redis;

  @Override
  public DataSinkDescription declareModel() {
    return DataSinkBuilder.create("org.apache.streampipes.sinks.databases.jvm.redis")
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .category(DataSinkType.DATABASE)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithUnaryMapping(EpRequirements.anyProperty(),
                Labels.withId(EVENT_PRIMARY_KEY),
                PropertyScope.NONE).build())
        .requiredSingleValueSelection(Labels.withId(EVENT_KEY_AUTO_INCREMENT), Options.from("False", "True"))
        .requiredTextParameter(Labels.withId(REDIS_HOST_KEY))
        .requiredIntegerParameter(Labels.withId(REDIS_PORT_KEY), 6379)
        .requiredIntegerParameter(Labels.withId(EVENT_TTL_KEY), -1)
        // TODO: Use this after optional parameters implementation
        //  .requiredSecret(Labels.withId(REDIS_PASSWORD_KEY))
        //  .requiredTextParameter(Labels.withId(REDIS_CLIENT_KEY))
        .requiredIntegerParameter(Labels.withId(REDIS_INDEX_KEY), -1)
        .requiredIntegerParameter(Labels.withId(REDIS_POOL_MAX_ACTIVE_KEY), 8)
        .requiredIntegerParameter(Labels.withId(REDIS_POOL_MAX_IDLE_KEY), 8)
        .requiredIntegerParameter(Labels.withId(REDIS_POOL_MAX_WAIT_KEY), -1)
        .requiredIntegerParameter(Labels.withId(REDIS_POOL_TIMEOUT_KEY), 2000)
        .build();
  }

  @Override
  public void onInvocation(SinkParams parameters, EventSinkRuntimeContext runtimeContext) throws SpRuntimeException {
    var extractor = parameters.extractor();
    String redisHost = extractor.singleValueParameter(REDIS_HOST_KEY, String.class);
    Integer redisPort = extractor.singleValueParameter(REDIS_PORT_KEY, Integer.class);
    String primaryKey = extractor.mappingPropertyValue(EVENT_PRIMARY_KEY);
    Boolean autoIncrement = Boolean.valueOf(extractor.selectedSingleValue(EVENT_KEY_AUTO_INCREMENT, String.class));
    Integer ttl = extractor.singleValueParameter(EVENT_TTL_KEY, Integer.class);
    // TODO: Use this after optional parameters implementation
    //        String redisPassword = extractor.secretValue(REDIS_PASSWORD_KEY);
    //        String redisClient = extractor.singleValueParameter(REDIS_CLIENT_KEY, String.class);
    Integer redisIndex = extractor.singleValueParameter(REDIS_INDEX_KEY, Integer.class);
    Integer redisPoolMaxActive = extractor.singleValueParameter(REDIS_POOL_MAX_ACTIVE_KEY, Integer.class);
    Integer redisPoolMaxIdle = extractor.singleValueParameter(REDIS_POOL_MAX_IDLE_KEY, Integer.class);
    Integer redisPoolMaxWait = extractor.singleValueParameter(REDIS_POOL_MAX_WAIT_KEY, Integer.class);
    Integer redisPoolTimeout = extractor.singleValueParameter(REDIS_POOL_TIMEOUT_KEY, Integer.class);
    String redisPassword = "";
    String redisClient = "";

    RedisParameters
        params = new RedisParameters(primaryKey, autoIncrement, ttl, redisHost,
        redisPort, redisPassword, redisClient, redisIndex, redisPoolMaxActive, redisPoolMaxIdle,
        redisPoolMaxWait, redisPoolTimeout);

    this.redis = new Redis();
    redis.onInvocation(params);
  }

  @Override
  public void onEvent(Event event) throws SpRuntimeException {
    redis.onEvent(event);
  }

  @Override
  public void onDetach() throws SpRuntimeException {
    redis.onDetach();
  }
}
