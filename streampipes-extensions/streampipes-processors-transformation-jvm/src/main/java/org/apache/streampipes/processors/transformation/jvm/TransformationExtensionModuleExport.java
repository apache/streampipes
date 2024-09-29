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
package org.apache.streampipes.processors.transformation.jvm;

import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.declarer.IExtensionModuleExport;
import org.apache.streampipes.extensions.api.migration.IModelMigrator;
import org.apache.streampipes.extensions.api.pe.IStreamPipesPipelineElement;
import org.apache.streampipes.processors.transformation.jvm.processor.array.count.CountArrayProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.array.split.SplitArrayProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.counter.BooleanCounterProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.edge.SignalEdgeFilterProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.inverter.BooleanInverterProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.logical.BooleanOperatorProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.state.BooleanToStateProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.timekeeping.BooleanTimekeepingProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.timer.BooleanTimerProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.csvmetadata.CsvMetadataEnrichmentProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.datetime.DateTimeFromStringProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.fieldrename.FiledRenameProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.hasher.FieldHasherProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.mapper.FieldMapperProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.measurementconverter.MeasurementUnitConverterProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.round.RoundProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.state.labeler.number.NumberLabelerProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.staticmetadata.StaticMetaDataEnrichmentProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.stringoperator.counter.StringCounterProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.stringoperator.state.StringToStateProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.stringoperator.timer.StringTimerProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.task.TaskDurationProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.timestampextractor.TimestampExtractorProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.transformtoboolean.TransformToBooleanProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.value.change.ChangedValueDetectionProcessor;
import org.apache.streampipes.processors.transformation.jvm.processor.value.duration.CalculateDurationProcessor;

import java.util.Collections;
import java.util.List;

public class TransformationExtensionModuleExport implements IExtensionModuleExport {

  @Override
  public List<StreamPipesAdapter> adapters() {
    return Collections.emptyList();
  }

  @Override
  public List<IStreamPipesPipelineElement<?>> pipelineElements() {
    return List.of(new CountArrayProcessor(), new SplitArrayProcessor(), new CalculateDurationProcessor(),
            new ChangedValueDetectionProcessor(), new TimestampExtractorProcessor(), new BooleanCounterProcessor(),
            new BooleanInverterProcessor(), new DateTimeFromStringProcessor(), new BooleanTimekeepingProcessor(),
            new BooleanTimerProcessor(), new CsvMetadataEnrichmentProcessor(), new FieldHasherProcessor(),
            new FieldMapperProcessor(), new MeasurementUnitConverterProcessor(), new TaskDurationProcessor(),
            new TransformToBooleanProcessor(), new StaticMetaDataEnrichmentProcessor(), new StringTimerProcessor(),
            new SignalEdgeFilterProcessor(), new BooleanToStateProcessor(), new NumberLabelerProcessor(),
            new StringToStateProcessor(), new StringCounterProcessor(), new BooleanOperatorProcessor(),
            new FiledRenameProcessor(), new RoundProcessor());
  }

  @Override
  public List<IModelMigrator<?, ?>> migrators() {
    return Collections.emptyList();
  }
}
