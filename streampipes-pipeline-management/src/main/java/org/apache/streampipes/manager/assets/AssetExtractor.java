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

import org.apache.streampipes.commons.constants.GlobalStreamPipesConstants;
import org.apache.streampipes.commons.zip.ZipFileExtractor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class AssetExtractor {

  private InputStream zipInputStream;
  private String appId;

  public AssetExtractor(InputStream zipInputStream, String appId) {
    this.zipInputStream = zipInputStream;
    this.appId = appId;
  }

  public void extractAssetContents() throws IOException {
    new ZipFileExtractor(zipInputStream).extractZipToFile(makeAssetLocation(appId));
    replaceImagePaths();
  }

  private void replaceImagePaths() {
    new DocumentationParser(new File(makeDocumentationAssetPath(appId)), appId).replaceImageUrls();
  }

  private String makeAssetLocation(String appId) {
    return AssetConstants.ASSET_BASE_DIR
        + File.separator + appId;
  }

  private String makeDocumentationAssetPath(String appId) {
    return makeAssetLocation(appId) + File.separator + GlobalStreamPipesConstants
        .STD_DOCUMENTATION_NAME;
  }
}
