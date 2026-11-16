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

package org.apache.streampipes.export;

import org.apache.streampipes.export.dataimport.PerformImportGenerator;
import org.apache.streampipes.export.generator.ZipFileBuilder;
import org.apache.streampipes.model.export.AssetExportConfiguration;
import org.apache.streampipes.model.export.ExportItem;
import org.apache.streampipes.model.export.StreamPipesApplicationPackage;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.serializers.json.JacksonSerializer;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class PipelineImportTest {

  @Test
  void shouldCreatePermissionsForPipelineDocumentIds() throws Exception {
    for (var pipelineId : List.of("persist-machine-1", "persist-machine.1", "persist-machine:1", "persist-machine:1.2")) {
      var resources = mock(SpResourceManager.class, RETURNS_DEEP_STUBS);
      var archiveId = pipelineId.replace(":", "").replace(".", "");
      var pipeline = new Pipeline();
      pipeline.setPipelineId(pipelineId);
      var mapper = JacksonSerializer.getObjectMapper();
      var manifest = new StreamPipesApplicationPackage();
      manifest.addPipeline(archiveId);
      var archive = ZipFileBuilder.create()
          .addManifest(mapper.writeValueAsString(manifest))
          .addText(archiveId, mapper.writeValueAsString(pipeline))
          .buildZip();
      var config = new AssetExportConfiguration();
      config.addPipeline(new ExportItem(archiveId, "Pipeline", true));

      new PerformImportGenerator(config, "importer", null, resources, null)
          .generate(new ByteArrayInputStream(archive));

      var storedPipeline = ArgumentCaptor.forClass(Pipeline.class);
      verify(resources.managePipelines().getDb()).persist(storedPipeline.capture());
      assertEquals(pipelineId, storedPipeline.getValue().getPipelineId());
      verify(resources.managePermissions()).createDefault(pipelineId, Pipeline.class, "importer", true);
      verifyNoMoreInteractions(resources.managePermissions());
    }
  }
}
