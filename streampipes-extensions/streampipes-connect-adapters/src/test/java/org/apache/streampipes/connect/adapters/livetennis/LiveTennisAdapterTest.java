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

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.connect.GeneralAdaptersExtensionModuleExport;
import org.apache.streampipes.extensions.api.connect.IEventCollector;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.model.staticproperty.SecretStaticProperty;
import org.apache.streampipes.sdk.extractor.AdapterParameterExtractor;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

class LiveTennisAdapterTest {

  private static final String API_KEY = "fixture-key";
  private static final String MATCH = """
      {"id":42,"tournament":"Example tournament","tour":"atp",
       "players":{"p1":{"name":"Player one"},"p2":{"name":"Player two"}},
       "score":{"sets":[1,0],"games":[[6,3],[4,4]],"points":["30","15"],
                "server":1,"is_tiebreak":false}}
      """;

  @TempDir
  Path directory;

  private HttpServer server;
  private URI endpoint;
  private MutableClock clock;
  private LiveTennisClient client;
  private AtomicInteger requests;
  private volatile int status;
  private volatile String response;
  private volatile String receivedKey;
  private volatile String receivedQuery;
  private volatile long bodyDelayMillis;
  private volatile long responseElapsedMillis;

  @BeforeEach
  void setUp() throws IOException {
    requests = new AtomicInteger();
    status = 200;
    response = "{\"data\":[" + MATCH + "]}";
    clock = new MutableClock();
    server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    server.createContext("/matches", exchange -> {
      requests.incrementAndGet();
      receivedKey = exchange.getRequestHeaders().getFirst("X-API-Key");
      receivedQuery = exchange.getRequestURI().getRawQuery();
      if (status == 302) {
        exchange.getResponseHeaders().add("Location", "/redirected");
      }
      byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
      exchange.sendResponseHeaders(status, bytes.length);
      clock.advance(responseElapsedMillis);
      try (var body = exchange.getResponseBody()) {
        try {
          Thread.sleep(bodyDelayMillis);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          return;
        }
        body.write(bytes);
      } catch (IOException e) {
        // The size limit cancels oversized responses while the fixture server is writing.
      }
    });
    server.createContext("/redirected", exchange -> {
      requests.incrementAndGet();
      exchange.sendResponseHeaders(500, -1);
      exchange.close();
    });
    server.start();
    endpoint = URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/matches?status=live&limit=200");
    client = new LiveTennisClient(directory, endpoint, clock);
  }

  @AfterEach
  void tearDown() {
    server.stop(0);
  }

  @Test
  void readsPlayerMajorScoresAndUsesHeaderAuthentication() throws Exception {
    Map<String, Object> event = client.fetch(API_KEY).getFirst();
    assertEquals(42L, event.get("matchId"));
    assertEquals("Player one", event.get("player1"));
    assertEquals("Player two", event.get("player2"));
    assertEquals("Example tournament", event.get("tournament"));
    assertEquals("atp", event.get("tour"));
    assertEquals("1-0", event.get("sets"));
    assertEquals("6-4, 3-4", event.get("games"));
    assertEquals("30-15", event.get("points"));
    assertEquals("1", event.get("server"));
    assertEquals(true, event.get("scoreAvailable"));
    assertEquals(false, event.get("isTiebreak"));
    assertEquals(clock.millis(), event.get("timestamp"));
    assertEquals(API_KEY, receivedKey);
    assertEquals("status=live&limit=200", receivedQuery);
  }

  @Test
  void sharesSnapshotsAcrossClientsAndPreservesFetchTime() throws Exception {
    var original = client.fetch(API_KEY);
    clock.advance(899_999);
    var restarted = new LiveTennisClient(directory, endpoint, clock);
    assertEquals(original, restarted.fetch(API_KEY));
    assertEquals(1, requests.get());
    clock.advance(1);
    var refreshed = restarted.fetch(API_KEY);
    assertEquals(clock.millis(), refreshed.getFirst().get("timestamp"));
    assertEquals(2, requests.get());
  }

  @Test
  void responseTimeDoesNotShortenTheNextCooldown() throws Exception {
    responseElapsedMillis = 2000;
    client.fetch(API_KEY);
    clock.advance(898_000);
    client.fetch(API_KEY);
    assertEquals(1, requests.get());
    clock.advance(2000);
    client.fetch(API_KEY);
    assertEquals(2, requests.get());
  }

  @Test
  void doesNotShareMutableEventMapsWithConsumers() throws Exception {
    client.fetch(API_KEY).getFirst().put("player1", "Changed by a consumer");
    assertEquals("Player one", client.fetch(API_KEY).getFirst().get("player1"));
    assertEquals(1, requests.get());
  }

  @Test
  void concurrentClientsMakeOneRequest() throws Exception {
    try (var executor = Executors.newFixedThreadPool(2)) {
      var first = executor.submit(() -> client.fetch(API_KEY));
      var second = executor.submit(() -> new LiveTennisClient(directory, endpoint, clock).fetch(API_KEY));
      assertEquals(first.get(5, TimeUnit.SECONDS), second.get(5, TimeUnit.SECONDS));
      assertEquals(1, requests.get());
    }
  }

