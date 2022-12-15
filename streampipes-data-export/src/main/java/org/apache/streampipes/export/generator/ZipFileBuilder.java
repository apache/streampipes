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

package org.apache.streampipes.export.generator;

import org.apache.streampipes.export.constants.ExportConstants;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFileBuilder {

  private final Map<String, byte[]> binaryEntries;
  private final Map<String, String> textEntries;
  private final Map<String, File> fileEntries;
  private String manifest;

  public static ZipFileBuilder create() {
    return new ZipFileBuilder();
  }

  private ZipFileBuilder() {
    this.binaryEntries = new HashMap<>();
    this.fileEntries = new HashMap<>();
    this.textEntries = new HashMap<>();
  }

  public ZipFileBuilder addText(String filename,
                                String content) {
    this.textEntries.put(filename, content);

    return this;
  }

  public ZipFileBuilder addBinary(String filename,
                                  byte[] content) {
    this.binaryEntries.put(filename, content);

    return this;
  }

  public ZipFileBuilder addFile(String filename,
                                File file) {
    this.fileEntries.put(filename, file);

    return this;
  }

  public ZipFileBuilder addManifest(String manifest) {
    this.manifest = manifest;

    return this;
  }

  public byte[] buildZip() throws IOException {
    return makeZip();
  }

  private byte[] makeZip() throws IOException {
    byte[] buffer = new byte[1024];

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ZipOutputStream out = new ZipOutputStream(outputStream);

    for (String documentKey : this.textEntries.keySet()) {
      byte[] document = asBytes(this.textEntries.get(documentKey));
      addZipEntry(documentKey + ".json", document, out, buffer);
    }

    for (String binary : this.binaryEntries.keySet()) {
      addZipEntry(binary, this.binaryEntries.get(binary), out, buffer);
    }

    addZipEntry(ExportConstants.MANIFEST + ".json", asBytes(manifest), out, buffer);
    out.closeEntry();
    out.close();
    return outputStream.toByteArray();
  }

  private byte[] asBytes(String document) {
    return document.getBytes(StandardCharsets.UTF_8);
  }

  private void addZipEntry(String filename,
                           byte[] document,
                           ZipOutputStream out,
                           byte[] buffer) throws IOException {
    ZipEntry ze = new ZipEntry(filename);
    out.putNextEntry(ze);

    try (InputStream in = new ByteArrayInputStream(document)) {
      int len;
      while ((len = in.read(buffer)) > 0) {
        out.write(buffer, 0, len);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
