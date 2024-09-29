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
import org.apache.streampipes.commons.file.FileHasher;
import org.apache.streampipes.extensions.management.client.StreamPipesClientResolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileProtocolUtils {
  private static final Logger LOG = LoggerFactory.getLogger(FileProtocolUtils.class);

  public static InputStream getFileInputStream(String selectedFilename) throws FileNotFoundException {
    if (!isFilePresent(selectedFilename) || checkIfFileChanged(selectedFilename)) {
      try {
        storeFileLocally(selectedFilename);
        LOG.info("Cache file locally: %s".formatted(selectedFilename));
      } catch (IOException e) {
        throw new ParseException("Could not receive file");
      }
    }

    return new FileInputStream(makeFileLoc(selectedFilename));
  }

  /**
   * Checks if the file has changed in the backend since the last time it was read This prevents that the cached file is
   * invalid, or was updated by the user
   * 
   * @param selectedFilename
   *          name of the file
   * @return whether the content of the file has changed or not
   */
  private static boolean checkIfFileChanged(String selectedFilename) {
    try {
      var hash = new FileHasher().hash(getFile(selectedFilename));
      StreamPipesClient client = getStreamPipesClientInstance();
      return client.fileApi().checkFileContentChanged(selectedFilename, hash);
    } catch (IOException e) {
      throw new ParseException("Could not read file with filename: %s".formatted(selectedFilename));
    }
  }

  private static boolean isFilePresent(String selectedFilename) {
    var file = getFile(selectedFilename);
    return file.exists();
  }

  private static File getFile(String selectedFilename) {
    return new File(makeFileLoc(selectedFilename));
  }

  private static void storeFileLocally(String selectedFilename) throws IOException {
    File storageDir = new File(makeServiceStorageDir());
    if (!storageDir.exists()) {
      storageDir.mkdirs();
    }
    StreamPipesClient client = getStreamPipesClientInstance();

    client.fileApi().writeToFile(selectedFilename, makeFileLoc(selectedFilename));
  }

  private static String makeServiceStorageDir() {
    return System.getProperty("user.home") + File.separator + ".streampipes" + File.separator + "service";
  }

  private static String makeFileLoc(String filename) {
    return makeServiceStorageDir() + File.separator + filename;
  }

  private static StreamPipesClient getStreamPipesClientInstance() {
    return new StreamPipesClientResolver().makeStreamPipesClientInstance();
  }

}
