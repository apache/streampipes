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

package org.streampipes.pe.shared;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderExtractor {

  private static final Pattern pattern = Pattern.compile("#[^#]*#");

  public static String replacePlaceholders(String content, String json) {
    List<String> placeholders = getPlaceholders(content);
    JsonParser parser = new JsonParser();
    JsonObject jsonObject = parser.parse(json).getAsJsonObject();

    for(String placeholder : placeholders) {
      String replacedValue = getPropertyValue(jsonObject, placeholder);
      content = content.replaceAll(placeholder, replacedValue);
    }

    return content;
  }

  public static String replacePlaceholders(String content, Map<String, Object> event) {
    List<String> placeholders = getPlaceholders(content);

    for(String placeholder : placeholders) {
      String replacedValue = getPropertyValue(event, placeholder);
      content = content.replaceAll(placeholder, replacedValue);
    }

    return content;
  }

  private static String getPropertyValue(Map<String, Object> event, String placeholder) {
    String key = placeholder.replaceAll("#", "");
    return String.valueOf(event.get(key));
  }

  private static String getPropertyValue(JsonObject jsonObject, String placeholder) {
    String jsonKey = placeholder.replaceAll("#", "");
    return String.valueOf(jsonObject.get(jsonKey).getAsString());
  }

  private static List<String> getPlaceholders(String content) {
    List<String> results = new ArrayList<>();
    Matcher matcher = pattern.matcher(content);
    while (matcher.find()) {
      results.add(matcher.group());
    }
    return results;
  }
}
