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

package org.apache.streampipes.connect.adapters.image;

import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.extensions.management.client.StreamPipesClientResolver;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipFileImageIterator {

  private static final int BUFFER_SIZE = 4096;

  private List<byte[]> allImages;
  private int current;

  /* Defines whether the iterator starts from the beginning or not */
  private boolean infinite;

  public ZipFileImageIterator(String zipFileUrl, boolean infinite) throws IOException {
    ZipInputStream inputStream = fetchZipInputStream(zipFileUrl);
    this.infinite = infinite;

    ZipEntry entry;
    this.allImages = new ArrayList<>();

    while ((entry = inputStream.getNextEntry()) != null) {
      if (isImage(entry.getName())) {
        allImages.add(extractFile(inputStream));
      }
    }
    this.current = 0;

  }

  public boolean hasNext() {
    return infinite || current < this.allImages.size();
  }

  public String next() throws IOException {

    // Reset the current file counter when infinite is true and iterator is at the end
    if (infinite) {
      if (current >= this.allImages.size()) {
        this.current = 0;
      }
    }

    byte[] bytes = allImages.get(current);

    current++;
    String resultImage = Base64.getEncoder().encodeToString(bytes);
    return resultImage;
  }

  private static boolean isImage(String name) {
    return (!name.startsWith("_"))
        && (name.toLowerCase().endsWith(".png")
        || name.toLowerCase().endsWith(".jpg")
        || name.toLowerCase().endsWith(".jpeg"));

  }

  private ZipInputStream fetchZipInputStream(String filename) {
    StreamPipesClient client = new StreamPipesClientResolver().makeStreamPipesClientInstance();
    byte[] result = client.fileApi().getFileContent(filename);
    return new ZipInputStream(new ByteArrayInputStream(result));
  }

  private byte[] extractFile(ZipInputStream zipIn) throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    BufferedOutputStream bos = new BufferedOutputStream(outputStream);
    byte[] bytesIn = new byte[BUFFER_SIZE];
    int read = 0;
    while ((read = zipIn.read(bytesIn)) != -1) {
      bos.write(bytesIn, 0, read);
    }
    bos.close();
    byte[] bytes = outputStream.toByteArray();
    outputStream.close();
    return bytes;
  }
}
