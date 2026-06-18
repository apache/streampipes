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

import org.apache.streampipes.model.Tuple2;
import org.apache.streampipes.model.datalake.DataExplorerWidgetModel;
import org.apache.streampipes.storage.api.explorer.IChartStorage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CachedChartStorageTest {

  private static final String WIDGET_ID = "widget-id";

  private IChartStorage delegate;
  private CachedChartStorage storage;

  @BeforeEach
  void setUp() {
    delegate = mock(IChartStorage.class);
    var cacheManager = new ConcurrentMapCacheManager(
        CachedChartStorage.CACHE_NAME,
        CachedChartStorage.FIND_ALL_CACHE_NAME
    );
    storage = new CachedChartStorage(delegate, cacheManager);
  }

  @Test
  void findAllCachesSerializedCopies() {
    var widget = makeWidget("Initial title");
    when(delegate.findAll()).thenReturn(List.of(widget));

    var firstResult = storage.findAll();
    firstResult.get(0).getBaseAppearanceConfig().put("widgetTitle", "Changed title");
    var secondResult = storage.findAll();

    assertNotSame(firstResult, secondResult);
    assertNotSame(firstResult.get(0), secondResult.get(0));
    assertEquals("Initial title", secondResult.get(0).getBaseAppearanceConfig().get("widgetTitle"));
    verify(delegate, times(1)).findAll();
  }

  @Test
  void getElementByIdCachesSerializedCopy() {
    var widget = makeWidget("Initial title");
    when(delegate.getElementById(WIDGET_ID)).thenReturn(widget);

    var firstResult = storage.getElementById(WIDGET_ID);
    firstResult.getBaseAppearanceConfig().put("widgetTitle", "Changed title");
    var secondResult = storage.getElementById(WIDGET_ID);

    assertNotSame(firstResult, secondResult);
    assertEquals("Initial title", secondResult.getBaseAppearanceConfig().get("widgetTitle"));
    verify(delegate, times(1)).getElementById(WIDGET_ID);
  }

  @Test
  void updateElementEvictsCachedElement() {
    var widget = makeWidget("Initial title");
    var updatedWidget = makeWidget("Updated title");
    when(delegate.getElementById(WIDGET_ID)).thenReturn(widget, updatedWidget);
    when(delegate.updateElement(updatedWidget)).thenReturn(updatedWidget);

    storage.getElementById(WIDGET_ID);
    storage.updateElement(updatedWidget);
    var result = storage.getElementById(WIDGET_ID);

    assertEquals("Updated title", result.getBaseAppearanceConfig().get("widgetTitle"));
    verify(delegate, times(2)).getElementById(WIDGET_ID);
  }

  @Test
  void updateElementEvictsFindAllCache() {
    var widget = makeWidget("Initial title");
    var updatedWidget = makeWidget("Updated title");
    when(delegate.findAll()).thenReturn(List.of(widget), List.of(updatedWidget));
    when(delegate.updateElement(updatedWidget)).thenReturn(updatedWidget);

    storage.findAll();
    storage.updateElement(updatedWidget);
    var result = storage.findAll();

    assertEquals("Updated title", result.get(0).getBaseAppearanceConfig().get("widgetTitle"));
    verify(delegate, times(2)).findAll();
  }

  @Test
  void deleteElementByIdEvictsCachedElement() {
    var widget = makeWidget("Initial title");
    when(delegate.getElementById(WIDGET_ID)).thenReturn(widget);

    storage.getElementById(WIDGET_ID);
    storage.deleteElementById(WIDGET_ID);
    storage.getElementById(WIDGET_ID);

    verify(delegate).deleteElementById(WIDGET_ID);
    verify(delegate, times(2)).getElementById(WIDGET_ID);
  }

  @Test
  void successfulPersistEvictsExistingElement() {
    var widget = makeWidget("Initial title");
    var persistedWidget = makeWidget("Persisted title");
    when(delegate.getElementById(WIDGET_ID)).thenReturn(widget, persistedWidget);
    when(delegate.persist(persistedWidget)).thenReturn(new Tuple2<>(true, WIDGET_ID));

    storage.getElementById(WIDGET_ID);
    storage.persist(persistedWidget);
    var result = storage.getElementById(WIDGET_ID);

    assertEquals("Persisted title", result.getBaseAppearanceConfig().get("widgetTitle"));
    verify(delegate, times(2)).getElementById(WIDGET_ID);
  }

  private DataExplorerWidgetModel makeWidget(String title) {
    var widget = new DataExplorerWidgetModel();
    widget.setElementId(WIDGET_ID);
    var baseAppearanceConfig = new HashMap<String, Object>();
    baseAppearanceConfig.put("widgetTitle", title);
    widget.setBaseAppearanceConfig(baseAppearanceConfig);
    return widget;
  }
}
