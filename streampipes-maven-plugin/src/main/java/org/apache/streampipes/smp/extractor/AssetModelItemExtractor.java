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

import org.apache.streampipes.smp.constants.PeType;
import org.apache.streampipes.smp.model.AssetModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AssetModelItemExtractor {

  private static final String REGEX = "(DataSinkBuilder|ProcessingElementBuilder)\\.create\\"
      + "((\".*?\"),.*?(\".*?\"),.*?(\".*?\")\\)";

  private static final String ONLY_APP_ID_REGEX = "(DataSinkBuilder|ProcessingElementBuilder)\\"
      + ".create\\"
      + "((\".*?\")\\)";

  private static final Pattern pattern = Pattern.compile(REGEX);
  private static final Pattern onlyAppIdPattern = Pattern.compile(ONLY_APP_ID_REGEX);

  private String declareModelContent;

  public AssetModelItemExtractor(String declareModelContent) {
    this.declareModelContent = declareModelContent
        .replaceAll("\n", "");

  }


  public AssetModel extractAssetItem() throws IllegalArgumentException {
    AssetModel assetModel = new AssetModel();
    Matcher matcher = pattern.matcher(this.declareModelContent);
    Boolean matches = false;

//    while (matcher.find()) {
//      matches = true;
//      PeType peType = new PipelineElementTypeExtractor(matcher.group(1)).extractType();
//      String appId = removeQuotes(matcher.group(2));
//      String pipelineElementName = removeQuotes(matcher.group(3));
//      String pipelineElementDescription = removeQuotes(matcher.group(4));
//
//      assetModel.setPeType(peType);
//      assetModel.setAppId(appId);
//      assetModel.setPipelineElementName(pipelineElementName);
//      assetModel.setPipelineElementDescription(pipelineElementDescription);
//
//    }

    if (matches) {
      return assetModel;
    } else {
      Matcher appIdMatcher = onlyAppIdPattern.matcher(this.declareModelContent);
      while (appIdMatcher.find()) {
        matches = true;
        PeType peType = new PipelineElementTypeExtractor(appIdMatcher.group(1)).extractType();
        assetModel.setAppId(removeQuotes(appIdMatcher.group(2)));
        assetModel.setPeType(peType);
      }
      if (!matches) {
        throw new IllegalArgumentException("Could not extract appId");
      } else {
        return assetModel;
      }
    }
  }

  private String removeQuotes(String text) {
    return text
        .replaceAll("\" \\+ \"", "")
        .replaceAll("\"", "");
  }
}
