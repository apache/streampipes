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

package org.apache.streampipes.connect.container.master.management;

import org.apache.streampipes.connect.adapter.exception.AdapterException;
import org.apache.streampipes.model.connect.worker.ConnectWorkerContainer;
import org.apache.streampipes.storage.couchdb.impl.ConnectionWorkerContainerStorageImpl;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class FileManagement {

  private ConnectionWorkerContainerStorageImpl connectionWorkerContainerStorage;

  public FileManagement() {
      this.connectionWorkerContainerStorage = new ConnectionWorkerContainerStorageImpl();
  }

  public String saveFileAtWorker(String appId, InputStream inputStream, String fileName, String userName) throws AdapterException {
      appId = appId.replaceAll("sp:", "https://streampipes.org/vocabulary/v1/");

      String workerUrl = new Utils().getWorkerUrlById(appId);
      String newUrl = Utils.addUserNameToApi(workerUrl, userName);
      return WorkerRestClient.saveFileAtWorker(newUrl, inputStream, fileName);
  }


  public List<String> getAllFilePathsFromWorker(String username) throws AdapterException {
      List<String> filePaths = new LinkedList<>();

      //TODO: if have more than connect-worker: add information from which container is the file
      List<ConnectWorkerContainer> allConnectWorkerContainer = this.connectionWorkerContainerStorage.getAllConnectWorkerContainers();
      for (ConnectWorkerContainer connectWorkerContainer : allConnectWorkerContainer) {
          String workerUrl = connectWorkerContainer.getEndpointUrl();
          String newUrl = Utils.addUserNameToApi(workerUrl, username);
          List<String> paths = WorkerRestClient.getAllFilePathsFromWorker(newUrl);
          filePaths.addAll(paths);
      }

      return filePaths;
    }

  public InputStream getFileFromWorker(String appId, String fileName, String userName) throws AdapterException {
      String workerUrl = new Utils().getWorkerUrlById(appId);
      String newUrl = Utils.addUserNameToApi(workerUrl, userName);
      return WorkerRestClient.getFileFromWorker(newUrl, fileName);
  }


  public void deleteFileFromWorker(String appId, String fileName, String userName) throws AdapterException {
      appId = appId.replaceAll("sp:", "https://streampipes.org/vocabulary/v1/");

      //TODO: if have more than connect-worker: use 'app-id' information to find the correct connect-container
      List<ConnectWorkerContainer> allConnectWorkerContainer = this.connectionWorkerContainerStorage.getAllConnectWorkerContainers();
      String workerUrl = allConnectWorkerContainer.get(0).getEndpointUrl();
      //String workerUrl = new Utils().getWorkerUrlById(appId);
      String newUrl = Utils.addUserNameToApi(workerUrl, userName);
      WorkerRestClient.deleteFileFromWorker(newUrl, fileName);
  }

}
