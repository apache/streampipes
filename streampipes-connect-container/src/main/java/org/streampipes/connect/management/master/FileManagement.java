/*
Copyright 2018 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.streampipes.connect.management.master;

import org.apache.commons.io.IOUtils;
import org.streampipes.connect.adapter.exception.AdapterException;
import org.streampipes.connect.config.ConnectContainerConfig;
import org.streampipes.model.connect.worker.ConnectWorkerContainer;
import org.streampipes.storage.couchdb.impl.ConnectionWorkerContainerStorageImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FileManagement {

  private ConnectionWorkerContainerStorageImpl connectionWorkerContainerStorage;

  public FileManagement() {
      this.connectionWorkerContainerStorage = new ConnectionWorkerContainerStorageImpl();
  }

  @Deprecated
  public String saveFile(InputStream inputStream, String fileName) throws IOException {
      String filePath = getMainFilePath() + fileName;
      saveFile(filePath, inputStream);
      return filePath;
  }

  public String saveFileAtWorker(String appId, InputStream inputStream, String fileName, String userName) throws AdapterException {
      String workerUrl = new Utils().getWorkerUrlById(appId);
      String newUrl = Utils.addUserNameToApi(workerUrl, userName);
      return WorkerRestClient.saveFileAtWorker(newUrl, inputStream, fileName);
  }

  @Deprecated
  public List<String> getFilePahts(String username) {
      List<String> urls = new ArrayList<>();
      File[] files = new File(getMainFilePath()).listFiles();
      for (int i = 0; i < files.length; i++) {
          urls.add(getMainFilePath() + files[i].getName());
      }

      return urls;
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

  @Deprecated
  public File getFile(String name) throws IOException {
      File file = new File(getMainFilePath() + name);
      if(file.exists()) {
          return file;
      } else {
          throw new IOException();
      }
  }

  public InputStream getFileFromWorker(String appId, String fileName, String userName) throws AdapterException {
      String workerUrl = new Utils().getWorkerUrlById(appId);
      String newUrl = Utils.addUserNameToApi(workerUrl, userName);
      return WorkerRestClient.getFileFromWorker(newUrl, fileName);
  }

  @Deprecated
  public void deleteFile(String name) throws IOException {
      File file = new File(getMainFilePath() + name);
      if(file.exists()) {
          file.delete();
      } else {
          throw new IOException("File" + name + "is not excisting");
      }
  }

  public void deleteFileFromWorker(String appId, String fileName, String userName) throws AdapterException {
      //TODO: if have more than connect-worker: use 'app-id' information to find the correct connect-container
      List<ConnectWorkerContainer> allConnectWorkerContainer = this.connectionWorkerContainerStorage.getAllConnectWorkerContainers();
      String workerUrl = allConnectWorkerContainer.get(0).getEndpointUrl();
      //String workerUrl = new Utils().getWorkerUrlById(appId);
      String newUrl = Utils.addUserNameToApi(workerUrl, userName);
      WorkerRestClient.deleteFileFromWorker(newUrl, fileName);
  }

  @Deprecated
  private void saveFile(String filePath, InputStream inputStream ) throws IOException {
      File file = new File(filePath);
      file.getParentFile().mkdirs();
      file.createNewFile();
      byte[] aByte = IOUtils.toByteArray(inputStream);
      FileOutputStream fos =new FileOutputStream(file);
      IOUtils.write(aByte, fos);
  }

  @Deprecated
  private String getMainFilePath() {
      return ConnectContainerConfig.INSTANCE.getDataLocation();
  }

}
