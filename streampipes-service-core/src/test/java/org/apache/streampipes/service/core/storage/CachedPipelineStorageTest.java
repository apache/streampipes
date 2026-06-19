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

import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.storage.api.pipeline.IPipelineStorage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CachedPipelineStorageTest {

  private static final String PIPELINE_ID = "pipeline-id";
  private static final String ADAPTER_ID = "adapter-id";

  private IPipelineStorage delegate;
  private CachedPipelineStorage storage;

  @BeforeEach
  void setUp() {
    delegate = mock(IPipelineStorage.class);
    var cacheManager = new ConcurrentMapCacheManager(CachedPipelineStorage.CACHE_NAME);
    storage = new CachedPipelineStorage(delegate, cacheManager);
  }

  @Test
  void findAllCachesSerializedCopies() {
    var pipeline = makePipeline("Initial name");
    when(delegate.findAll()).thenReturn(List.of(pipeline));

    var firstResult = storage.findAll();
    firstResult.get(0).setName("Changed name");
    var secondResult = storage.findAll();

    assertNotSame(firstResult, secondResult);
    assertNotSame(firstResult.get(0), secondResult.get(0));
    assertEquals("Initial name", secondResult.get(0).getName());
    verify(delegate).findAll();
  }

  @Test
  void getElementByIdCachesSerializedCopy() {
    var pipeline = makePipeline("Initial name");
    when(delegate.getElementById(PIPELINE_ID)).thenReturn(pipeline);

    var firstResult = storage.getElementById(PIPELINE_ID);
    firstResult.setName("Changed name");
    var secondResult = storage.getElementById(PIPELINE_ID);

    assertNotSame(firstResult, secondResult);
    assertEquals("Initial name", secondResult.getName());
    verify(delegate).getElementById(PIPELINE_ID);
  }

  @Test
  void getPipelinesUsingAdapterCachesResult() {
    when(delegate.getPipelinesUsingAdapter(ADAPTER_ID)).thenReturn(List.of(PIPELINE_ID));

    var firstResult = storage.getPipelinesUsingAdapter(ADAPTER_ID);
    var secondResult = storage.getPipelinesUsingAdapter(ADAPTER_ID);

    assertNotSame(firstResult, secondResult);
    assertEquals(List.of(PIPELINE_ID), secondResult);
    verify(delegate).getPipelinesUsingAdapter(ADAPTER_ID);
  }

  @Test
  void updateElementClearsAllCaches() {
    var pipeline = makePipeline("Initial name");
    var updatedPipeline = makePipeline("Updated name");
    when(delegate.getElementById(PIPELINE_ID)).thenReturn(pipeline, updatedPipeline);
    when(delegate.findAll()).thenReturn(List.of(pipeline), List.of(updatedPipeline));
    when(delegate.getPipelinesUsingAdapter(ADAPTER_ID))
        .thenReturn(List.of(PIPELINE_ID), List.of("updated-pipeline-id"));
    when(delegate.updateElement(updatedPipeline)).thenReturn(updatedPipeline);

    storage.getElementById(PIPELINE_ID);
    storage.findAll();
    storage.getPipelinesUsingAdapter(ADAPTER_ID);
    storage.updateElement(updatedPipeline);

    assertEquals("Updated name", storage.getElementById(PIPELINE_ID).getName());
    assertEquals("Updated name", storage.findAll().get(0).getName());
    assertEquals(List.of("updated-pipeline-id"), storage.getPipelinesUsingAdapter(ADAPTER_ID));
    verify(delegate, times(2)).getElementById(PIPELINE_ID);
    verify(delegate, times(2)).findAll();
    verify(delegate, times(2)).getPipelinesUsingAdapter(ADAPTER_ID);
  }

  @Test
  void updateWaitsForInFlightCacheMissBeforeClearingCache() throws Exception {
    var initialPipeline = makePipeline("Initial name");
    var updatedPipeline = makePipeline("Updated name");
    var readStarted = new CountDownLatch(1);
    var allowReadToFinish = new CountDownLatch(1);
    var updateAttempted = new CountDownLatch(1);
    when(delegate.getElementById(PIPELINE_ID)).thenAnswer(invocation -> {
      readStarted.countDown();
      allowReadToFinish.await();
      return initialPipeline;
    }).thenReturn(updatedPipeline);
    when(delegate.updateElement(updatedPipeline)).thenReturn(updatedPipeline);

    var executor = Executors.newFixedThreadPool(2);
    try {
      var readFuture = executor.submit(() -> storage.getElementById(PIPELINE_ID));
      assertTrue(readStarted.await(5, TimeUnit.SECONDS));
      var updateFuture = executor.submit(() -> {
        updateAttempted.countDown();
        return storage.updateElement(updatedPipeline);
      });
      assertTrue(updateAttempted.await(5, TimeUnit.SECONDS));

      assertThrows(TimeoutException.class, () -> updateFuture.get(100, TimeUnit.MILLISECONDS));
      allowReadToFinish.countDown();

      assertEquals("Initial name", readFuture.get().getName());
      assertEquals("Updated name", updateFuture.get().getName());
      assertEquals("Updated name", storage.getElementById(PIPELINE_ID).getName());
      verify(delegate, times(2)).getElementById(PIPELINE_ID);
    } finally {
      executor.shutdownNow();
    }
  }

  @Test
  void invalidationFailureDisablesFurtherCacheReads() throws Exception {
    var stalePipeline = makePipeline("Stale name");
    var updatedPipeline = makePipeline("Updated name");
    var cache = mock(Cache.class);
    var cacheManager = mock(CacheManager.class);
    when(cacheManager.getCache(CachedPipelineStorage.CACHE_NAME)).thenReturn(cache);
    when(cache.get("id:" + PIPELINE_ID, String.class))
        .thenReturn(org.apache.streampipes.serializers.json.JacksonSerializer.getObjectMapper()
            .writerFor(Pipeline.class)
            .writeValueAsString(stalePipeline));
    doThrow(new IllegalStateException("Cache unavailable")).when(cache).clear();
    when(delegate.updateElement(updatedPipeline)).thenReturn(updatedPipeline);
    when(delegate.getElementById(PIPELINE_ID)).thenReturn(updatedPipeline);
    var storageWithFailingCache = new CachedPipelineStorage(delegate, cacheManager);

    assertEquals("Stale name", storageWithFailingCache.getElementById(PIPELINE_ID).getName());
    storageWithFailingCache.updateElement(updatedPipeline);
    assertEquals("Updated name", storageWithFailingCache.getElementById(PIPELINE_ID).getName());

    verify(cache, times(1)).get("id:" + PIPELINE_ID, String.class);
    verify(delegate).getElementById(PIPELINE_ID);
  }

  private Pipeline makePipeline(String name) {
    var pipeline = new Pipeline();
    pipeline.setPipelineId(PIPELINE_ID);
    pipeline.setName(name);
    return pipeline;
  }
}
