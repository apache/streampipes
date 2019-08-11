/*
Copyright 2019 FZI Forschungszentrum Informatik

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

package org.streampipes.connect.container.worker.management;

import org.apache.commons.io.IOUtils;
import org.streampipes.connect.config.ConnectContainerConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FileManagement {

    public String saveFile(InputStream inputStream, String fileName) throws IOException {
        String filePath = getMainFilePath() + fileName;
        saveFile(filePath, inputStream);
        return filePath;
    }

    public List<String> getFilePaths() {
        List<String> urls = new ArrayList<>();
        File[] files = new File(getMainFilePath()).listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                urls.add(getMainFilePath() + files[i].getName());
            }
        }

        return urls;
    }

    public File getFile(String name) throws IOException {
        File file = new File(getMainFilePath() + name);
        if(file.exists()) {
            return file;
        } else {
            throw new IOException();
        }
    }

    public void deleteFile(String name) throws IOException {
        File file = new File(getMainFilePath() + name);
        if(file.exists()) {
            file.delete();
        } else {
            throw new IOException("File" + name + "is not existing");
        }
    }

    private void saveFile(String filePath, InputStream inputStream) throws IOException {
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        file.createNewFile();
        byte[] aByte = IOUtils.toByteArray(inputStream);
        FileOutputStream fos = new FileOutputStream(file);
        IOUtils.write(aByte, fos);
    }

    private String getMainFilePath() {
        String dataLocation = System.getenv("SP_DATA_LOCATION");
        if (dataLocation == null) {
            dataLocation = "/data/";
        }
        return dataLocation;
    }




}
