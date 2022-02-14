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
package org.apache.streampipes.manager.assets;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImagePathReplacer {

  private static final String IMAGE_REGEX = "(<img.*src=\")(.*)(\")";
  private static final Pattern pattern = Pattern.compile(IMAGE_REGEX);

  private static final String API_PREFIX = "/streampipes-backend/api/v2/pe/";
  private static final String API_APPENDIX = "/assets/";

  private String originalContent;
  private String appId;

  public ImagePathReplacer(String originalContent, String appId) {
    this.originalContent = originalContent;
    this.appId = appId;
  }

  public String replaceContent() {
    String newContent;
    Matcher matcher = pattern.matcher(originalContent);
    newContent = matcher.replaceAll("$1" + getUrl() + "$2$3");

    return newContent;
  }

  private String getUrl() {
    return API_PREFIX + appId + API_APPENDIX;
  }
}
