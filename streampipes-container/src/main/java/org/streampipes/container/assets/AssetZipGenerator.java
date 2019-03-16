/*
Copyright 2019 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.container.assets;

import com.google.common.io.Resources;
import org.streampipes.commons.zip.ZipFileGenerator;

import java.io.File;

public class AssetZipGenerator {

  private String elementAppId;

  public AssetZipGenerator(String elementAppId) {
    this.elementAppId = elementAppId;
  }

  public byte[] makeZip() {
    File assetFolder = getAssetFolder();
    return new ZipFileGenerator(assetFolder).makeZipToBytes();
  }

  private File getAssetFolder() {
    return new File(Resources.getResource(elementAppId).getFile());
  }
}
