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
package org.apache.streampipes.sinks.internal.jvm.datalake;

import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataexplorer.TimeSeriesStore;
import org.apache.streampipes.dataexplorer.management.DataExplorerDispatcher;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.datalake.DataLakeMeasureSchemaUpdateStrategy;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.wrapper.params.compat.SinkParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataSink;

public class DataLakeSink extends StreamPipesDataSink {

  private static final String DATABASE_MEASUREMENT_KEY = "db_measurement";
  private static final String TIMESTAMP_MAPPING_KEY = "timestamp_mapping";
  public static final String SCHEMA_UPDATE_KEY = "schema_update";

  public static final String SCHEMA_UPDATE_OPTION = "Update schema";

  public static final String EXTEND_EXISTING_SCHEMA_OPTION = "Extend existing schema";

  private TimeSeriesStore timeSeriesStore;

  @Override
  public DataSinkDescription declareModel() {
    return DataSinkBuilder.create("org.apache.streampipes.sinks.internal.jvm.datalake", 1).withLocales(Locales.EN)
            .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON).category(DataSinkType.INTERNAL)
            .requiredStream(StreamRequirementsBuilder.create()
                    .requiredPropertyWithUnaryMapping(EpRequirements.timestampReq(),
                            Labels.withId(TIMESTAMP_MAPPING_KEY), PropertyScope.NONE)
                    .build())
            .requiredTextParameter(Labels.withId(DATABASE_MEASUREMENT_KEY))
            .requiredSingleValueSelection(Labels.withId(SCHEMA_UPDATE_KEY),
                    Options.from(SCHEMA_UPDATE_OPTION, EXTEND_EXISTING_SCHEMA_OPTION))

            .build();
  }

  @Override
  public void onInvocation(SinkParams parameters, EventSinkRuntimeContext runtimeContext) throws SpRuntimeException {
    var extractor = parameters.extractor();
    var timestampField = extractor.mappingPropertyValue(TIMESTAMP_MAPPING_KEY);
    var measureName = extractor.singleValueParameter(DATABASE_MEASUREMENT_KEY, String.class);
    var eventSchema = parameters.getInputSchemaInfos().get(0).getEventSchema();

    var measure = new DataLakeMeasure(measureName, timestampField, eventSchema);

    var schemaUpdateOptionString = extractor.selectedSingleValue(SCHEMA_UPDATE_KEY, String.class);

    if (schemaUpdateOptionString.equals(EXTEND_EXISTING_SCHEMA_OPTION)) {
      measure.setSchemaUpdateStrategy(DataLakeMeasureSchemaUpdateStrategy.EXTEND_EXISTING_SCHEMA);
    } else {
      measure.setSchemaUpdateStrategy(DataLakeMeasureSchemaUpdateStrategy.UPDATE_SCHEMA);
    }

    measure = new DataExplorerDispatcher().getDataExplorerManager()
            .getMeasurementSanitizer(runtimeContext.getStreamPipesClient(), measure).sanitizeAndRegister();

    this.timeSeriesStore = new TimeSeriesStore(
            new DataExplorerDispatcher().getDataExplorerManager().getTimeseriesStorage(measure), measure,
            Environments.getEnvironment(), true);

  }

  @Override
  public void onEvent(Event event) throws SpRuntimeException {
    this.timeSeriesStore.onEvent(event);
  }

  @Override
  public void onDetach() throws SpRuntimeException {
    this.timeSeriesStore.close();
  }
}
