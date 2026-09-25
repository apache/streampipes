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

package org.apache.streampipes.dataexplorer.iotdb;

import org.apache.streampipes.model.dataset.DatasetMetadata;

import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.session.pool.SessionPool;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class IotDbAdministrationBackendTest {
  @Test
  void mutationsUseNonQueryApiWithoutClosingSharedPool() throws Exception {
    var pool = mock(SessionPool.class);
    var administration = new IotDbAdministrationBackend(pool);
    var dataset = new DatasetMetadata();
    dataset.setMeasureName("machine");
    var path = IotDbQueryCompiler.datasetPath("machine");
    assertTrue(administration.deleteRange(dataset, 100L, 300L));
    assertTrue(administration.deleteRange(dataset, null, 300L));
    assertTrue(administration.deleteRange(dataset, null, null));
    assertTrue(administration.delete(dataset));
    verify(pool).executeNonQueryStatement("DELETE FROM " + path + ".* WHERE time > 100 AND time < 300");
    verify(pool).executeNonQueryStatement("DELETE FROM " + path + ".* WHERE time < 300");
    verify(pool).executeNonQueryStatement("DELETE FROM " + path + ".*");
    verify(pool).executeNonQueryStatement("DELETE TIMESERIES " + path + ".*");
    verifyNoMoreInteractions(pool);
  }

  @Test
  void failedMutationsAreReported() throws Exception {
    var pool = mock(SessionPool.class);
    var dataset = new DatasetMetadata();
    dataset.setMeasureName("machine");
    doThrow(new StatementExecutionException("failed")).when(pool)
        .executeNonQueryStatement("DELETE TIMESERIES " + IotDbQueryCompiler.datasetPath("machine") + ".*");
    assertFalse(new IotDbAdministrationBackend(pool).delete(dataset));
  }
}
