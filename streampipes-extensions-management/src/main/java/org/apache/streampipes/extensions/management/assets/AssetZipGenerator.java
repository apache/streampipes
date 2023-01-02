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
package org.apache.streampipes.extensions.management.assets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class AssetZipGenerator {

  private List<String> includedAssets;
  private String appId;

  public AssetZipGenerator(String appId, List<String> includedAssets) {
    this.includedAssets = includedAssets;
    this.appId = appId;
  }

  public byte[] makeZip() throws IOException {
    return makeZipFromAssets();
  }

  private byte[] makeZipFromAssets() throws IOException {
    byte[] buffer = new byte[1024];

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ZipOutputStream out = new ZipOutputStream(outputStream);

    for (String asset : includedAssets) {
      ZipEntry ze = new ZipEntry(asset);
      out.putNextEntry(ze);

      InputStream in = null;
      try {
        ClassLoader classLoader = this.getClass().getClassLoader();
        in = classLoader.getResourceAsStream(makePath(asset));
        int len;
        while ((len = in.read(buffer)) > 0) {
          out.write(buffer, 0, len);
        }
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        in.close();
      }
    }
    out.closeEntry();
    out.close();
    return outputStream.toByteArray();
  }

  private String makePath(String assetAppendix) {
    return this.appId + "/" + assetAppendix;
  }
}
