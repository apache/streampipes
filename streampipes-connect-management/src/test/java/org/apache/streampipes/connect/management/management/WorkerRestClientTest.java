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

import org.apache.streampipes.connect.management.util.WorkerPaths;
import org.apache.streampipes.extensions.api.connect.exception.AdapterException;
import org.apache.streampipes.model.connect.adapter.GenericAdapterSetDescription;
import org.apache.streampipes.model.connect.adapter.GenericAdapterStreamDescription;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({WorkerRestClient.class, WorkerPaths.class})
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*"})
public class WorkerRestClientTest {

  /**
   * Notes: In this class I tested how powermock could be used to mok static methods
   * One problem is to mock static methods that return void
   */

  @Before
  public void before() {
    PowerMockito.mockStatic(WorkerRestClient.class);
    PowerMockito.mockStatic(WorkerPaths.class);
  }

  @Test
  public void stopStreamAdapterSuccess() throws Exception {

    String expectedUrl = "worker/stream/stop";
    doNothing().when(WorkerRestClient.class, "stopAdapter", any(), anyString());
    when(WorkerRestClient.class, "stopStreamAdapter", anyString(), any()).thenCallRealMethod();
    when(WorkerPaths.class, "getStreamStopPath").thenReturn(expectedUrl);
    GenericAdapterStreamDescription description = new GenericAdapterStreamDescription();
    description.setElementId("id1");

    WorkerRestClient.stopStreamAdapter("", description);

    verifyStatic(WorkerRestClient.class, times(1));
    WorkerRestClient.stopAdapter(any(), eq(expectedUrl));

  }

  @Test(expected = AdapterException.class)
  public void stopStreamAdapterFail() throws Exception {
    doThrow(new AdapterException()).when(WorkerRestClient.class, "stopAdapter", any(), anyString());
    when(WorkerRestClient.class, "stopStreamAdapter", anyString(), any()).thenCallRealMethod();

    GenericAdapterStreamDescription description = new GenericAdapterStreamDescription();
    description.setElementId("id1");

    WorkerRestClient.stopStreamAdapter("", description);

  }

  @Test
  public void invokeSetAdapterSuccess() throws Exception {

    String expectedUrl = "worker/set/invoke";
    doNothing().when(WorkerRestClient.class, "startAdapter", anyString(), any());
    when(WorkerRestClient.class, "invokeSetAdapter", anyString(), any()).thenCallRealMethod();
    when(WorkerPaths.class, "getSetInvokePath").thenReturn(expectedUrl);

    GenericAdapterSetDescription description = new GenericAdapterSetDescription();
    description.setElementId("id1");
    WorkerRestClient.invokeSetAdapter("", description);

    verifyStatic(WorkerRestClient.class, times(1));
    WorkerRestClient.startAdapter(eq("worker/set/invoke"), any());

  }

  @Test(expected = AdapterException.class)
  public void invokeSetAdapterFail() throws Exception {
    doThrow(new AdapterException()).when(WorkerRestClient.class, "startAdapter", anyString(), any());
    when(WorkerRestClient.class, "invokeSetAdapter", anyString(), any()).thenCallRealMethod();

    WorkerRestClient.invokeSetAdapter("", null);
  }

  @Test
  public void stopSetAdapterSuccess() throws Exception {

    String expectedUrl = "worker/set/stop";
    doNothing().when(WorkerRestClient.class, "stopAdapter", any(), anyString());
    when(WorkerRestClient.class, "stopSetAdapter", anyString(), any()).thenCallRealMethod();
    when(WorkerPaths.class, "getSetStopPath").thenReturn(expectedUrl);

    GenericAdapterSetDescription description = new GenericAdapterSetDescription();
    description.setElementId("id1");
    WorkerRestClient.stopSetAdapter("", description);

    verifyStatic(WorkerRestClient.class, times(1));
    WorkerRestClient.stopAdapter(any(), eq(expectedUrl));

  }

  @Test(expected = AdapterException.class)
  public void stopSetAdapterFail() throws Exception {
    doThrow(new AdapterException()).when(WorkerRestClient.class, "stopAdapter", any(), anyString());
    when(WorkerRestClient.class, "stopSetAdapter", anyString(), any()).thenCallRealMethod();

    GenericAdapterSetDescription description = new GenericAdapterSetDescription();
    description.setElementId("id1");
    WorkerRestClient.stopSetAdapter("", description);

  }

}
