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

package org.apache.streampipes.resource.management;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.model.Tuple2;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.storage.api.IAdapterStorage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AdapterResourceManagerTest {

  private IAdapterStorage storage;
  private AdapterResourceManager adapterResourceManager;

  @BeforeEach
  void setUp() {
    storage = mock(IAdapterStorage.class);
    adapterResourceManager = new AdapterResourceManager(storage);
  }

  @Test
  public void encryptAndUpdate_ValidateDescriptionStored() throws AdapterException {

    adapterResourceManager.encryptAndUpdate(new AdapterDescription());

    verify(storage, times(1)).updateElement(any());
  }

  @Test
  void encryptAndCreate_ReturnsIdOnSuccess() throws AdapterException {
    var id = "adapterId";

    when(storage.persist(any())).thenReturn(new Tuple2<>(true, id));
    var result = adapterResourceManager.encryptAndCreate(new AdapterDescription());

    verify(storage, times(1)).persist(any());
    assertEquals(id, result);
  }

  @Test
  void encryptAndCreate_ThrowsAdapterExceptionOnConflict() {

    doThrow(new org.lightcouch.DocumentConflictException("Conflict")).when(storage).persist(any());

    assertThrows(AdapterException.class, () -> adapterResourceManager.encryptAndCreate(new AdapterDescription()));
  }

}
