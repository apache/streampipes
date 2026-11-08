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


package org.apache.streampipes.service.core.migrations;

import org.apache.streampipes.manager.setup.CouchDbInstallationStep;
import org.apache.streampipes.storage.api.pipeline.ICompactPipelineTemplateStorage;
import org.apache.streampipes.storage.api.system.IGenericStorage;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class CouchDbInstallationStepTest {

  private final InMemoryCouchTransport couch = new InMemoryCouchTransport();
  private final CouchDbInstallationStep step = new CouchDbInstallationStep(
      couch.stores(), mock(IGenericStorage.class), mock(ICompactPipelineTemplateStorage.class));

  @Test
  void createsDatabasesAndViews() {
    step.install();

    assertEquals(0, step.getErrorCount());
    assertEquals(List.of("_design/users"), couch.documentIds("users"));
    assertEquals(List.of("_design/adapters", "_design/pipelines"), couch.documentIds("pipeline"));
    assertEquals(List.of("_design/measurement"), couch.documentIds("dataset"));
    assertEquals(List.of("_design/assets", "_design/function-states", "_design/transformation-scripts"),
        couch.documentIds("genericstorage"));
    assertTrue(couch.document("pipeline", "_design/adapters").at("/views/used-adapters/map").asText()
        .contains("emit(stream.correspondingAdapterId, doc._id);"));
  }

  @Test
  void unchangedViewsAreNotWrittenAgainAndAreNotAFailure() {
    step.install();
    step.install();

    assertEquals(0, step.getErrorCount());
    assertEquals(1, couch.requestsStartingWith("PUT /users/_design/users").size());
    assertEquals(1, couch.requestsStartingWith("PUT /pipeline/_design/pipelines").size());
    assertEquals(1, couch.requestsStartingWith("PUT /genericstorage/_design/assets").size());
  }

  @Test
  void failureOfTheUserViewIsCounted() {
    couch.fail("PUT /users/_design/users", 500);

    step.install();

    assertEquals(1, step.getErrorCount());
  }
}
