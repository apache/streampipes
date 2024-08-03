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

package org.apache.streampipes.extensions.management.connect;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.extensions.management.init.IDeclarersSingleton;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AdapterWorkerManagementTest {

  @Test
  public void invokeAdapterNotPresent() throws AdapterException {
    var adapterDescription = AdapterConfigurationBuilder
        .create("id", 0,  null)
        .build();

    var declarerSingleton = mock(IDeclarersSingleton.class);
    when(declarerSingleton.getAdapter(any())).thenReturn(Optional.empty());

    var adapterWorkerManagement = new AdapterWorkerManagement(
        null, declarerSingleton);

    assertThrows(AdapterException.class, () -> adapterWorkerManagement.invokeAdapter(adapterDescription));

  }
}
