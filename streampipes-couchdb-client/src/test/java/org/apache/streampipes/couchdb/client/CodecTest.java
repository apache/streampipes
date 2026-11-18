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

package org.apache.streampipes.couchdb.client;

import org.apache.streampipes.couchdb.client.codec.GsonCodec;
import org.apache.streampipes.couchdb.client.codec.JacksonCodec;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CodecTest {

  static class GsonDoc {
    @SerializedName("_id")
    String id;
    @SerializedName("_rev")
    String rev;
    BigDecimal amount;
    List<String> tags;
  }

  record JacksonDoc(@JsonProperty("_id") String id, @JsonProperty("_rev") String rev, int count) {
  }

  private static final String ROWS = "{\"rows\":["
      + "{\"id\":\"a\",\"doc\":{\"_id\":\"a\",\"_rev\":\"1-x\",\"amount\":0.1000000000000000055511151231257827,"
      + "\"tags\":[\"x\",\"y\"],\"count\":1,\"unknown\":{\"deep\":[1,{\"k\":null}]}}},"
      + "{\"id\":\"b\",\"doc\":{\"_id\":\"b\",\"_rev\":\"2-y\",\"amount\":1E+3,\"tags\":[],\"count\":2}}"
      + "]}";

  @Test
  public void gsonDecodesConsecutiveDocumentsWithExactNumbers() throws IOException {
    List<GsonDoc> docs = CouchJson.readRowDocuments(stream(ROWS), GsonCodec.of(new Gson()), GsonDoc.class, false);
    assertEquals(2, docs.size());
    assertEquals("a", docs.get(0).id);
    assertEquals("1-x", docs.get(0).rev);
    assertEquals(new BigDecimal("0.1000000000000000055511151231257827"), docs.get(0).amount);
    assertEquals(List.of("x", "y"), docs.get(0).tags);
    assertEquals("b", docs.get(1).id);
    assertEquals(0, new BigDecimal("1000").compareTo(docs.get(1).amount));
  }

  @Test
  public void jacksonDecodesConsecutiveDocuments() throws IOException {
    List<JacksonDoc> docs = CouchJson.readRowDocuments(stream(ROWS), JacksonCodec.defaults(), JacksonDoc.class, false);
    assertEquals(List.of(new JacksonDoc("a", "1-x", 1), new JacksonDoc("b", "2-y", 2)), docs);
  }

  @Test
  public void codecsEncodeMetadataAsTheModelNamesIt() {
    var gsonDoc = new GsonDoc();
    gsonDoc.id = "a";
    var gsonMeta = CouchJson.scan(GsonCodec.of(new Gson()).encode(gsonDoc));
    assertEquals("a", gsonMeta.id());

    var jacksonMeta = CouchJson.scan(JacksonCodec.defaults().encode(new JacksonDoc("b", "1-z", 0)));
    assertEquals("b", jacksonMeta.id());
    assertEquals("1-z", jacksonMeta.rev());

    var mapMeta = CouchJson.scan(JacksonCodec.defaults().encode(Map.of("_id", "c")));
    assertEquals("c", mapMeta.id());
  }

  private static ByteArrayInputStream stream(String value) {
    return new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
  }
}
