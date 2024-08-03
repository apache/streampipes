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


import org.apache.streampipes.extensions.api.connect.IParser;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.mock;

public class AdapterConfigurationBuilderTest {

  String id = "adapterAppid";

  @Test
  public void create() {
    var adapterConfiguration = AdapterConfigurationBuilder
        .create(id, 0, null)
        .buildConfiguration();

    Assertions.assertNotNull(adapterConfiguration);
    Assertions.assertEquals(
        id,
        adapterConfiguration.getAdapterDescription()
                            .getAppId()
    );

    var expected = new AdapterDescription();
    expected.setElementId("sp:" + id);
    Assertions.assertEquals(expected, adapterConfiguration.getAdapterDescription());
  }

  @Test
  public void withOneParser() {
    var expected = mock(IParser.class);
    var adapterConfiguration = AdapterConfigurationBuilder
        .create(id, 0, null)
        .withSupportedParsers(expected)
        .buildConfiguration();

    Assertions.assertEquals(List.of(expected), adapterConfiguration.getSupportedParsers());
  }

  @Test
  public void withMultipleParserInOneCall() {
    var parser1 = mock(IParser.class);
    var parser2 = mock(IParser.class);
    var adapterConfiguration = AdapterConfigurationBuilder
        .create(id, 0, null)
        .withSupportedParsers(parser1, parser2)
        .buildConfiguration();

    Assertions.assertEquals(List.of(parser1, parser2), adapterConfiguration.getSupportedParsers());
  }

  @Test
  public void withMultipleParserInTwoCall() {
    var parser1 = mock(IParser.class);
    var parser2 = mock(IParser.class);
    var adapterConfiguration = AdapterConfigurationBuilder
        .create(id, 0, null)
        .withSupportedParsers(parser1)
        .withSupportedParsers(parser2)
        .buildConfiguration();

    Assertions.assertEquals(List.of(parser1, parser2), adapterConfiguration.getSupportedParsers());
  }

  @Test
  public void withCategory() {
    var adapterConfiguration = AdapterConfigurationBuilder
        .create(id, 0, null)
        .withCategory(AdapterType.Manufacturing)
        .buildConfiguration();

    var actual = adapterConfiguration.getAdapterDescription()
                                     .getCategory();

    Assertions.assertEquals(1, actual.size());
    Assertions.assertEquals(AdapterType.Manufacturing.getCode(), actual.get(0));
  }


}
