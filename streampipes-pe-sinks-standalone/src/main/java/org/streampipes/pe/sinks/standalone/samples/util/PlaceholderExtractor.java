package org.streampipes.pe.sinks.standalone.samples.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by riemer on 08.04.2017.
 */
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
