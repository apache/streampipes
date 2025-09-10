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

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

public class LocalFolder implements IObjectStorage {

    private final Path filePath;

    public LocalFolder(String measurementName, String format) throws Exception {

        Files.createDirectories(Paths.get(System.getenv("SP_RETENTION_LOCAL_DIR") + "/" + measurementName));

        this.filePath = Paths.get(System.getenv("SP_RETENTION_LOCAL_DIR") + "/" + measurementName + "/dump_"
                + Instant.now().toString() + "." + format);

    }

    @Override
    public void store(StreamingResponseBody datastream) throws IOException {
        try (OutputStream outputStream = new FileOutputStream(filePath.toFile())) {
            datastream.writeTo(outputStream);
        }
    }

}
