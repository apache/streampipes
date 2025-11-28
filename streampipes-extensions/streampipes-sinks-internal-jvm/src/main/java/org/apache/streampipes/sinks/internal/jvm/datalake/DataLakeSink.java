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

import org.apache.streampipes.client.api.IStreamPipesClient;
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataexplorer.TimeSeriesStore;
import org.apache.streampipes.dataexplorer.api.IDataExplorerSchemaManagement;
import org.apache.streampipes.dataexplorer.management.DataExplorerDispatcher;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataSink;
import org.apache.streampipes.extensions.api.pe.config.IDataSinkConfiguration;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataSinkParameters;
import org.apache.streampipes.extensions.api.runtime.SupportsRuntimeConfig;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.datalake.DataLakeMeasureSchemaUpdateStrategy;
import org.apache.streampipes.model.datalake.RetentionTimeConfig;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableAnyStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.builder.sink.DataSinkConfiguration;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DataLakeSink implements IStreamPipesDataSink, SupportsRuntimeConfig {

  private static final String DATABASE_MEASUREMENT_KEY = "db_measurement";
  private static final String TIMESTAMP_MAPPING_KEY = "timestamp_mapping";
  public static final String SCHEMA_UPDATE_KEY = "schema_update";
  public static final String DIMENSIONS_KEY = "dimensions_selection";
  public static final String IGNORE_DUPLICATES_KEY = "ignore_duplicates";

  public static final String SCHEMA_UPDATE_OPTION = "Update schema";

  public static final String EXTEND_EXISTING_SCHEMA_OPTION = "Extend existing schema";
  private static final Logger LOG = LoggerFactory.getLogger(DataLakeSink.class);

  private TimeSeriesStore timeSeriesStore;


  @Override
  public IDataSinkConfiguration declareConfig() {
    return DataSinkConfiguration.create(
        DataLakeSink::new,
        DataSinkBuilder
            .create("org.apache.streampipes.sinks.internal.jvm.datalake", 2)
            .withLocales(Locales.EN)
            .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
            .category(DataSinkType.INTERNAL)
            .requiredStream(StreamRequirementsBuilder
                                .create()
                                .requiredPropertyWithUnaryMapping(
                                    EpRequirements.timestampReq(),
                                    Labels.withId(TIMESTAMP_MAPPING_KEY),
                                    PropertyScope.NONE
                                )
                                .build())
            .requiredTextParameter(Labels.withId(DATABASE_MEASUREMENT_KEY))
            .requiredSingleValueSelection(
                Labels.withId(SCHEMA_UPDATE_KEY),
                Options.from(SCHEMA_UPDATE_OPTION, EXTEND_EXISTING_SCHEMA_OPTION)
            )
            .requiredMultiValueSelectionFromContainer(Labels.withId(DIMENSIONS_KEY))
            .requiredSlideToggle(Labels.withId(IGNORE_DUPLICATES_KEY), false)
            .build()
    );
  }

  @Override
  public void onPipelineStarted(IDataSinkParameters parameters, EventSinkRuntimeContext runtimeContext) {

    var extractor = parameters.extractor();
    var timestampField = extractor.mappingPropertyValue(TIMESTAMP_MAPPING_KEY);
    var measureName = extractor.singleValueParameter(DATABASE_MEASUREMENT_KEY, String.class);
    var dimensions = extractor.selectedMultiValues(DIMENSIONS_KEY, String.class);
    var ignoreDuplicates = extractor.slideToggleValue(IGNORE_DUPLICATES_KEY);
    var eventSchema = this.assignPropertyScopesBasedOnDimensions(
        parameters.getInputSchemaInfos()
                  .get(0)
                  .getEventSchema(),
        dimensions
    );

    this.ensureMeasurementPropertiesExist(eventSchema);

    var retentionTimeConfig = getRetentionTime(measureName, runtimeContext.getStreamPipesClient());

    var measure = new DataLakeMeasure(measureName, timestampField, eventSchema, retentionTimeConfig);

    var schemaUpdateOptionString = extractor.selectedSingleValue(SCHEMA_UPDATE_KEY, String.class);

    if (schemaUpdateOptionString.equals(EXTEND_EXISTING_SCHEMA_OPTION)) {
      measure.setSchemaUpdateStrategy(DataLakeMeasureSchemaUpdateStrategy.EXTEND_EXISTING_SCHEMA);
    } else {
      measure.setSchemaUpdateStrategy(DataLakeMeasureSchemaUpdateStrategy.UPDATE_SCHEMA);
    }

    measure = new DataExplorerDispatcher().getDataExplorerManager()
                                          .getMeasurementSanitizer(runtimeContext.getStreamPipesClient(), measure)
                                          .sanitizeAndRegister();

    this.timeSeriesStore = new TimeSeriesStore(
        new DataExplorerDispatcher().getDataExplorerManager()
                                    .getTimeseriesStorage(measure, ignoreDuplicates),
        measure,
        Environments.getEnvironment(),
        true
    );

  }

  @Override
  public void onEvent(Event event) throws SpRuntimeException {
    this.timeSeriesStore.onEvent(event);
  }

  @Override
  public void onPipelineStopped() {
    this.timeSeriesStore.close();
  }

  @Override
  public StaticProperty resolveConfiguration(
      String staticPropertyInternalName,
      IStaticPropertyExtractor extractor
  ) {
    var staticProperty = extractor.getStaticPropertyByName(DIMENSIONS_KEY, RuntimeResolvableAnyStaticProperty.class);
    var inputFields = extractor.getInputEventProperties(0);
    new DataLakeDimensionProvider().applyOptions(inputFields, staticProperty);
    return staticProperty;
  }

  private RetentionTimeConfig getRetentionTime(String measureName, IStreamPipesClient client){

    IDataExplorerSchemaManagement dataExplorerSchemaManagement = new DataExplorerDispatcher().getDataExplorerManager()
        .getSchemaManagement();

    var originalMeasure = dataExplorerSchemaManagement.getExistingMeasureByName(measureName);

    RetentionTimeConfig retentionTime = null;

    if (originalMeasure.isPresent()){
      retentionTime = originalMeasure.get().getRetentionTime();
    }
  return retentionTime;
  }

  /**
   * Assigns property scopes to event properties based on the provided list of dimension names.
   *
   * @param eventSchema The event schema whose properties will be updated.
   * @param dimensions  The list of property runtime names to be marked as dimensions.
   * @return A new {@link EventSchema} with updated property scopes.
   */
  private EventSchema assignPropertyScopesBasedOnDimensions(EventSchema eventSchema, List<String> dimensions) {

    var eventProperties = eventSchema
        .getEventProperties()
        .stream()
        .peek(ep -> {
          // Set all properties to DIMENSION_PROPERTY when seleted in dimensions
          if (dimensions.contains(ep.getRuntimeName())) {
            LOG.info("Using {} as dimension", ep.getRuntimeName());
            ep.setPropertyScope(PropertyScope.DIMENSION_PROPERTY.name());
          }
        })
        .peek(ep -> {
          // Remova all dimensions from DIMENSION_PROPERTY scope if not part of dimensions
          if (PropertyScope.DIMENSION_PROPERTY.name()
                .equals(ep.getPropertyScope())) {
            if (!dimensions.contains(ep.getRuntimeName())) {
              ep.setPropertyScope(PropertyScope.MEASUREMENT_PROPERTY.name());
            }
          }
        })
        .toList();

    return new EventSchema(eventProperties);
  }

  /**
   * Validates that not all properties in the given {@link EventSchema} are marked as dimensions.
   * If that is the case, influx db is not able to query the data correclty.
   * This validation is due to the usage of influxdb, if we change the dabtabase in the future, we can remove this
   * validation.
   *
   * @param eventSchema The event schema to validate.
   * @throws SpRuntimeException if all properties are marked as dimensions.
   */
  private void ensureMeasurementPropertiesExist(EventSchema eventSchema) throws SpRuntimeException {
    int amountOfPropertiesWithoutTimestamp = eventSchema.getEventProperties()
                                                        .size() - 1;
    long amountOfDimensionProperties = eventSchema
        .getEventProperties()
        .stream()
        .filter(ep -> PropertyScope.DIMENSION_PROPERTY.name()
                                                      .equals(ep.getPropertyScope()))
        .count();

    if (amountOfPropertiesWithoutTimestamp > 0 && amountOfDimensionProperties == amountOfPropertiesWithoutTimestamp) {
      throw new SpRuntimeException(
          "Cannot store data: All properties are marked as dimensions. "
              + "At least one property must be a measurement value. Please edit the pipeline to fix this problem.");

    }
  }
}
