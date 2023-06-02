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

package org.apache.streampipes.extensions.management.model;

import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.api.connect.IEventCollector;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.connect.context.IAdapterGuessSchemaContext;
import org.apache.streampipes.extensions.api.connect.context.IAdapterRuntimeContext;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.model.connect.guess.GuessSchema;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SpServiceDefinitionBuilderTest {

  @Test
  public void registerAdapter() {
    var expected = new TestAdapter();
    var result = SpServiceDefinitionBuilder.create("", "", "", 1)
        .registerAdapter(expected)
        .build();

    assertEquals(1, result.getAdapters().size());
    assertEquals(expected, result.getAdapters().get(0));
  }

  private static class TestAdapter implements StreamPipesAdapter {

    @Override
    public IAdapterConfiguration declareConfig() {
      return null;
    }

    @Override
    public void onAdapterStarted(IAdapterParameterExtractor extractor, IEventCollector collector,
                                 IAdapterRuntimeContext adapterRuntimeContext) {

    }

    @Override
    public void onAdapterStopped(IAdapterParameterExtractor extractor, IAdapterRuntimeContext adapterRuntimeContext) {

    }

    @Override
    public GuessSchema onSchemaRequested(IAdapterParameterExtractor extractor,
                                         IAdapterGuessSchemaContext adapterGuessSchemaContext) {
      return null;
    }
  }
}
