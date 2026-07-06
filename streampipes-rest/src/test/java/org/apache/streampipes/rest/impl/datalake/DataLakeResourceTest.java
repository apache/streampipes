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

package org.apache.streampipes.rest.impl.datalake;

import org.apache.streampipes.dataexplorer.api.IDataExplorerQueryManagement;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DataLakeResourceTest {

  @Test
  void getLatestEventsRequestsLatestTimestampsForDistinctMeasurements() throws Exception {
    var queryManagement = mock(IDataExplorerQueryManagement.class);
    when(queryManagement.getLatestTimestamps(List.of("a", "bb", "broken")))
        .thenReturn(Map.of("a", 1L, "bb", 2L, "broken", 0L));
    var resource = dataLakeResource(queryManagement, true);

    var response = resource.getLatestEvents(List.of("a", "bb", "a", "broken"));

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(Map.of("a", 1L, "bb", 2L, "broken", 0L), response.getBody());
    verify(queryManagement).getLatestTimestamps(List.of("a", "bb", "broken"));
    verify(queryManagement, times(0)).getData(any(), eq(true));
  }

  @Test
  void getLatestEventsRejectsUnauthorizedMeasurementBeforeQuerying() throws Exception {
    var queryManagement = mock(IDataExplorerQueryManagement.class);
    var resource = dataLakeResource(queryManagement, false);

    var response = resource.getLatestEvents(List.of("a"));

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("No read permission for measurement a", response.getBody());
    verify(queryManagement, times(0)).getData(any(), eq(true));
    verify(queryManagement, times(0)).getLatestTimestamps(any());
  }

  private static DataLakeResource dataLakeResource(IDataExplorerQueryManagement queryManagement,
                                                   boolean canRead) throws Exception {
    var resource = mock(DataLakeResource.class, CALLS_REAL_METHODS);
    doReturn(canRead).when(resource).checkPermissionByName(any(), eq("READ"));
    setQueryManagement(resource, queryManagement);
    return resource;
  }

  private static void setQueryManagement(DataLakeResource resource,
                                         IDataExplorerQueryManagement queryManagement) throws Exception {
    Field field = DataLakeResource.class.getDeclaredField("dataExplorerQueryManagement");
    field.setAccessible(true);
    field.set(resource, queryManagement);
  }
}
