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

public class MarkdownHeaderGenerator {

  private AssetModel assetModel;
  private String markdown;

  public MarkdownHeaderGenerator(AssetModel assetModel, String markdown) {
    this.assetModel = assetModel;
    this.markdown = markdown;
  }

  public String createHeaders() {
    StringBuilder builder = new StringBuilder();
    builder
        .append("---\n")
        .append("id: ")
        .append(assetModel.getAppId())
        .append("\n")
        .append("title: ")
        .append(assetModel.getPipelineElementName())
        .append("\n")
        .append("sidebar_label: ")
        .append(assetModel.getPipelineElementName())
        .append("\n")
        .append("---\n\n")
        .append(markdown);

    return builder.toString();
  }
}
