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

package org.apache.streampipes.sinks.databases.jvm.iotdb;

import org.apache.streampipes.model.graph.DataSinkInvocation;

import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class TestIotDbParameters {

  @Test
  public void testIotDbParameters() {
    final String host = "localhost";
    final Integer port = 6667;
    final String user = "user";
    final String password = "password";
    final String database = "database";
    final String device = "device";
    final String timestampField = "timestampField";

    final IotDbParameters parameters =
        new IotDbParameters(mock(DataSinkInvocation.class), host, port, user, password, database, device,
            timestampField);

    Assert.assertEquals(host, parameters.getHost());
    Assert.assertEquals(port, parameters.getPort());
    Assert.assertEquals(user, parameters.getUser());
    Assert.assertEquals(password, parameters.getPassword());
    Assert.assertEquals(database, parameters.getDatabase());
    Assert.assertEquals(device, parameters.getDevice());
    Assert.assertEquals(timestampField, parameters.getTimestampField());
  }
}