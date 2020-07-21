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
package org.apache.streampipes.processors.imageprocessing.jvm.processor.imageenrichment;

import static org.apache.streampipes.processors.imageprocessing.jvm.processor.commons.RequiredBoxStream.IMAGE_PROPERTY;

import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.processors.imageprocessing.jvm.processor.commons.RequiredBoxStream;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

public class ImageEnrichmentController extends StandaloneEventProcessingDeclarer<ImageEnrichmentParameters> {

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processor.imageclassification.jvm.image-enricher")
            .withAssets(Assets.DOCUMENTATION, Assets.ICON)
            .withLocales(Locales.EN)
            .category(DataProcessorType.FILTER)
            .requiredStream(RequiredBoxStream.getBoxStream())
            .outputStrategy(OutputStrategies.fixed(
                    EpProperties.stringEp(Labels.empty(), "image", "https://image.com")
            ))
            .build();
  }

  @Override
  public ConfiguredEventProcessor<ImageEnrichmentParameters> onInvocation(DataProcessorInvocation dataProcessorInvocation, ProcessingElementParameterExtractor extractor) {
    String imageProperty = extractor.mappingPropertyValue(IMAGE_PROPERTY);
    String boxArray = extractor.mappingPropertyValue(RequiredBoxStream.BOX_ARRAY_PROPERTY);

    ImageEnrichmentParameters params = new ImageEnrichmentParameters(dataProcessorInvocation, imageProperty,
            boxArray, "box_width", "box_height", "box_x", "box_y", "score", "classesindex");

    return new ConfiguredEventProcessor<>(params, ImageEnricher::new);


  }


}
