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

package org.apache.streampipes.model.connect.adapter;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AdapterConfigurationTest {

  @Test
  public void simpleAdapterDescriptionTest() {
    var adapterDescription = new AdapterDescription();
    var adapterConfiguration = new AdapterConfiguration(adapterDescription, null);

    assertEquals(adapterDescription, adapterConfiguration.getAdapterDescription());
  }

  @Test
  public void adapterDescriptionWithParserTest() {
//    var jsonConfig = new FreeTextStaticProperty();
//    var parserConfig = new StaticPropertyAlternatives();
//    var jsonAlternative = new StaticPropertyAlternative();
//    jsonAlternative.setStaticProperty(jsonConfig);
//    parserConfig.setAlternatives(List.of(jsonAlternative));
//
//    var expectedDescription = new AdapterDescription();
//    expectedDescription.setConfig(List.of(parserConfig));
//
//    var parserDescription = new ParserDescription();
//    parserDescription.setConfig(List.of(parserConfig));
//
//    var adapterDescription = new AdapterDescription();
//    Parser parser = Mockito.mock(Parser.class);
//    when(parser.declareDescription()).thenReturn(parserDescription);
//    List<Parser> parsers = List.of(parser);
//
//    var adapterConfiguration = new AdapterConfiguration(adapterDescription, parsers);
//    var actual = adapterConfiguration.getAdapterDescription();
//
//    assertEquals(expectedDescription.getConfig().size(),
//        actual.getConfig().size());
//    assertEquals(parserConfig,
//        actual.getConfig().get(0));
  }
}