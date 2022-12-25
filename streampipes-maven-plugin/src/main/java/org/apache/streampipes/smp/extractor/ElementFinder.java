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

import java.util.List;

public abstract class ElementFinder {

  protected String sourceRoot;
  protected Log log;
  protected String baseDir;

  public ElementFinder(String sourceRoot, Log log, String baseDir) {
    this.sourceRoot = sourceRoot;
    this.log = log;
    this.baseDir = baseDir;
  }

  public abstract List<AssetModel> makeAssetModels();
}
