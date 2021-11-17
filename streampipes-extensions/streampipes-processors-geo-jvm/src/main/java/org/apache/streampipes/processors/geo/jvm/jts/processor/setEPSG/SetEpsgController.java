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

package org.apache.streampipes.processors.geo.jvm.jts.processor.setEPSG;

import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.*;
import org.apache.streampipes.sdk.utils.Datatypes;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;
import org.apache.streampipes.sdk.utils.Assets;

public class SetEpsgController extends StandaloneEventProcessingDeclarer<SetEpsgParameter> {

  public final static String EPA_NAME = "EPSG Enricher";

  public final static String EPSG_KEY = "epsg-key";
  public final static String EPSG_RUNTIME = "epsg";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder
        .create("org.apache.streampipes.processors.geo.jvm.jts.processor.setEPSG")
        .category(DataProcessorType.GEO)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .build())
        .requiredIntegerParameter(Labels.withId(EPSG_KEY), 4326)

        .outputStrategy(
            OutputStrategies.append(PrimitivePropertyBuilder
                .create(Datatypes.Integer, EPSG_RUNTIME)
                .domainProperty("http://data.ign.fr/def/ignf#CartesianCS")
                .build())
        )
        .build();
  }

  @Override
  public ConfiguredEventProcessor<SetEpsgParameter> onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

    Integer epsg_value = extractor.singleValueParameter(EPSG_KEY, Integer.class);
    SetEpsgParameter params = new SetEpsgParameter(graph, epsg_value);

    return new ConfiguredEventProcessor<>(params, SetEPSG::new);
  }
}
