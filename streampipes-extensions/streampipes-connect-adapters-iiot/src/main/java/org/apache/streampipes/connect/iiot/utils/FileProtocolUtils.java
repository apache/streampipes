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

package org.apache.streampipes.connect.iiot.utils;

import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.commons.exceptions.connect.ParseException;
import org.apache.streampipes.extensions.management.client.StreamPipesClientResolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FileProtocolUtils {

  public static InputStream getFileInputStream(String selectedFilename) throws FileNotFoundException {
    if (!isFilePresent(selectedFilename)) {
      try {
        storeFileLocally(selectedFilename);
      } catch (IOException e) {
        throw new ParseException("Could not receive file");
      }
    }

    return new FileInputStream(makeFileLoc(selectedFilename));
  }

  private static boolean isFilePresent(String selectedFilename) {
    File file = new File(makeFileLoc(selectedFilename));
    return file.exists();
  }

  private static void storeFileLocally(String selectedFilename) throws IOException {
    File storageDir = new File(makeServiceStorageDir());
    if (!storageDir.exists()) {
      storageDir.mkdirs();
    }
    StreamPipesClient client = new StreamPipesClientResolver().makeStreamPipesClientInstance();

    client.fileApi().writeToFile(selectedFilename, makeFileLoc(selectedFilename));
  }

  private static String makeServiceStorageDir() {
    return System.getProperty("user.home")
        + File.separator
        + ".streampipes"
        + File.separator
        + "service";
  }

  private static String makeFileLoc(String filename) {
    return makeServiceStorageDir() + File.separator + filename;
  }

}
