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
import org.apache.streampipes.smp.util.Utils;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class LocalesExtractor {

  private static final String TITLE = ".title";
  private static final String DESCRIPTION = ".description";
  private AssetModel assetModel;
  private String baseDir;

  public LocalesExtractor(String baseDir, AssetModel assetModel) {
    this.baseDir = baseDir;
    this.assetModel = assetModel;
  }

  public AssetModel extract() throws IOException {
    String localesPath = Utils.makeLocalesPath(baseDir, assetModel.getAppId());
    Properties props = loadProperties(localesPath);

    assetModel.setPipelineElementName(extractKey(props, assetModel, TITLE));
    assetModel.setPipelineElementDescription(extractKey(props, assetModel, DESCRIPTION));

    return assetModel;
  }

  private String extractKey(Properties props, AssetModel assetModel, String appendix) {
    return props.getProperty(assetModel.getAppId() + appendix);
  }

  private Properties loadProperties(String localesPath) throws IOException {
    Properties props = new Properties();
    props.load(new FileReader(localesPath));
    return props;
  }
}
