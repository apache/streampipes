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

package org.apache.streampipes.sdk.builder.adapter;

import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.grounding.FormatDescription;
import org.apache.streampipes.model.connect.grounding.ProtocolDescription;
import org.apache.streampipes.model.connect.rules.value.AddTimestampRuleDescription;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GenericDataStreamAdapterBuilderTest {

  private static final String ID = "org.apache.streampipes.sdk.builder.adapter.id";

  GenericDataStreamAdapterBuilder basicBuilder =
      GenericDataStreamAdapterBuilder.create(ID).withAssets(Assets.DOCUMENTATION, Assets.ICON).withLocales(Locales.EN)
          .category(AdapterType.Manufacturing);

  @Test
  public void basicDescriptionParameterTests() {
    var result = basicBuilder.build();

    assertEquals(ID, result.getAppId());
    assertTrue(result.getIncludedAssets().contains(Assets.DOCUMENTATION));
    assertTrue(result.getIncludedAssets().contains(Assets.ICON));
    assertTrue(result.getIncludedLocales().contains(Locales.EN.toFilename()));
    assertTrue(result.getCategory().contains(AdapterType.Manufacturing.name()));
  }

  @Test
  public void formatDescriptionTest() {
    FormatDescription formatDescription = FormatDescriptionBuilder.create("", "", "").build();

    var result = basicBuilder
        .format(formatDescription)
        .build();

    assertEquals(formatDescription, result.getFormatDescription());
  }

  @Test
  public void protocolDescriptionTest() {
    ProtocolDescription protocolDescription = ProtocolDescriptionBuilder
        .create("")
        .build();

    var result = basicBuilder
        .protocol(protocolDescription)
        .build();
    assertEquals(protocolDescription, result.getProtocolDescription());
  }

  @Test
  public void addRuleTest() {
    var testRule = new AddTimestampRuleDescription();

    var result = basicBuilder
        .addRules(List.of(testRule))
        .build();

    assertEquals(1, result.getRules().size());
    assertEquals(testRule, result.getRules().get(0));
  }

}