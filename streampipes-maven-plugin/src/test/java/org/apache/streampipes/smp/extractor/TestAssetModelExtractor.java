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

package org.apache.streampipes.smp.extractor;

import org.apache.streampipes.smp.constants.PeType;
import org.apache.streampipes.smp.model.AssetModel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestAssetModelExtractor {

  private String getGetDeclareModelContent4 =
      "return ProcessingElementBuilder.create(\"org.apache.streampipes.processors.filters.jvm.numericalfilter\")\n"
      + "            .category(DataProcessorType.FILTER)\n"
      + "            .withAssets(Assets.DOCUMENTATION, Assets.ICON)\n"
      + "            .withLocales(Locales.EN)\n"
      + "            .requiredStream(StreamRequirementsBuilder\n"
      + "                    .create()\n"
      + "                    .requiredPropertyWithUnaryMapping(EpRequirements.numberReq(),\n"
      + "                            Labels.withId(NUMBER_MAPPING),\n"
      + "                            PropertyScope.NONE).build())\n"
      + "            .outputStrategy(OutputStrategies.keep())\n"
      + "            .requiredSingleValueSelection(Labels.withId(OPERATION), Options.from(\"<\", \"<=\", \">\",\n"
      + "                    \">=\", \"==\", \"!=\"))\n"
      + "            .requiredFloatParameter(Labels.withId(VALUE), NUMBER_MAPPING)\n"
      + "            .supportedProtocols(SupportedProtocols.kafka(), SupportedProtocols.jms())\n"
      + "            .supportedFormats(SupportedFormats.jsonFormat())\n"
      + "            .build();";


  @Test
  public void testAssetExtraction4() {
    AssetModel model = new AssetModelItemExtractor(getGetDeclareModelContent4).extractAssetItem();

    assertEquals("org.apache.streampipes.processors.filters.jvm.numericalfilter", model.getAppId());
    assertEquals(PeType.PROCESSOR, model.getPeType());
    assertNull(model.getPipelineElementName());
    assertNull(model.getPipelineElementDescription());

  }
}
