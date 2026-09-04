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

package org.apache.streampipes.dataexplorer.query;

import org.apache.streampipes.dataexplorer.api.IDatasetMeasurementCounter;
import org.apache.streampipes.model.dataset.DatasetMeasure;
import org.apache.streampipes.model.dataset.SpQueryResult;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.PropertyScope;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public abstract class DatasetMeasurementCounter implements IDatasetMeasurementCounter {

  private static final Logger LOG = LoggerFactory.getLogger(DatasetMeasurementCounter.class);

  protected final List<DatasetMeasure> allMeasurements;
  protected final List<String> measurementNames;
  protected final int daysBack;

  public DatasetMeasurementCounter(
      List<DatasetMeasure> allMeasurements,
      List<String> measurementNames,
      int daysBack
  ) {
    this.allMeasurements = allMeasurements;
    this.measurementNames = measurementNames;
    this.daysBack = daysBack;
  }

  @Override
  public Map<String, Integer> countMeasurementSizes() {

    // create async futures so that count queries can be executed parallel
    Map<String, CompletableFuture<Integer>> countQueriesFutures = measurementNames
        .stream()
        .map(this::getMeasure)
        .filter(Objects::nonNull)
        .collect(Collectors.toMap(
                     DatasetMeasure::getMeasureName,
                     this::createQueryAsAsyncFuture
                 )
        );

    return getQueryResults(countQueriesFutures);
  }

  /**
   * Retrieves the {@link DatasetMeasure} with the specified measure name from the collection of all measurements.
   *
   * @param measureName The name of the measure to retrieve.
   * @return The DatasetMeasure corresponding to the provided measure name, or null if no such measure is found.
   */
  private DatasetMeasure getMeasure(String measureName) {
    return allMeasurements
        .stream()
        .filter(m -> m.getMeasureName()
                      .equals(measureName))
        .findFirst()
        .orElse(null);
  }

  /**
   * Retrieves the results of asynchronous count queries from the given CompletableFuture map.
   *
   * @param queryFutures A Map containing the futures of
   *                     asynchronous count queries mapped by their respective keys.
   * @return A Map representing the results of the queries, where each key corresponds to
   * a measure name and the value is the count result.
   */
  private Map<String, Integer> getQueryResults(Map<String, CompletableFuture<Integer>> queryFutures) {
    Map<String, Integer> resultPerMeasure = new HashMap<>();
    queryFutures.forEach((key, value) -> {
      try {
        resultPerMeasure.put(key, value.get());
      } catch (InterruptedException | ExecutionException e) {
        LOG.error("Async execution of count query failed: {}", e.getMessage());
        resultPerMeasure.put(key, 0);
      }
    });

    return resultPerMeasure;
  }

  /**
   * Retrieves the runtime name of the first measurement property from the event schema of the given DatasetMeasure.
   *
   * @param measure The {@link DatasetMeasure} from which to retrieve the first measurement property.
   * @return The runtime name of the first measurement property, or null if no such property is found.
   */
  protected String getFirstMeasurementProperty(DatasetMeasure measure) {
    var propertyRuntimeName = measure
        .getEventSchema()
        .getEventProperties()
        .stream()
        .filter(this::isStoredAsField)
        .map(EventProperty::getRuntimeName)
        .findFirst()
        .orElse(null);

    if (propertyRuntimeName == null) {
      LOG.error("No countable property was found in the event schema for measure {}", measure.getMeasureName());
    }

    return propertyRuntimeName;
  }

  private boolean isStoredAsField(EventProperty eventProperty) {
    return !(eventProperty instanceof EventPropertyPrimitive)
        || !PropertyScope.DIMENSION_PROPERTY.name().equals(eventProperty.getPropertyScope());
  }

  protected Integer extractResult(SpQueryResult queryResult, String fieldName) {
    return (
        (Double) (
            queryResult.getAllDataSeries()
                       .get(0)
                       .getRows()
                       .get(0)
                       .get(queryResult.getHeaders()
                                       .indexOf(fieldName))
        )
    ).intValue();
  }

  /**
   * Create the count query for the given DatasetMeasure and return it as a {@link CompletableFuture}.
   */
  protected abstract CompletableFuture<Integer> createQueryAsAsyncFuture(DatasetMeasure measure);
}
