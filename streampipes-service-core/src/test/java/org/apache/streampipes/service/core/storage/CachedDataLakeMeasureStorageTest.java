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
package org.apache.streampipes.service.core.storage;

import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.storage.api.explorer.IDataLakeMeasureStorage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CachedDataLakeMeasureStorageTest {

  private static final String MEASURE_ID = "measure-id";
  private static final String MEASURE_NAME = "measure-name";

  private IDataLakeMeasureStorage delegate;
  private CachedDataLakeMeasureStorage storage;

  @BeforeEach
  void setUp() {
    delegate = mock(IDataLakeMeasureStorage.class);
    var cacheManager = new ConcurrentMapCacheManager(CachedDataLakeMeasureStorage.CACHE_NAME);
    storage = new CachedDataLakeMeasureStorage(delegate, cacheManager);
  }

  @Test
  void findAllCachesSerializedCopies() {
    var measure = makeMeasure("Initial pipeline");
    when(delegate.findAll()).thenReturn(List.of(measure));

    var firstResult = storage.findAll();
    firstResult.get(0).setPipelineName("Changed pipeline");
    var secondResult = storage.findAll();

    assertNotSame(firstResult, secondResult);
    assertNotSame(firstResult.get(0), secondResult.get(0));
    assertEquals("Initial pipeline", secondResult.get(0).getPipelineName());
    verify(delegate).findAll();
  }

  @Test
  void getByMeasureNameCachesSerializedCopy() {
    var measure = makeMeasure("Initial pipeline");
    when(delegate.getByMeasureName(MEASURE_NAME)).thenReturn(measure);

    var firstResult = storage.getByMeasureName(MEASURE_NAME);
    firstResult.setPipelineName("Changed pipeline");
    var secondResult = storage.getByMeasureName(MEASURE_NAME);

    assertNotSame(firstResult, secondResult);
    assertEquals("Initial pipeline", secondResult.getPipelineName());
    verify(delegate).getByMeasureName(MEASURE_NAME);
  }

  @Test
  void updateElementClearsAllCaches() {
    var measure = makeMeasure("Initial pipeline");
    var updatedMeasure = makeMeasure("Updated pipeline");
    when(delegate.getElementById(MEASURE_ID)).thenReturn(measure, updatedMeasure);
    when(delegate.findAll()).thenReturn(List.of(measure), List.of(updatedMeasure));
    when(delegate.getByMeasureName(MEASURE_NAME)).thenReturn(measure, updatedMeasure);
    when(delegate.updateElement(updatedMeasure)).thenReturn(updatedMeasure);

    storage.getElementById(MEASURE_ID);
    storage.findAll();
    storage.getByMeasureName(MEASURE_NAME);
    storage.updateElement(updatedMeasure);

    assertEquals("Updated pipeline", storage.getElementById(MEASURE_ID).getPipelineName());
    assertEquals("Updated pipeline", storage.findAll().get(0).getPipelineName());
    assertEquals("Updated pipeline", storage.getByMeasureName(MEASURE_NAME).getPipelineName());
    verify(delegate, times(2)).getElementById(MEASURE_ID);
    verify(delegate, times(2)).findAll();
    verify(delegate, times(2)).getByMeasureName(MEASURE_NAME);
  }

  private DataLakeMeasure makeMeasure(String pipelineName) {
    var measure = new DataLakeMeasure();
    measure.setElementId(MEASURE_ID);
    measure.setMeasureName(MEASURE_NAME);
    measure.setPipelineName(pipelineName);
    return measure;
  }
}
