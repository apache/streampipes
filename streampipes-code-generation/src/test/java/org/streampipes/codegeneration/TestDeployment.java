/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.streampipes.codegeneration;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.streampipes.codegeneration.api.CodeGenerator;
import org.streampipes.model.client.deployment.DeploymentConfiguration;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.serializers.json.GsonSerializer;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class TestDeployment {

  public static void main(String[] args) {
    DeploymentConfiguration deploymentConfiguration = GsonSerializer.getGsonWithIds().fromJson(fromResources("DeploymentConfig.json"), DeploymentConfiguration.class);
    DataProcessorDescription dataProcessorDescription = GsonSerializer.getGsonWithIds().fromJson(fromResources("Entity.json"), DataProcessorDescription.class);

    File f = CodeGenerator.getCodeGenerator(deploymentConfiguration, dataProcessorDescription).getGeneratedFile();
  }

  private static String fromResources(String filename) {
    URL url = Resources.getResource(filename);
    try {
      return Resources.toString(url, Charsets.UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
      return "";
    }
  }
}
