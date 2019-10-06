/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * you may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.streampipes.processors.transformation.jvm.processor.csvmetadata;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.container.api.ResolvesContainerProvidedOptions;
import org.streampipes.container.api.ResolvesContainerProvidedOutputStrategy;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.model.staticproperty.Option;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.Locales;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.utils.Assets;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

import java.util.List;

public class CsvMetadataEnrichmentController
        extends StandaloneEventProcessingDeclarer<CsvMetadataEnrichmentParameters>
        implements ResolvesContainerProvidedOptions,
        ResolvesContainerProvidedOutputStrategy<DataProcessorInvocation, ProcessingElementParameterExtractor> {

  private static final String MAPPING_FIELD_KEY = "mapping-field";
  private static final String CSV_FILE_KEY = "csv-file";
  private static final String FIELDS_TO_APPEND_KEY = "field-to-append";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.streampipes.processors.transformation.jvm"
            + ".csvmetadata")
            .withLocales(Locales.EN)
            .withAssets(Assets.DOCUMENTATION)
            .requiredStream(StreamRequirementsBuilder.create()
                    .requiredPropertyWithUnaryMapping(
                            EpRequirements.anyProperty(),
                            Labels.withId(MAPPING_FIELD_KEY),
                            PropertyScope.NONE)
                    .build())
            .requiredFile(Labels.withId(CSV_FILE_KEY))
            .requiredMultiValueSelectionFromContainer(Labels.withId(FIELDS_TO_APPEND_KEY))
            .outputStrategy(OutputStrategies.customTransformation())
            .build();
  }

  @Override
  public ConfiguredEventProcessor<CsvMetadataEnrichmentParameters> onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {
    return null;
  }

  @Override
  public List<Option> resolveOptions(String requestId, StaticPropertyExtractor parameterExtractor) {
    return null;
  }

  @Override
  public EventSchema resolveOutputStrategy(DataProcessorInvocation processingElement, ProcessingElementParameterExtractor parameterExtractor) throws SpRuntimeException {
    return null;
  }
}
