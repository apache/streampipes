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
import org.apache.streampipes.export.dataimport.PreviewImportGenerator;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.pipeline.PipelineManager;
import org.apache.streampipes.model.export.AssetExportConfiguration;
import org.apache.streampipes.resource.management.SpResourceManager;

import java.io.IOException;
import java.io.InputStream;

public class ImportManager {

  public static AssetExportConfiguration getImportPreview(InputStream packageZipStream,
                                                          ExtensionServiceRequestManager extensionServiceRequestManager,
                                                          SpResourceManager resourceManager,
                                                          PipelineManager pipelineManager)
      throws IOException {
    return new PreviewImportGenerator(extensionServiceRequestManager, resourceManager, pipelineManager)
        .generate(packageZipStream);
  }

  public static void performImport(InputStream packageZipStream,
                                   AssetExportConfiguration exportConfiguration,
                                   String ownerSid,
                                   ExtensionServiceRequestManager extensionServiceRequestManager,
                                   SpResourceManager resourceManager,
                                   PipelineManager pipelineManager) throws IOException {
    new PerformImportGenerator(
        exportConfiguration,
        ownerSid,
        extensionServiceRequestManager,
        resourceManager,
        pipelineManager
    )
        .generate(packageZipStream);
  }
}
