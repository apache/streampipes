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
import org.streampipes.connect.config.ConnectContainerConfig;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileManagement {

    public void saveFile(InputStream inputStream, String fileName) throws IOException {
        String filePath = getMainFilePath() + fileName;
        saveFile(filePath, inputStream);
    }

    public List<String> getUrls(String username) throws IOException {
        String urlPrefix = ConnectContainerConfig.INSTANCE.getConnectContainerMasterUrl()+ "api/v1/" +
                username + "/master/file/";

        List<String> urls = new ArrayList<>();
        File[] files = new File(getMainFilePath()).listFiles();
        for (int i = 0; i < files.length; i++) {
            urls.add(urlPrefix + files[i].getName());
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
            throw new IOException("File" + name + "is not excisting");
        }
    }

    private void saveFile(String filePath, InputStream inputStream ) throws IOException {
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        file.createNewFile();
        byte[] aByte = IOUtils.toByteArray(inputStream);
        FileOutputStream fos =new FileOutputStream(file);
        IOUtils.write(aByte, fos);
    }

    private String getMainFilePath() {
        return ConnectContainerConfig.INSTANCE.getDataLocation();
    }




}