  @Test
  void separateProcessesShareTheSameRequestFloor() throws Exception {
    var first = readerProcess().start();
    var second = readerProcess().start();
    try {
      assertTrue(first.waitFor(15, TimeUnit.SECONDS));
      assertTrue(second.waitFor(15, TimeUnit.SECONDS));
      assertEquals(0, first.exitValue(), new String(first.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
      assertEquals(0, second.exitValue(), new String(second.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
      assertEquals(1, requests.get());
    } finally {
      first.destroyForcibly();
      second.destroyForcibly();
    }
  }

  private ProcessBuilder readerProcess() {
    return new ProcessBuilder(Path.of(System.getProperty("java.home"), "bin", "java").toString(),
        "-cp", System.getProperty("java.class.path"), CacheReader.class.getName(),
        directory.toString(), endpoint.toString(), Long.toString(clock.millis()))
        .redirectErrorStream(true);
  }

  @Test
  void keepsCredentialsOutOfTheCache() throws Exception {
    client.fetch(API_KEY);
    try (var paths = Files.list(directory)) {
      for (var path : paths.toList()) {
        assertFalse(path.getFileName().toString().contains(API_KEY));
        assertFalse(Files.readString(path).contains(API_KEY));
      }
    }
  }

  @Test
  void givesDifferentCredentialsIndependentBudgets() throws Exception {
    client.fetch(API_KEY);
    client.fetch("second-fixture-key");
    assertEquals(2, requests.get());
  }

  @Test
  void retainsTheCooldownAfterHttpErrorsAndRestarts() throws Exception {
    status = 429;
    response = API_KEY;
    var error = assertThrows(IOException.class, () -> client.fetch(API_KEY));
    assertEquals("Live Tennis API returned HTTP 429", error.getMessage());
    status = 200;
    response = "{\"data\":[]}";
    var restarted = new LiveTennisClient(directory, endpoint, clock);
    assertThrows(IOException.class, () -> restarted.fetch(API_KEY));
    assertEquals(1, requests.get());
    clock.advance(900_000);
    assertTrue(restarted.fetch(API_KEY).isEmpty());
    assertEquals(2, requests.get());
  }

  @Test
  void doesNotPresentAnOldSuccessfulSnapshotAfterARefreshFailure() throws Exception {
    client.fetch(API_KEY);
    clock.advance(900_000);
    status = 503;
    assertThrows(IOException.class, () -> client.fetch(API_KEY));
    assertThrows(IOException.class, () -> client.fetch(API_KEY));
    assertEquals(2, requests.get());
  }

  @Test
  void doesNotFollowRedirects() {
    status = 302;
    assertThrows(IOException.class, () -> client.fetch(API_KEY));
    assertEquals(1, requests.get());
  }

  @Test
  void cachesEmptySlates() throws Exception {
    response = "{\"data\":[]}";
    assertTrue(client.fetch(API_KEY).isEmpty());
    assertTrue(client.fetch(API_KEY).isEmpty());
    assertEquals(1, requests.get());
  }

  @Test
  void rejectsMalformedResponsesWithoutRetrying() {
    for (String malformed : List.of("not json", "{}", "{\"data\":null}", "{\"data\":{}}",
        "{\"data\":[{}]}")) {
      response = malformed;
      String key = "fixture-" + requests.get();
      assertThrows(IOException.class, () -> client.fetch(key));
      int count = requests.get();
      assertThrows(IOException.class, () -> client.fetch(key));
      assertEquals(count, requests.get());
    }
  }

  @Test
  void rejectsOversizedResponsesAndKeepsTheCooldown() {
    response = " ".repeat(2 * 1024 * 1024 + 1);
    assertThrows(IOException.class, () -> client.fetch(API_KEY));
    assertThrows(IOException.class, () -> client.fetch(API_KEY));
    assertEquals(1, requests.get());
  }

  @Test
  void boundsTheBodyReadTimeAndRetainsTheCooldown() {
    bodyDelayMillis = 1000;
    var impatient = new LiveTennisClient(directory, endpoint, clock, Duration.ofMillis(250));
    assertThrows(IOException.class, () -> impatient.fetch(API_KEY));
    assertThrows(IOException.class, () -> impatient.fetch(API_KEY));
    assertEquals(1, requests.get());
  }

  @Test
  void doesNotFetchWithoutWritableQuotaState() throws IOException {
    Path file = Files.writeString(directory.resolve("not-a-directory"), "occupied");
    var unwritable = new LiveTennisClient(file, endpoint, clock);
    assertThrows(IOException.class, () -> unwritable.fetch(API_KEY));
    assertEquals(0, requests.get());
  }

  @Test
  void stopsRequestsWhenQuotaStateIsCorrupt() throws Exception {
    client.fetch(API_KEY);
    try (var paths = Files.list(directory)) {
      Path state = paths.filter(path -> path.toString().endsWith(".json")).findFirst().orElseThrow();
      Files.writeString(state, "interrupted write");
    }
    clock.advance(900_000);
    assertThrows(IOException.class, () -> client.fetch(API_KEY));
    assertEquals(1, requests.get());
  }

  @Test
  void aClockRollbackDoesNotPermitAnotherRequest() throws Exception {
    client.fetch(API_KEY);
    clock.advance(-900_000);
    client.fetch(API_KEY);
    assertEquals(1, requests.get());
  }

  @Test
  void rejectsMissingOrInvalidCredentialsBeforeMakingRequests() {
    assertThrows(IOException.class, () -> client.fetch(null));
    assertThrows(IOException.class, () -> client.fetch(""));
    assertThrows(IOException.class, () -> client.fetch("\nprivate"));
    assertEquals(0, requests.get());
  }

  @Test
  void handlesAbsentScoresWithoutInventingZeroes() throws Exception {
    response = """
        {"data":[{"id":43,"tour":null,"tournament":"Example",
         "players":{"p1":{"name":"One"},"p2":{"name":"Two"}},"score":null}]}
        """;
    Map<String, Object> event = client.fetch(API_KEY).getFirst();
    assertEquals(false, event.get("scoreAvailable"));
    for (String field : List.of("tour", "sets", "games", "points", "server")) {
      assertEquals("", event.get(field), field);
    }
  }

  @Test
  void handlesWithheldGamesAndNullablePoints() throws Exception {
    response = response.replace("[[6,3],[4,4]]", "null").replace("[\"30\",\"15\"]", "[null,null]");
    var event = client.fetch(API_KEY).getFirst();
    assertEquals("", event.get("games"));
    assertEquals("?-?", event.get("points"));
  }

  @Test
  void preservesMatchTiebreakPointsAndUnequalGameArrays() throws Exception {
    response = response.replace("[[6,3],[4,4]]", "[[6,4,10],[4,6,5]]")
        .replace("\"is_tiebreak\":false", "\"is_tiebreak\":true");
    var event = client.fetch(API_KEY).getFirst();
    assertEquals("6-4, 4-6, 10-5", event.get("games"));
    assertEquals(true, event.get("isTiebreak"));
    clock.advance(900_000);
    response = response.replace("[4,6,5]", "[4,6]");
    assertEquals("6-4, 4-6, 10-?", client.fetch(API_KEY).getFirst().get("games"));
  }

  @Test
  void previewsAndRunningAdaptersShareTheSameCache() throws Exception {
    var extractor = extractor();
    var adapter = new LiveTennisAdapter(client);
    var sample = adapter.onSampleDataRequested(extractor, null);
    assertEquals(1, sample.getSamples().size());
    var collector = mock(IEventCollector.class);
    try {
      adapter.onAdapterStarted(extractor, collector, null);
      verify(collector, timeout(5000)).collect(sample.getSamples().getFirst());
      adapter.pullData();
      adapter.onSampleDataRequested(extractor, null);
      assertEquals(1, requests.get());
      assertEquals(900, adapter.getPollingInterval().value());
      assertEquals(TimeUnit.SECONDS, adapter.getPollingInterval().timeUnit());
    } finally {
      adapter.onAdapterStopped(extractor, null);
    }
  }

  @Test
  void emptyPreviewsExplainWhyNoSchemaWasReturned() {
    response = "{\"data\":[]}";
    var adapter = new LiveTennisAdapter(client);
    var error = assertThrows(AdapterException.class, () -> adapter.onSampleDataRequested(extractor(), null));
    assertTrue(error.getMessage().contains("No live tennis matches"));
    assertThrows(AdapterException.class, () -> adapter.onSampleDataRequested(extractor(), null));
    assertEquals(1, requests.get());
  }

  @Test
  void registersTheAdapterInTheGeneralExtensionModule() {
    assertEquals(1, new GeneralAdaptersExtensionModuleExport().adapters().stream()
        .filter(adapter -> adapter instanceof LiveTennisAdapter).count());
    new LiveTennisAdapter().declareConfig();
  }

  private IAdapterParameterExtractor extractor() {
    var description = new LiveTennisAdapter().declareConfig().getAdapterDescription();
    var property = (SecretStaticProperty) description.getConfig().getFirst();
    property.setValue(API_KEY);
    description.setElementId("urn:test:tennis");
    return AdapterParameterExtractor.from(description, List.of());
  }

  private static class MutableClock extends Clock {
    private final AtomicLong millis = new AtomicLong(1_800_000_000_000L);

    void advance(long amount) {
      millis.addAndGet(amount);
    }

    @Override
    public ZoneId getZone() {
      return ZoneOffset.UTC;
    }

    @Override
    public Clock withZone(ZoneId zone) {
      return this;
    }

    @Override
    public Instant instant() {
      return Instant.ofEpochMilli(millis.get());
    }
  }

  public static class CacheReader {
    public static void main(String[] args) throws Exception {
      var clock = Clock.fixed(Instant.ofEpochMilli(Long.parseLong(args[2])), ZoneOffset.UTC);
      var client = new LiveTennisClient(Path.of(args[0]), URI.create(args[1]), clock);
      if (client.fetch(API_KEY).size() != 1) {
        throw new IllegalStateException("Expected one match");
      }
    }
  }
}
