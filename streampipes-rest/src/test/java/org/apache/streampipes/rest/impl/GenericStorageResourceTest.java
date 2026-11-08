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
package org.apache.streampipes.rest.impl;

import org.apache.streampipes.rest.shared.exception.SpMessageException;
import org.apache.streampipes.storage.api.exceptions.StorageConflictException;
import org.apache.streampipes.storage.api.exceptions.StorageException;
import org.apache.streampipes.storage.api.exceptions.StorageNotFoundException;
import org.apache.streampipes.storage.api.exceptions.StorageQueryException;
import org.apache.streampipes.storage.api.exceptions.StorageUnavailableException;
import org.apache.streampipes.storage.api.system.IGenericStorage;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GenericStorageResourceTest {

  private final IGenericStorage storage = mock(IGenericStorage.class);
  private final GenericStorageResource resource = new GenericStorageResource(storage);

  @Test
  void answersWithTheDocument() {
    when(storage.findOne("a")).thenReturn(Map.of("_id", "a"));

    var response = resource.getCategory("type", "a");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(Map.of("_id", "a"), response.getBody());
  }

  @Test
  void answersNotFoundForAMissingDocument() {
    when(storage.findOne("a")).thenThrow(new StorageNotFoundException("missing"));
    doThrow(new StorageNotFoundException("missing")).when(storage).delete("a", "1-a");

    assertStatus(HttpStatus.NOT_FOUND, () -> resource.getCategory("type", "a"));
    assertStatus(HttpStatus.NOT_FOUND, () -> resource.delete("type", "a", "1-a"));
  }

  @Test
  void answersConflictForAStaleRevision() {
    when(storage.update("a", "{}")).thenThrow(new StorageConflictException("conflict"));
    doThrow(new StorageConflictException("conflict")).when(storage).delete("a", "1-a");

    assertStatus(HttpStatus.CONFLICT, () -> resource.update("type", "a", "{}"));
    assertStatus(HttpStatus.CONFLICT, () -> resource.delete("type", "a", "1-a"));
  }

  @Test
  void keepsInternalServerErrorForOtherStorageErrors() {
    when(storage.findAll("type")).thenThrow(new StorageUnavailableException("down"));
    when(storage.find("type", Map.of())).thenThrow(new StorageQueryException("invalid"));
    when(storage.create("{}")).thenThrow(new StorageException("other"));

    assertStatus(HttpStatus.INTERNAL_SERVER_ERROR, () -> resource.getAll("type"));
    assertStatus(HttpStatus.INTERNAL_SERVER_ERROR, () -> resource.find("type", Map.of()));
    assertStatus(HttpStatus.INTERNAL_SERVER_ERROR, () -> resource.create("type", "{}"));
  }

  private static void assertStatus(HttpStatus expected, Runnable call) {
    assertEquals(expected, assertThrows(SpMessageException.class, call::run).getStatus());
  }
}
