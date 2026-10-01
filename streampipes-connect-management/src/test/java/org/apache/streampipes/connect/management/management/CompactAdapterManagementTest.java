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

package org.apache.streampipes.connect.management.management;

import org.apache.streampipes.connect.management.compact.generator.AdapterModelGenerator;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.compact.CompactAdapter;
import org.apache.streampipes.storage.api.connect.IAdapterStorage;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class CompactAdapterManagementTest {

  private final IAdapterStorage descriptions = mock(IAdapterStorage.class);
  private final AdapterModelGenerator generator = mock(AdapterModelGenerator.class);
  private final CompactAdapterManagement management = new CompactAdapterManagement(List.of(generator), descriptions);
  private final CompactAdapter compact = new CompactAdapter(null, null, null, "app", List.of(), null, null, null);

  @Test
  void appliesGeneratorsToMatchingInjectedDescription() throws Exception {
    var other = new AdapterDescription();
    other.setAppId("other");
    var description = new AdapterDescription();
    description.setAppId("app");
    when(descriptions.findAll()).thenReturn(List.of(other, description));

    assertSame(description, management.convertToAdapterDescription(compact, "user"));
    verify(generator).apply(description, compact, "user");
  }

  @Test
  void rejectsUnknownAppBeforeApplyingGenerators() {
    when(descriptions.findAll()).thenReturn(List.of());

    assertThrows(IllegalArgumentException.class, () -> management.convertToAdapterDescription(compact, "user"));
    verifyNoInteractions(generator);
  }
}
