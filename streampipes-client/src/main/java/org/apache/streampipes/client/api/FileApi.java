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

package org.apache.streampipes.client.api;

import org.apache.streampipes.client.http.BinaryGetRequest;
import org.apache.streampipes.client.model.StreamPipesClientConfig;
import org.apache.streampipes.client.util.StreamPipesApiPath;


public class FileApi extends AbstractClientApi implements IFileApi {

  public FileApi(StreamPipesClientConfig clientConfig) {
    super(clientConfig);
  }


  /**
   * @deprecated As of release 0.95.0, replaced by {@link #getFileContent(String)}.
   * The parameter isOriginalFileName is not used anymore.
   */
  @Deprecated(since = "0.95.0", forRemoval = true)
  public byte[] getFileContent(String filename, boolean isOriginalFileName) {
    return this.getFileContent(filename);
  }

  @Override
  public byte[] getFileContent(String filename) {
    return new BinaryGetRequest(
        clientConfig,
        getBaseResourcePath(filename),
        null
    )
        .executeRequest();
  }

  @Override
  public String getFileContentAsString(String filename) {
    return new String(getFileContent(filename));
  }

  @Override
  public void writeToFile(String file, String fileLocation) {
    new BinaryGetRequest(clientConfig, getBaseResourcePath(file), null)
        .writeToFile(fileLocation);
  }

  @Override
  public boolean checkFileContentChanged(String filename, String hash) {
    return getSingle(
        getBaseResourcePath(filename)
            .addToPath("checkFileContentChanged")
            .addToPath(hash),
        Boolean.class
    );
  }

  protected StreamPipesApiPath getBaseResourcePath(String fileName) {
    return StreamPipesApiPath.fromBaseApiPath()
                             .addToPath("files")
                             .addToPath(fileName);
  }
}
