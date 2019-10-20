/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * you may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.streampipes.manager.file;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class FileHandler {

  public void storeFile(String filename, InputStream fileInputStream) throws IOException {
    File targetFile = makeFile(filename);
    FileUtils.copyInputStreamToFile(fileInputStream, targetFile);
  }

  public void deleteFile(String filename) {
    File targetFile = makeFile(filename);
    targetFile.delete();
  }

  public File getFile(String filename) {
    return FileUtils.getFile(makeFile(filename));
  }

  private File makeFile(String filename) {
    File fileDir = new File(makeFileLocation());
    if (!fileDir.exists()) {
      fileDir.mkdirs();
    }
    return new File(makeFileLocation() + File.separator + filename);
  }

  private String makeFileLocation() {
    return FileConstants.FILES_BASE_DIR
            + File.separator;
  }
}
