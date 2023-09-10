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

package org.apache.streampipes.smp.extractor;

import org.apache.streampipes.smp.model.AssetModel;

import org.apache.maven.plugin.logging.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LocalesExtractor {

  public static final String LOCALES_FILE_EN = "strings.en";

  private static final String TITLE = ".title";
  private static final String DESCRIPTION = ".description";

  private final Log log;
  private final ClassLoader loader;

  public LocalesExtractor(Log log,
                          ClassLoader loader) {
    this.log = log;
    this.loader = loader;
  }

  public void applyLocales(AssetModel assetModel) {
    var localeFile = loader.getResourceAsStream(assetModel.getAppId() + "/" + LOCALES_FILE_EN);
    try {
      var props = loadProperties(localeFile);
      assetModel.setPipelineElementName(extractKey(props, assetModel, TITLE));
      assetModel.setPipelineElementDescription(extractKey(props, assetModel, DESCRIPTION));
    } catch (IOException e) {
      log.warn(String.format("Could not load properties file %s", assetModel.getAppId()), e);
    }
  }

  private String extractKey(Properties props, AssetModel assetModel, String appendix) {
    return props.getProperty(assetModel.getAppId() + appendix);
  }

  private Properties loadProperties(InputStream stream) throws IOException {
    Properties props = new Properties();
    props.load(stream);
    return props;
  }
}
