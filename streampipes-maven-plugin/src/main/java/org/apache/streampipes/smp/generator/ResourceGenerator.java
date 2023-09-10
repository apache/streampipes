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

package org.apache.streampipes.smp.generator;

import org.apache.streampipes.smp.model.AssetModel;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public abstract class ResourceGenerator {

  protected final ClassLoader loader;
  protected final AssetModel extensionsElement;
  protected final Path targetPath;

  public ResourceGenerator(ClassLoader loader,
                           AssetModel extensionsElement,
                           Path targetPath) {
    this.loader = loader;
    this.extensionsElement = extensionsElement;
    this.targetPath = targetPath;
  }

  protected InputStream getResourceInputStream(String resourceName) {
    return loader.getResourceAsStream(extensionsElement.getAppId() + "/" + resourceName);
  }

  public abstract void generate() throws IOException;
}
