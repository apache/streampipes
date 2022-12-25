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

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class DockerImageExtractor {

  private static final String Deployment = "deployment";
  private static final String DockerCompose = "docker-compose.yml";
  private static final String Image = "image";
  private static final String Services = "services";
  private static final String Colon = ":";
  private static final String ImagePrefix = "${SP_PE_DOCKER_REGISTRY}/streampipes"
      + "/streampipes"
      + "-pipeline-elements/";

  private String baseDir;

  public DockerImageExtractor(String baseDir) {
    this.baseDir = baseDir;
  }

  public String extractImageName() {
    Path dockerComposePath = Paths.get(baseDir, Deployment, DockerCompose);

    Yaml yaml = new Yaml();
    try {
      Map<String, Object> fileContents =
          yaml.load(new String(Files.readAllBytes(dockerComposePath)));

      return extractNameFromYaml(fileContents);

    } catch (IOException e) {
      e.printStackTrace();
      return "";
    }
  }

  public String extractNameFromYaml(Map<String, Object> fileContents) {
    Map<String, Object> services = (Map<String, Object>) fileContents.get(Services);
    AtomicReference<String> imageName = new AtomicReference<>("");
    if (services.size() > 0) {
      services.forEach((key, value) -> imageName.set(parseValue(((Map<String,
          Object>) value).get(Image).toString())));
    }

    return imageName.get();
  }

  private String parseValue(String imageName) {
    return imageName.replace(ImagePrefix, "").split(Colon)[0];
  }
}
