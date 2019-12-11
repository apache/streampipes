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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFileGenerator {

  private List<String> fileList;

  private File inputDirectory;
  private File outputFile;

  public ZipFileGenerator(File inputDirectory, File outputFile) {
    this(inputDirectory);
    this.outputFile = outputFile;
  }

  public ZipFileGenerator(File inputDirectory) {
    this.fileList = new ArrayList<>();
    this.inputDirectory = inputDirectory;
    generateFileList(inputDirectory);
  }

  public byte[] makeZipToBytes() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    makeZip(bos);
    return bos.toByteArray();
  }

  public void makeZipToFile() {
    try {
      FileOutputStream fos = new FileOutputStream(outputFile);
      makeZip(fos);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public void makeZip(OutputStream outputStream) {
    byte[] buffer = new byte[1024];
    ZipOutputStream zos = null;
    try {
      zos = new ZipOutputStream(outputStream);

      FileInputStream in = null;

      for (String file : this.fileList) {
        ZipEntry ze = new ZipEntry(file);
        zos.putNextEntry(ze);
        try {
          in = new FileInputStream(inputDirectory + File.separator + file);
          int len;
          while ((len = in.read(buffer)) > 0) {
            zos.write(buffer, 0, len);
          }
        } finally {
          in.close();
        }
      }
      zos.closeEntry();
    } catch (IOException ex) {
      ex.printStackTrace();
    } finally {
      try {
        zos.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void generateFileList(File node) {

    if (node.isFile()) {
      fileList.add(generateZipEntry(node.toString()));
    }

    if (node.isDirectory()) {
      String[] subNote = node.list();
      for (String filename : subNote) {
        generateFileList(new File(node, filename));
      }
    }
  }

  private String generateZipEntry(String file) {
    return file.substring(inputDirectory.toString().length() + 1, file.length());
  }

}
