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

package org.apache.streampipes.extensions.connectors.plc.adapter.generic.assets;

import org.apache.streampipes.commons.constants.GlobalStreamPipesConstants;
import org.apache.streampipes.extensions.api.assets.DefaultAssetResolver;

import org.apache.plc4x.java.api.PlcDriver;

import java.io.IOException;
import java.util.Properties;

public class PlcAdapterAssetResolver extends DefaultAssetResolver {

  private final String appId;
  private final PlcDriver driver;

  public PlcAdapterAssetResolver(String folderName,
                                 String appId,
                                 PlcDriver driver) {
    super(folderName);
    this.appId = appId;
    this.driver = driver;
  }

  @Override
  public Properties getLocale(ClassLoader classLoader,
                              String localeName) throws IOException {
    var properties = super.getLocale(classLoader, localeName);
    properties.put(appId + ".title", driver.getProtocolName());
    properties.put(appId + ".description", "");

    return properties;
  }

  @Override
  public byte[] getAsset(ClassLoader classLoader,
                         String assetName) throws IOException {
    if (assetName.equals(GlobalStreamPipesConstants.STD_DOCUMENTATION_NAME)) {
      var docsTemplate = new String(getResourceFile(classLoader, assetName).readAllBytes());
      return new DocumentationGenerator(driver, docsTemplate).generateDocumentation();
    } else {
      return super.getAsset(classLoader, assetName);
    }
  }
}
