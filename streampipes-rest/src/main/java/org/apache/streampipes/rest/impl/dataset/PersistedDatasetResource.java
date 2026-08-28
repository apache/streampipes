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
package org.apache.streampipes.rest.impl.dataset;

import org.apache.streampipes.dataexplorer.influx.sanitize.MeasureNameSanitizer;
import org.apache.streampipes.model.dataset.DatasetMeasure;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.rest.impl.dashboard.AbstractPipelineExtractionResource;
import org.apache.streampipes.rest.security.AuthConstants;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v3/dataset/pipelines")
public class PersistedDatasetResource extends AbstractPipelineExtractionResource<DatasetMeasure> {

  private static final String DATASET_APP_ID = "org.apache.streampipes.sinks.internal.jvm.datalake";
  private static final String MEASURE_FIELD_INTERNAL_NAME = "db_measurement";

  public PersistedDatasetResource(SpResourceManager resourceManager) {
    super(resourceManager);
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.HAS_READ_DATASET_PRIVILEGE)
  @PostFilter("hasPermission(filterObject.pipelineId, 'READ') and hasPermission(filterObject.measureName, 'READ')")
  public List<DatasetMeasure> getPersistedDataStreams() {
    return extract(new ArrayList<>(), DATASET_APP_ID);
  }

  @GetMapping(path = "{pipelineId}/{measureName}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getVisualizablePipelineByPipelineIdAndVisualizationName(
      @PathVariable("pipelineId") String pipelineId,
      @PathVariable("measureName") String measureName) {
    return getPipelineByIdAndFieldValue(DATASET_APP_ID, pipelineId, measureName);
  }

  @Override
  protected DatasetMeasure convert(Pipeline pipeline, DataSinkInvocation sink) {

    var measureName = extractFieldValue(sink, MEASURE_FIELD_INTERNAL_NAME);
    var sanitizedMeasureName = new MeasureNameSanitizer().sanitize(measureName);

    DatasetMeasure measure = new DatasetMeasure();
    measure.setEventSchema(sink.getInputStreams().get(0).getEventSchema());
    measure.setPipelineId(pipeline.getPipelineId());
    measure.setPipelineName(pipeline.getName());
    measure.setMeasureName(sanitizedMeasureName);
    measure.setPipelineIsRunning(pipeline.isRunning());

    return measure;
  }

  @Override
  protected boolean matches(DatasetMeasure measure, String pipelineId, String fieldValue) {
    return measure.getMeasureName().equals(fieldValue);
  }
}
