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
package org.apache.streampipes.extensions.management.assets;

import org.apache.streampipes.extensions.api.assets.AssetResolver;
import org.apache.streampipes.extensions.api.assets.DefaultAssetResolver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssetZipGenerator {

  private static final Logger LOG = LoggerFactory.getLogger(AssetZipGenerator.class);

  private final List<String> includedAssets;

  private final AssetResolver assetResolver;

  public AssetZipGenerator(String appId, List<String> includedAssets) {
    this.includedAssets = includedAssets;
    this.assetResolver = new DefaultAssetResolver(appId);
  }

  public AssetZipGenerator(List<String> includedAssets, AssetResolver assetResolver) {
    this.includedAssets = includedAssets;
    this.assetResolver = assetResolver;
  }

  public byte[] makeZip() throws IOException {
    return makeZipFromAssets();
  }

  private byte[] makeZipFromAssets() throws IOException {
    byte[] buffer = new byte[1024];

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ZipOutputStream out = new ZipOutputStream(outputStream);

    for (String asset : includedAssets) {
      ZipEntry ze = new ZipEntry(asset);
      out.putNextEntry(ze);

      try (InputStream in = new ByteArrayInputStream(assetResolver.getAsset(asset))) {
        int len;
        while ((len = in.read(buffer)) > 0) {
          out.write(buffer, 0, len);
        }
      } catch (Exception e) {
        LOG.error("Could not resolve assets", e);
      }
    }
    out.closeEntry();
    out.close();
    return outputStream.toByteArray();
  }
}
