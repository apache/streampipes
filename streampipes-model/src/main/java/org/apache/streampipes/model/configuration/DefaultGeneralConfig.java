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

package org.apache.streampipes.model.configuration;

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.environment.Environments;

import java.io.File;

public class DefaultGeneralConfig {

  public GeneralConfig make() {
    var generalConfig = new GeneralConfig();
    generalConfig.setLinkSettings(new DefaultLinkSettings().make());
    generalConfig.setAssetDir(makeAssetLocation());
    generalConfig.setFilesDir(makeFileLocation());
    return generalConfig;
  }

  public String makeAssetLocation() {
    return makeStreamPipesHomeLocation()
        + "assets";
  }

  public String makeFileLocation() {
    return makeStreamPipesHomeLocation()
        + "files";
  }

  private String makeStreamPipesHomeLocation() {
    var userDefinedAssetDir = getEnvironment().getCoreAssetBaseDir();
    var assetDirAppendix = getSpAssetDirAppendix();
    if (userDefinedAssetDir.exists()) {
      return userDefinedAssetDir.getValue() + assetDirAppendix;
    } else {
      return System.getProperty("user.home") + assetDirAppendix;
    }
  }

  private String getSpAssetDirAppendix() {
    return File.separator + ".streampipes" + File.separator;
  }

  private Environment getEnvironment() {
    return Environments.getEnvironment();
  }
}
