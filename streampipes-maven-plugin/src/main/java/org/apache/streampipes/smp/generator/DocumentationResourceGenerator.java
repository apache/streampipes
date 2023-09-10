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

package org.apache.streampipes.smp.generator;

import org.apache.streampipes.smp.model.AssetModel;
import org.apache.streampipes.smp.util.DirectoryManager;

import com.google.common.base.Charsets;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Path;

public class DocumentationResourceGenerator extends ResourceGenerator {

  public static final String DOCUMENTATION_FILE_NAME = "documentation.md";

  public DocumentationResourceGenerator(ClassLoader loader,
                                        AssetModel extensionsElement,
                                        Path targetPath) {
    super(loader, extensionsElement, targetPath);
  }

  @Override
  public void generate() throws IOException {
    DirectoryManager.createIfNotExists(targetPath);
    try (var inputStream = getResourceInputStream(DOCUMENTATION_FILE_NAME)) {
      if (inputStream != null) {
        var originalDocumentationFileContents = new String(inputStream.readAllBytes());
        // modify docs for documentation page
        String documentationFileContents =
            new MarkdownTitleRemover(originalDocumentationFileContents).removeTitle();

        documentationFileContents =
            new MarkdownHeaderGenerator(extensionsElement, documentationFileContents).createHeaders();

        documentationFileContents = new ImagePathReplacer(documentationFileContents,
            extensionsElement.getAppId()).replaceContentForDocs();

        FileUtils.writeStringToFile(
            targetPath.resolve(extensionsElement.getAppId() + ".md").toFile(),
            documentationFileContents,
            Charsets.UTF_8);
      }
    }
  }
}
