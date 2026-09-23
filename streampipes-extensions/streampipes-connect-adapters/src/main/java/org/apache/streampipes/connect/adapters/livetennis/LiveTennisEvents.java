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

package org.apache.streampipes.connect.adapters.livetennis;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class LiveTennisEvents {

  private LiveTennisEvents() {
  }

  static List<Map<String, Object>> from(JsonArray data, long fetchedAt) throws IOException {
    try {
      List<Map<String, Object>> events = new ArrayList<>();
      for (JsonElement item : data) {
        JsonObject match = item.getAsJsonObject();
        JsonObject players = match.getAsJsonObject("players");
        JsonObject score = object(match.get("score"));
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("timestamp", fetchedAt);
        event.put("matchId", match.get("id").getAsLong());
        event.put("tournament", text(match.get("tournament")));
        event.put("tour", text(match.get("tour")));
        event.put("player1", text(object(players.get("p1")).get("name")));
        event.put("player2", text(object(players.get("p2")).get("name")));
        event.put("scoreAvailable", match.has("score") && !match.get("score").isJsonNull());
        event.put("sets", pair(score.get("sets")));
        event.put("games", games(score.get("games")));
        event.put("points", pair(score.get("points")));
        event.put("server", text(score.get("server")));
        event.put("isTiebreak", score.has("is_tiebreak") && !score.get("is_tiebreak").isJsonNull()
            && score.get("is_tiebreak").getAsBoolean());
        events.add(event);
      }
      return events;
    } catch (RuntimeException e) {
      throw new IOException("Live Tennis API returned an invalid match");
    }
  }

  private static JsonObject object(JsonElement value) {
    return value == null || value.isJsonNull() ? new JsonObject() : value.getAsJsonObject();
  }

  private static String text(JsonElement value) {
    return value == null || value.isJsonNull() ? "" : value.getAsString();
  }

  private static String pair(JsonElement value) {
    if (value == null || value.isJsonNull() || value.getAsJsonArray().isEmpty()) {
      return "";
    }
    JsonArray pair = value.getAsJsonArray();
    return point(pair, 0) + "-" + point(pair, 1);
  }

  private static String point(JsonArray values, int index) {
    String value = index < values.size() ? text(values.get(index)) : "";
    return value.isEmpty() ? "?" : value;
  }

  private static String games(JsonElement value) {
    if (value == null || value.isJsonNull() || value.getAsJsonArray().isEmpty()) {
      return "";
    }
    JsonArray players = value.getAsJsonArray();
    JsonArray first = players.get(0).getAsJsonArray();
    JsonArray second = players.size() > 1 ? players.get(1).getAsJsonArray() : new JsonArray();
    List<String> sets = new ArrayList<>();
    // The API groups game scores by player, not by set.
    for (int i = 0; i < Math.max(first.size(), second.size()); i++) {
      sets.add(point(first, i) + "-" + point(second, i));
    }
    return String.join(", ", sets);
  }
}
