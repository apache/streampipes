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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Duration;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

class LiveTennisClient {

  // One attempt per 900 seconds is at most 96 requests per day, below the free limit of 100.
  static final int INTERVAL_SECONDS = 900;
  private static final int MAX_RESPONSE_BYTES = 2 * 1024 * 1024;
  private static final Gson GSON = new Gson();
  private static final HttpClient HTTP = HttpClient.newBuilder()
      .connectTimeout(Duration.ofSeconds(10))
      .followRedirects(HttpClient.Redirect.NEVER)
      .build();

  private final Path cacheDirectory;
  private final URI endpoint;
  private final Clock clock;
  private final Duration requestTimeout;

  LiveTennisClient() {
    this(Path.of(System.getenv().getOrDefault("SP_LIVE_TENNIS_CACHE_DIRECTORY",
            Path.of(System.getProperty("java.io.tmpdir"), "streampipes-live-tennis").toString())),
        URI.create("https://api.livetennisapi.com/api/public/v1/matches?status=live&limit=200"),
        Clock.systemUTC());
  }

  LiveTennisClient(Path cacheDirectory, URI endpoint, Clock clock) {
    this(cacheDirectory, endpoint, clock, Duration.ofSeconds(15));
  }

  LiveTennisClient(Path cacheDirectory, URI endpoint, Clock clock, Duration requestTimeout) {
    this.cacheDirectory = cacheDirectory;
    this.endpoint = endpoint;
    this.clock = clock;
    this.requestTimeout = requestTimeout;
  }

  List<Map<String, Object>> fetch(String apiKey) throws IOException, InterruptedException {
    if (apiKey == null || apiKey.isBlank() || apiKey.chars().anyMatch(c -> c <= 32 || c >= 127)) {
      throw new IOException("A valid Live Tennis API key is required");
    }
    String fingerprint = fingerprint(apiKey);
    // The monitor handles threads; the file lock also handles separate extension processes.
    synchronized (LiveTennisClient.class) {
      Files.createDirectories(cacheDirectory);
      Path statePath = cacheDirectory.resolve(fingerprint + ".json");
      try (var channel = FileChannel.open(cacheDirectory.resolve(fingerprint + ".lock"),
          StandardOpenOption.CREATE, StandardOpenOption.WRITE);
           var lock = channel.lock()) {
        long now = clock.millis();
        CacheEntry cached = read(statePath);
        if (cached != null && now < cached.nextAttemptMillis()) {
          if (cached.data() == null) {
            throw new IOException("Live tennis request cooldown is active after a failed attempt");
          }
          return LiveTennisEvents.from(cached.data(), cached.fetchedAt());
        }

        long nextAttempt = now + Duration.ofSeconds(INTERVAL_SECONDS).toMillis();
        // Persist before the request so errors and restarts cannot bypass the request floor.
        write(statePath, new CacheEntry(nextAttempt, 0, null));
        JsonArray successfulData = null;
        long fetchedAt = 0;
        try {
          JsonArray data = request(apiKey);
          fetchedAt = clock.millis();
          List<Map<String, Object>> events = LiveTennisEvents.from(data, fetchedAt);
          successfulData = data;
          return events;
        } finally {
          // Waiting for a response must not shorten the next request's cooldown.
          nextAttempt = Math.max(now, clock.millis()) + Duration.ofSeconds(INTERVAL_SECONDS).toMillis();
          write(statePath, new CacheEntry(nextAttempt, fetchedAt, successfulData));
        }
      }
    }
  }

  private JsonArray request(String apiKey) throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder(endpoint)
        .header("X-API-Key", apiKey)
        .header("Accept", "application/json")
        .timeout(requestTimeout)
        .GET()
        .build();
    var pending = HTTP.sendAsync(request,
        HttpResponse.BodyHandlers.limiting(HttpResponse.BodyHandlers.ofByteArray(), MAX_RESPONSE_BYTES));
    HttpResponse<byte[]> response;
    try {
      response = pending.get(requestTimeout.toMillis(), TimeUnit.MILLISECONDS);
    } catch (TimeoutException e) {
      pending.cancel(true);
      throw new IOException("Live Tennis API request timed out");
    } catch (InterruptedException e) {
      pending.cancel(true);
      throw e;
    } catch (ExecutionException e) {
      throw new IOException("Live Tennis API request failed");
    }
    if (response.statusCode() != 200) {
      throw new IOException("Live Tennis API returned HTTP " + response.statusCode());
    }
    try {
      var document = JsonParser.parseString(new String(response.body(), StandardCharsets.UTF_8)).getAsJsonObject();
      var data = document.getAsJsonArray("data");
      if (data == null || data.size() > 200) {
        throw new IOException("Live Tennis API returned an invalid match list");
      }
      return data;
    } catch (RuntimeException e) {
      throw new IOException("Live Tennis API returned an invalid match list");
    }
  }

  private CacheEntry read(Path path) throws IOException {
    if (!Files.exists(path)) {
      return null;
    }
    if (Files.size(path) > 2L * MAX_RESPONSE_BYTES) {
      throw new IOException("Live tennis cache exceeds the size limit");
    }
    try {
      CacheEntry entry = GSON.fromJson(Files.readString(path), CacheEntry.class);
      if (entry == null || entry.nextAttemptMillis() <= 0 || entry.fetchedAt() < 0) {
        throw new IOException("Live tennis cache is invalid");
      }
      return entry;
    } catch (RuntimeException e) {
      throw new IOException("Live tennis cache is invalid");
    }
  }

  private void write(Path path, CacheEntry entry) throws IOException {
    byte[] bytes = GSON.toJson(entry).getBytes(StandardCharsets.UTF_8);
    try (var channel = FileChannel.open(path, StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
      ByteBuffer buffer = ByteBuffer.wrap(bytes);
      while (buffer.hasRemaining()) {
        channel.write(buffer);
      }
      channel.force(true);
    }
  }

  private String fingerprint(String apiKey) {
    try {
      return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
          .digest(apiKey.getBytes(StandardCharsets.UTF_8)));
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 is unavailable", e);
    }
  }

  private record CacheEntry(long nextAttemptMillis, long fetchedAt, JsonArray data) {
  }
}
