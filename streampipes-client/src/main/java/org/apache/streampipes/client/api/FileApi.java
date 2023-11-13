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

import java.util.Map;


public class FileApi extends AbstractClientApi implements IFileApi {

  public FileApi(StreamPipesClientConfig clientConfig) {
    super(clientConfig);
  }

  public byte[] getFileContent(String filename, boolean isOriginalFileName) {
    return new BinaryGetRequest(clientConfig, getBaseResourcePath(filename)
            .withQueryParameters(Map.of("isOriginalFilename", String.valueOf(isOriginalFileName))), null)
            .executeRequest();
  }
  @Override
  public byte[] getFileContent(String fileId) {
    return getFileContent(fileId, false);
  }

  public String getFileContentAsString(String filename, boolean isOriginalFileName) {
    return new String(getFileContent(filename, isOriginalFileName));
  }

  @Override
  public String getFileContentAsString(String fileId) {
    return getFileContentAsString(fileId, false);
  }

  @Override
  public void writeToFile(String file, String fileLocation) {
    new BinaryGetRequest(clientConfig, getBaseResourcePath(file), null)
        .writeToFile(fileLocation);
  }

  protected StreamPipesApiPath getBaseResourcePath(String fileName) {
    return StreamPipesApiPath.fromBaseApiPath()
        .addToPath("files")
        .addToPath(fileName);
  }
}
