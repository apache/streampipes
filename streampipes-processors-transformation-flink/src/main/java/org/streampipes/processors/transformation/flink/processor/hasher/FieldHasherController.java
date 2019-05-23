/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

package org.streampipes.processors.transformation.flink.processor.hasher;

import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.processors.transformation.flink.processor.hasher.algorithm.HashAlgorithmType;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.Locales;
import org.streampipes.sdk.helpers.Options;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.sdk.utils.Assets;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;

public class FieldHasherController extends FlinkDataProcessorDeclarer<FieldHasherParameters> {

  private static final String HASH_PROPERTIES = "property-mapping";
  private static final String HASH_ALGORITHM = "hash-algorithm";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.streampipes.processors.transformation.flink.fieldhasher")
            .withLocales(Locales.EN)
            .withAssets(Assets.DOCUMENTATION, Assets.ICON)
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredPropertyWithUnaryMapping(EpRequirements.anyProperty(), Labels.withId
                            (HASH_PROPERTIES), PropertyScope.NONE)
                    .build())
            .requiredSingleValueSelection(Labels.withId(HASH_ALGORITHM),
                    Options.from("SHA1", "SHA2", "MD5"))
            .outputStrategy(OutputStrategies.keep())
            .supportedFormats(SupportedFormats.jsonFormat())
            .supportedProtocols(SupportedProtocols.kafka(), SupportedProtocols.jms())
            .build();
  }

  @Override
  public FlinkDataProcessorRuntime<FieldHasherParameters> getRuntime(
          DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {
    String propertyName = extractor.mappingPropertyValue(HASH_PROPERTIES);

    HashAlgorithmType hashAlgorithmType = HashAlgorithmType.valueOf(extractor.selectedSingleValue(HASH_ALGORITHM, String.class));

    return new FieldHasherProgram(
            new FieldHasherParameters(graph, propertyName, hashAlgorithmType));
  }

}
