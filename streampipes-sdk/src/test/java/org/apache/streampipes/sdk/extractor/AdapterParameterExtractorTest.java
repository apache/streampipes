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

package org.apache.streampipes.sdk.extractor;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.extensions.api.connect.IParser;
import org.apache.streampipes.model.connect.grounding.ParserDescription;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AdapterParameterExtractorTest {

  @Test
  public void selectedParser() throws AdapterException {
    var parserInstance = mock(IParser.class);
    var parserDescription = new ParserDescription("", "parserName", "");
    when(parserInstance.declareDescription()).thenReturn(parserDescription);
    when(parserInstance.fromDescription(any())).thenReturn(parserInstance);

    var adapterConfiguration = AdapterConfigurationBuilder.create("test", null)
        .withSupportedParsers(parserInstance)
        .buildConfiguration();

    var adapterDescription = adapterConfiguration.getAdapterDescription();

    // set the selected boolean for our parser configuration
    ((StaticPropertyAlternatives) (adapterDescription.getConfig().get(0)))
        .getAlternatives()
        .get(0)
        .setSelected(true);
    var adapterParameterExtractor = AdapterParameterExtractor.from(
        adapterConfiguration.getAdapterDescription(),
        List.of(parserInstance));

    assertEquals(parserInstance, adapterParameterExtractor.selectedParser());
  }
}
