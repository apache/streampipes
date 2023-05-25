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

package org.apache.streampipes.extensions.management.connect;

import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class ConnectWorkerDescriptionProviderTest {

  private String adapterId = "adapterAppId";
  private ConnectWorkerDescriptionProvider provider;

  @Before
  public void initializeProvider() {
    provider = spy(new ConnectWorkerDescriptionProvider());

    var testAdapter = mock(StreamPipesAdapter.class);
    doAnswer(invocation ->
        AdapterConfigurationBuilder
            .create(adapterId, null)
            .buildConfiguration())
        .when(testAdapter)
        .declareConfig();

    List<StreamPipesAdapter> adapters = List.of(testAdapter);
    doReturn(adapters).when(provider).getRegisteredAdapters();
  }

  @Test
  public void getAdapterDescriptions() {
    var expected = AdapterConfigurationBuilder
        .create(adapterId, null)
        .buildConfiguration()
        .getAdapterDescription();

    var result = provider.getAdapterDescriptions();

    assertEquals(1, result.size());
    assertEquals(expected, result.get(0));
  }

  @Test
  public void getAdapterDescription() {
    var result = provider.getAdapterDescription(adapterId);

    assertTrue(result.isPresent());
    assertEquals(adapterId, result.get().getAppId());
  }


}
