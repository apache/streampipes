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

package org.apache.streampipes.dataexplorer.export.ObjectStorge;


import org.apache.streampipes.model.configuration.ExportProviderSettings;
import org.apache.streampipes.model.configuration.ProviderType;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class TestExportProviderConnection {

    public static Map<String, Object> connectionTest(ExportProviderSettings setting) throws Exception {

        ProviderType providerType = setting.getProviderType();

        IObjectStorage exportProvider = ExportProviderFactory.createExportProvider(
            providerType, "TEST", setting,
            "csv");


        String filePath = exportProvider.getFileName();

        String csvData = "Message\nThis test file was automatically created as a connectivity test by StreamPipes.\n";
        InputStream csvInputStream = new ByteArrayInputStream(csvData.getBytes());
    StreamingResponseBody responseBody = outputStream -> {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = csvInputStream.read(buffer)) > 0) {
          outputStream.write(buffer, 0, length);
        }
      };
      try {
        exportProvider.store(responseBody);
      } catch (IOException e) {
        throw new IOException("Failed to store data in the export provider.",e);

      }


      
        Map<String, Object> response = new HashMap<>();
        response.put("filePath", filePath);
        response.put("setting", setting);

        return response;
    }
}