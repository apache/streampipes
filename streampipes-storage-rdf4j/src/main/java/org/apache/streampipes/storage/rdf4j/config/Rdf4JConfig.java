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

package org.apache.streampipes.storage.rdf4j.config;


import org.apache.streampipes.config.SpConfig;

import java.io.File;

public enum Rdf4JConfig {
  INSTANCE;

  private SpConfig config;

  Rdf4JConfig() {
    config = SpConfig.getSpConfig("storage/rdf4j");

    config.register(ConfigKeys.BACKGROUND_KNOWLEDGE_DIR, makeBackgroundStorageLocation(), "Directory of " +
            "the RDF4J native store directory (background knowledge)");
    config.register(ConfigKeys.PIPELINE_ELEMENT_KNOWLEDGE_DIR, makePipelineElementStorageLocation(), "Directory of " +
            "the RDF4J native store directory (pipeline element knowledge)");
  }

  private String makeBackgroundStorageLocation() {
    return makeStorageLocation()
            + "background";
  }

  private String makePipelineElementStorageLocation() {
    return makeStorageLocation()
            + "pipelineelements";
  }

  private String makeStorageLocation() {
    return System.getProperty("user.home")
            + File.separator
            + ".streampipes"
            + File.separator
            + "storage"
            + File.separator;
  }

  public String getBackgroundKnowledgeStorageLocation() {
      return config.getString(ConfigKeys.BACKGROUND_KNOWLEDGE_DIR);
  }

  public String getPipelineElementStorageLocation() {
      return config.getString(ConfigKeys.PIPELINE_ELEMENT_KNOWLEDGE_DIR);
  }


}
