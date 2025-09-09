/**
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

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.time.Instant;

public class LocalFolder extends IObjectStorage{
    
 private final Path filePath;

    public LocalFolder(StreamingResponseBody datastream, String measurementName) throws Exception {
        super(datastream);

        Files.createDirectories(Paths.get(System.getProperty("SP_RETENTION_LOCAL_DIR")+"/"+measurementName));

        this.filePath = Paths.get(System.getProperty("SP_RETENTION_LOCAL_DIR")+"/"+measurementName+"/dump_"+Instant.now().toString());


    }

    @Override
    public void store() throws Exception {
        try (OutputStream outputStream = new FileOutputStream(filePath.toFile())) {
            this.datastream.writeTo(outputStream);
        }
    }



}
