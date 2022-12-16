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

package org.apache.streampipes.processors.transformation.flink.processor.hasher;

import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.container.config.ConfigExtractor;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.processors.transformation.flink.processor.hasher.algorithm.HashAlgorithmType;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.apache.streampipes.wrapper.flink.FlinkDataProcessorRuntime;

public class FieldHasherController extends FlinkDataProcessorDeclarer<FieldHasherParameters> {

  private static final String HASH_PROPERTIES = "property-mapping";
  private static final String HASH_ALGORITHM = "hash-algorithm";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.transformation.flink.fieldhasher")
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithUnaryMapping(EpRequirements.stringReq(), Labels.withId
                (HASH_PROPERTIES), PropertyScope.NONE)
            .build())
        .requiredSingleValueSelection(Labels.withId(HASH_ALGORITHM),
            Options.from("SHA1", "SHA2", "MD5"))
        .outputStrategy(OutputStrategies.keep())
        .build();
  }

  @Override
  public FlinkDataProcessorRuntime<FieldHasherParameters> getRuntime(DataProcessorInvocation graph,
                                                                     ProcessingElementParameterExtractor extractor,
                                                                     ConfigExtractor configExtractor,
                                                                     StreamPipesClient streamPipesClient) {
    String propertyName = extractor.mappingPropertyValue(HASH_PROPERTIES);

    HashAlgorithmType hashAlgorithmType =
        HashAlgorithmType.valueOf(extractor.selectedSingleValue(HASH_ALGORITHM, String.class));

    return new FieldHasherProgram(
        new FieldHasherParameters(graph, propertyName, hashAlgorithmType), configExtractor, streamPipesClient);
  }

}
