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

package org.apache.streampipes.storage.couchdb.compat;

import org.apache.streampipes.couchdb.client.CouchServer;
import org.apache.streampipes.couchdb.client.TypedCollection;
import org.apache.streampipes.couchdb.client.codec.GsonCodec;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.storage.couchdb.compat.ModelFixtures.Variant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Checks that the new client with {@link GsonCodec} writes and reads every stored model exactly as LightCouch
 * does: same JSON on write, same objects (compared by their JSON) on read through {@code find}, {@code _all_docs}
 * and views. Gate for every phase that moves a storage off LightCouch (docs/couchdb-client-concept.md, 6.4).
 */
public class CodecCompatibilityTest {

  @TestFactory
  public Stream<DynamicTest> newCodecMatchesLightCouch() {
    return StoredModels.ALL.stream().flatMap(model -> Stream.of(Variant.values())
        .map(variant -> DynamicTest.dynamicTest(model.name() + " " + variant, () -> check(model, variant))));
  }

  /**
   * Generic storage documents are written as the caller's JSON and read as maps with
   * {@code JacksonSerializer.getObjectMapper()}; the client's raw collection must read the same maps.
   */
  @Test
  public void rawCollectionReadsGenericDocumentsLikeGenericStorageImpl() throws Exception {
    String stored = "{\"_id\":\"generic-1\",\"_rev\":\"1-a\",\"appDocType\":\"asset-type\",\"name\":\"Ünï <&>\","
        + "\"count\":2,\"ratio\":1.10,\"big\":12345678901234567890,\"tiny\":1.0e-7,\"none\":null,"
        + "\"nested\":{\"list\":[1,\"a\",true,{\"k\":[]}]}}";
    ObjectMapper current = JacksonSerializer.getObjectMapper();
    current.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    Map<String, Object> expected = current.readValue(stored, new TypeReference<>() {
    });

    byte[] storedBytes = stored.getBytes(StandardCharsets.UTF_8);
    try (CouchServer server = CannedCouchDb.serving("generic-1", storedBytes)) {
      var raw = server.database("genericstorage").raw();
      assertEquals(expected, raw.get("generic-1"));
      assertEquals(List.of(expected), raw.view("appDocType", "appDocType").docs());
      assertEquals(current.writeValueAsString(expected), current.writeValueAsString(raw.get("generic-1")));
    }
  }

  private static void check(StoredModel model, Variant variant) {
    Gson legacy = LegacyCouchDbPath.gson(model.gsonBuilder().get());
    GsonCodec codec = model.newCodec();
    String id = "fixture-" + model.fixtureType().getSimpleName().toLowerCase() + "-" + variant.name().toLowerCase();
    Object fixture = ModelFixtures.create(model.fixtureType(), variant, id, codec.gson());

    String legacyJson = LegacyCouchDbPath.encode(legacy, fixture);
    JsonNode legacyTree = JsonTrees.parse(legacyJson);
    assertSame(model, "encoded document", legacyTree, JsonTrees.parse(codec.encode(fixture)));
    if (variant == Variant.RICH) {
      // Adapters and principals are wrapped as {type, properties} by their serializers.
      JsonNode fields = legacyTree.has("properties") ? legacyTree.get("properties") : legacyTree;
      List<String> missing = ModelFixtures.serializedFieldNames(model.fixtureType()).stream()
          .filter(name -> !"_rev".equals(name) && !fields.has(name))
          .toList();
      assertTrue(missing.isEmpty(), () -> model + ": fixture leaves fields empty: " + missing);
    }

    byte[] stored = legacyJson.getBytes(StandardCharsets.UTF_8);
    Class<?> readType = model.readType();
    JsonNode expected = reencode(legacy, LegacyCouchDbPath.find(legacy, stored, readType));
    assertSame(model, "LightCouch view read", expected,
        reencode(legacy, LegacyCouchDbPath.view(legacy, CannedCouchDb.rowsResponse(id, stored), readType).get(0)));

    try (CouchServer server = CannedCouchDb.serving(id, stored)) {
      TypedCollection<Object> collection = server.database(model.database()).typed(rawClass(readType), codec);
      assertSame(model, "get", expected, reencode(legacy, collection.get(id)));
      List<Object> all = model.view() == null
          ? collection.findAll()
          : collection.view(model.designDocument(), model.viewName()).docs();
      assertEquals(1, all.size(), model + ": findAll");
      assertSame(model, "findAll", expected, reencode(legacy, all.get(0)));
    }
  }

  @SuppressWarnings("unchecked")
  private static Class<Object> rawClass(Class<?> type) {
    return (Class<Object>) type;
  }

  private static JsonNode reencode(Gson legacy, Object document) {
    return JsonTrees.parse(LegacyCouchDbPath.encode(legacy, document));
  }

  private static void assertSame(StoredModel model, String step, JsonNode expected, JsonNode actual) {
    List<String> differences = JsonTrees.differences(expected, actual);
    if (!differences.isEmpty()) {
      fail(model + ", " + step + ": LightCouch and new client differ\n  " + String.join("\n  ", differences));
    }
  }
}
