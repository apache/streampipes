/*
Copyright 2020 FZI Forschungszentrum Informatik

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

    public ZipFileImageIterator(String zipFileRoute) throws IOException {
        this.zipFile = new ZipFile(zipFileRoute);

        Enumeration<? extends ZipEntry> entries = zipFile.entries();

        this.allImages = new ArrayList<>();

        while(entries.hasMoreElements()){
            ZipEntry entry = entries.nextElement();
            if (isImage(entry.getName())){
                allImages.add(entry);
            }
        }
        current = 0;

    }

    public boolean hasNext() {
        return current < this.allImages.size();
    }

    public String next() throws IOException {
        ZipEntry entry = allImages.get(current);
        InputStream stream = zipFile.getInputStream(entry);
        byte[] bytes = IOUtils.toByteArray(stream);

        current++;
        String resultImage = Base64.getEncoder().encodeToString(bytes);
        return entry.getName();
    }

    public static void main(String... args) throws IOException {
        String route = "";
        ZipFileImageIterator zipFileImageIterator = new ZipFileImageIterator(route);

        while (zipFileImageIterator.hasNext()) {
            System.out.println(zipFileImageIterator.next());
        }

    }

    private static boolean isImage(String name) {
        return (!name.startsWith("_")) &&
                (name.toLowerCase().endsWith(".png") ||
                        name.toLowerCase().endsWith(".jpg") ||
                        name.toLowerCase().endsWith(".jpeg"));

    }
}
