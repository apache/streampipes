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

package org.apache.streampipes.extensions.connectors.influx.sink;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.extensions.connectors.influx.shared.InfluxConfigs;
import org.apache.streampipes.logging.api.Logger;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.SinkParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataSink;

import static org.apache.streampipes.extensions.connectors.influx.shared.InfluxKeys.BATCH_INTERVAL_ACTIONS_KEY;
import static org.apache.streampipes.extensions.connectors.influx.shared.InfluxKeys.DATABASE_MEASUREMENT_KEY;
import static org.apache.streampipes.extensions.connectors.influx.shared.InfluxKeys.MAX_FLUSH_DURATION_KEY;
import static org.apache.streampipes.extensions.connectors.influx.shared.InfluxKeys.TIMESTAMP_MAPPING_KEY;

public class InfluxDbSink extends StreamPipesDataSink {


  private InfluxDbClient influxDbClient;

  private static Logger log;

  @Override
  public void onInvocation(SinkParams parameters,
                           EventSinkRuntimeContext runtimeContext) throws SpRuntimeException {

    var extractor = parameters.extractor();
    log = parameters.getModel().getLogger(InfluxDbSink.class);

    var connectionSettings = InfluxConfigs.fromExtractor(extractor);
    String measureName = extractor.singleValueParameter(DATABASE_MEASUREMENT_KEY, String.class);
    measureName = InfluxDbSink.prepareString(measureName);
    String timestampField = extractor.mappingPropertyValue(TIMESTAMP_MAPPING_KEY);
    Integer batchSize = extractor.singleValueParameter(BATCH_INTERVAL_ACTIONS_KEY, Integer.class);
    Integer flushDuration = extractor.singleValueParameter(MAX_FLUSH_DURATION_KEY, Integer.class);


    this.influxDbClient = new InfluxDbClient(
        connectionSettings,
        measureName,
        timestampField,
        batchSize,
        flushDuration
    );
  }

  @Override
  public void onEvent(Event event) {
    try {
      influxDbClient.save(event);
    } catch (SpRuntimeException e) {
      log.error(e.getMessage());
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {
    influxDbClient.stop();
  }

  public static String prepareString(String s) {
    return s.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
  }

  @Override
  public DataSinkDescription declareModel() {
    var builder = DataSinkBuilder.create("org.apache.streampipes.sinks.databases.jvm.influxdb")
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .category(DataSinkType.DATABASE);

    InfluxConfigs.appendSharedInfluxConfig(builder);

    builder.requiredStream(StreamRequirementsBuilder.create().requiredPropertyWithUnaryMapping(
            EpRequirements.timestampReq(),
            Labels.withId(TIMESTAMP_MAPPING_KEY),
            PropertyScope.NONE).build())
        .requiredIntegerParameter(Labels.withId(BATCH_INTERVAL_ACTIONS_KEY))
        .requiredIntegerParameter(Labels.withId(MAX_FLUSH_DURATION_KEY), 2000);

    return builder.build();
  }
}
