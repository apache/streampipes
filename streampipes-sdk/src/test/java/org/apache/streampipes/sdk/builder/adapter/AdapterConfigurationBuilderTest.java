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


import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.Parser;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AdapterConfigurationBuilderTest {

  @Test
  public void create() {
    var adapterConfiguration = AdapterConfigurationBuilder
        .create()
        .build();

    assertNotNull(adapterConfiguration);
  }

  @Test
  public void withOneParser() {
    var expected = new Parser();
    var adapterConfiguration = AdapterConfigurationBuilder
        .create()
        .withSupportedParsers(expected)
        .build();

    assertEquals(List.of(expected), adapterConfiguration.getSupportedParsers());
  }

  @Test
  public void withMultipleParserInOneCall() {
    var parser1 = new Parser();
    var parser2 = new Parser();
    var adapterConfiguration = AdapterConfigurationBuilder
        .create()
        .withSupportedParsers(parser1, parser2)
        .build();

    assertEquals(List.of(parser1, parser2), adapterConfiguration.getSupportedParsers());
  }

  @Test
  public void withMultipleParserInTwoCall() {
    var parser1 = new Parser();
    var parser2 = new Parser();
    var adapterConfiguration = AdapterConfigurationBuilder
        .create()
        .withSupportedParsers(parser1)
        .withSupportedParsers(parser2)
        .build();

    assertEquals(List.of(parser1, parser2), adapterConfiguration.getSupportedParsers());
  }

  @Test
  public void withAdapterDescription() {
    var expected = new AdapterDescription();

    var adapterConfiguration = AdapterConfigurationBuilder
        .create()
        .withAdapterDescription(expected)
        .build();

    assertEquals(expected, adapterConfiguration.getAdapterDescription());
  }

}