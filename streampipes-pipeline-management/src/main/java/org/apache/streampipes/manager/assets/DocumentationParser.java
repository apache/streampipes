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
package org.apache.streampipes.manager.assets;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DocumentationParser {

  private File file;
  private String appId;

  public DocumentationParser(File file, String appId) {
    this.file = file;
    this.appId = appId;
  }


  public void replaceImageUrls() {
    try {
      String fileContents = getFileContents();
      String newFileContents = new ImagePathReplacer(fileContents, appId).replaceContent();
      FileUtils.writeStringToFile(file, newFileContents, StandardCharsets.UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private String getFileContents() throws IOException {
    return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
  }

}
