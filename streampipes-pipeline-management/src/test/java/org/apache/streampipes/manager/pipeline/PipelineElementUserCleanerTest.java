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

package org.apache.streampipes.manager.pipeline;

import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.pipeline.Pipeline;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;

class PipelineElementUserCleanerTest {

  @Test
  void clearCorrespondingUsersRemovesUsersFromProcessorsAndSinks() {
    var processor = new DataProcessorInvocation();
    processor.setCorrespondingUser("user-1");
    var sink = new DataSinkInvocation();
    sink.setCorrespondingUser("user-1");
    var pipeline = new Pipeline();
    pipeline.setSepas(List.of(processor));
    pipeline.setActions(List.of(sink));

    PipelineElementUserCleaner.clearCorrespondingUsers(pipeline);

    assertNull(processor.getCorrespondingUser());
    assertNull(sink.getCorrespondingUser());
  }
}
