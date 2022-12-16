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
package org.apache.streampipes.commons.zip;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipFileExtractor {

  private InputStream zipInputStream;

  public ZipFileExtractor(InputStream zipInputStream) {
    this.zipInputStream = zipInputStream;
  }

  // TODO used by export feature - extend this to support binaries
  public Map<String, byte[]> extractZipToMap() throws IOException {
    byte[] buffer = new byte[1024];
    Map<String, byte[]> entries = new HashMap<>();
    ZipInputStream zis = new ZipInputStream(zipInputStream);
    ZipEntry zipEntry = zis.getNextEntry();
    while (zipEntry != null) {
      ByteArrayOutputStream fos = new ByteArrayOutputStream();
      int len;
      while ((len = zis.read(buffer)) > 0) {
        fos.write(buffer, 0, len);
      }
      entries.put(sanitizeName(zipEntry.getName()), fos.toByteArray());
      fos.close();
      zipEntry = zis.getNextEntry();
    }
    zis.closeEntry();
    zis.close();

    return entries;
  }

  private String sanitizeName(String name) {
    return name.split("\\.")[0];
  }

  public void extractZipToFile(String targetFolder) throws IOException {
    File destDir = new File(targetFolder);
    if (!destDir.exists()) {
      destDir.mkdirs();
    }
    byte[] buffer = new byte[1024];
    ZipInputStream zis = new ZipInputStream(zipInputStream);
    ZipEntry zipEntry = zis.getNextEntry();
    while (zipEntry != null) {
      File newFile = newFile(destDir, zipEntry);
      FileOutputStream fos = new FileOutputStream(newFile);
      int len;
      while ((len = zis.read(buffer)) > 0) {
        fos.write(buffer, 0, len);
      }
      fos.close();
      zipEntry = zis.getNextEntry();
    }
    zis.closeEntry();
    zis.close();
  }

  private File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
    File destFile = new File(destinationDir, zipEntry.getName());

    String destDirPath = destinationDir.getCanonicalPath();
    String destFilePath = destFile.getCanonicalPath();

    if (!destFilePath.startsWith(destDirPath + File.separator)) {
      throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
    }

    return destFile;
  }
}
