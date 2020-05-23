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

package org.apache.streampipes.connect.adapters.image;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipFileImageIterator {
    private ZipFile zipFile;
    private List<ZipEntry> allImages;
    private int current;

    /* Defines whether the iterator starts from the beginning or not */
    private boolean infinite;

    public ZipFileImageIterator(String zipFileRoute, boolean infinite) throws IOException {
        this.zipFile = new ZipFile(zipFileRoute);
        this.infinite = infinite;

        Enumeration<? extends ZipEntry> entries = zipFile.entries();

        this.allImages = new ArrayList<>();

        while(entries.hasMoreElements()){
            ZipEntry entry = entries.nextElement();
            if (isImage(entry.getName())){
                allImages.add(entry);
            }
        }
        this.current = 0;

    }

    public boolean hasNext() {
        return infinite || current < this.allImages.size();
    }

    public String next() throws IOException {

        // Reset the current file counter when infinite is true and iterator is at the end
        if (infinite) {
            if (current >= this.allImages.size()) {
                this.current = 0;
            }
        }

        ZipEntry entry = allImages.get(current);
        InputStream stream = zipFile.getInputStream(entry);
        byte[] bytes = IOUtils.toByteArray(stream);

        current++;
        String resultImage = Base64.getEncoder().encodeToString(bytes);
        return resultImage;
    }

    private static boolean isImage(String name) {
        return (!name.startsWith("_")) &&
                (name.toLowerCase().endsWith(".png") ||
                        name.toLowerCase().endsWith(".jpg") ||
                        name.toLowerCase().endsWith(".jpeg"));

    }
}
