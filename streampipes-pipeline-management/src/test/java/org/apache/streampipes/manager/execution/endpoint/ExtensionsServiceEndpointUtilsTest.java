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

package org.apache.streampipes.manager.execution.endpoint;

import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.storage.api.pipeline.IPipelineElementDescriptionStorage;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceUrlProvider;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExtensionsServiceEndpointUtilsTest {

  private final IPipelineElementDescriptionStorage storage = mock(IPipelineElementDescriptionStorage.class);

  @Test
  void resolvesProcessorUsingInjectedStorage() {
    when(storage.getDataProcessorByAppId("processor")).thenReturn(new DataProcessorDescription());

    assertEquals(SpServiceUrlProvider.DATA_PROCESSOR,
        ExtensionsServiceEndpointUtils.getPipelineElementType("processor", storage));
    verify(storage).getDataProcessorByAppId("processor");
  }

  @Test
  void fallsBackToSinkWhenNoProcessorExists() {
    when(storage.getDataProcessorByAppId("sink")).thenThrow(new NoSuchElementException());

    assertEquals(SpServiceUrlProvider.DATA_SINK,
        ExtensionsServiceEndpointUtils.getPipelineElementType("sink", storage));
  }

  @Test
  void doesNotInterpretStorageFailuresAsSinkTypes() {
    when(storage.getDataProcessorByAppId("processor")).thenThrow(new IllegalStateException("Storage unavailable"));

    assertThrows(IllegalStateException.class,
        () -> ExtensionsServiceEndpointUtils.getPipelineElementType("processor", storage));
  }
}
