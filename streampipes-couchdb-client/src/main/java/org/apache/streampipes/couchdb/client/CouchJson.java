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

import org.apache.streampipes.couchdb.client.codec.DocumentCodec;
import org.apache.streampipes.couchdb.client.codec.JsonCopy;
import org.apache.streampipes.couchdb.client.exception.BadRequestException;
import org.apache.streampipes.couchdb.client.exception.CouchDbAuthenticationException;
import org.apache.streampipes.couchdb.client.exception.CouchDbException;
import org.apache.streampipes.couchdb.client.exception.DatabaseNotFoundException;
import org.apache.streampipes.couchdb.client.exception.DocumentConflictException;
import org.apache.streampipes.couchdb.client.exception.DocumentMappingException;
import org.apache.streampipes.couchdb.client.exception.DocumentNotFoundException;
import org.apache.streampipes.couchdb.client.exception.PreconditionFailedException;
import org.apache.streampipes.couchdb.client.transport.CouchRequest;
import org.apache.streampipes.couchdb.client.transport.CouchResponse;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Reads and writes CouchDB's own JSON (envelopes, write responses, errors). Document bodies go through a
 * {@link DocumentCodec}.
 */
final class CouchJson {

  static final ObjectMapper MAPPER = JsonMapper.builder().build();

  private static final String DESIGN_PREFIX = "_design/";
  private static final String ID = "_id";
  private static final String REV = "_rev";

  private CouchJson() {
  }

  record DocumentMeta(String id, String rev, boolean hasNullMetadata) {
  }

  static byte[] write(Object value) {
    try {
      return MAPPER.writeValueAsBytes(value);
    } catch (JsonProcessingException e) {
      throw new DocumentMappingException("Could not serialize request body", e);
    }
  }

  static String keyJson(Object key) {
    try {
      return MAPPER.writeValueAsString(key);
    } catch (JsonProcessingException e) {
      throw new DocumentMappingException("Could not serialize view key " + key, e);
    }
  }

  static JsonNode readTree(CouchResponse response) throws IOException {
    return MAPPER.readTree(response.body());
  }

  static DocumentRef readRef(CouchResponse response) throws IOException {
    JsonNode node = readTree(response);
    return new DocumentRef(node.path("id").asText(null), node.path("rev").asText(null));
  }

  /**
   * Reads {@code _id} and {@code _rev} from the top level of an encoded document without decoding the rest.
   */
  static DocumentMeta scan(byte[] json) {
    try (JsonParser parser = MAPPER.createParser(json)) {
      if (parser.nextToken() != JsonToken.START_OBJECT) {
        throw new IllegalArgumentException("A document must serialize to a JSON object");
      }
      String id = null;
      String rev = null;
      boolean hasNull = false;
      while (parser.nextToken() == JsonToken.FIELD_NAME) {
        String name = parser.currentName();
        JsonToken value = parser.nextToken();
        if (ID.equals(name) || REV.equals(name)) {
          if (value == JsonToken.VALUE_NULL) {
            hasNull = true;
          } else if (ID.equals(name)) {
            id = parser.getValueAsString();
          } else {
            rev = parser.getValueAsString();
          }
        } else {
          parser.skipChildren();
        }
      }
      return new DocumentMeta(id, rev, hasNull);
    } catch (IOException e) {
      throw new DocumentMappingException("Codec produced invalid JSON", e);
    }
  }

  /**
   * Copies a document without the given top-level fields.
   */
  static byte[] withoutFields(byte[] json, Set<String> fields) {
    var out = new ByteArrayOutputStream(json.length);
    try (JsonParser parser = MAPPER.createParser(json);
         JsonGenerator generator = MAPPER.createGenerator(out)) {
      parser.nextToken();
      generator.writeStartObject();
      while (parser.nextToken() == JsonToken.FIELD_NAME) {
        String name = parser.currentName();
        parser.nextToken();
        if (fields.contains(name)) {
          parser.skipChildren();
        } else {
          generator.writeFieldName(name);
          JsonCopy.copyExact(parser, generator);
        }
      }
      generator.writeEndObject();
    } catch (IOException e) {
      throw new DocumentMappingException("Could not rewrite document", e);
    }
    return out.toByteArray();
  }

  static <T> T readDocument(InputStream body, DocumentCodec codec, Type type) throws IOException {
    try (JsonParser parser = MAPPER.createParser(body)) {
      if (parser.nextToken() != JsonToken.START_OBJECT) {
        throw new DocumentMappingException("Expected a JSON object");
      }
      return codec.decode(parser, type);
    }
  }

  /**
   * Reads the documents of a view or {@code _all_docs} response with {@code include_docs=true}. Rows without a
   * document (missing or deleted keys) are skipped.
   */
  static <T> List<T> readRowDocuments(InputStream body,
                                      DocumentCodec codec,
                                      Type type,
                                      boolean excludeDesignDocuments) throws IOException {
    var result = new ArrayList<T>();
    try (JsonParser parser = MAPPER.createParser(body)) {
      forEachArrayElement(parser, "rows", () -> {
        String id = null;
        T doc = null;
        while (parser.nextToken() == JsonToken.FIELD_NAME) {
          String name = parser.currentName();
          JsonToken value = parser.nextToken();
          if ("id".equals(name) && value == JsonToken.VALUE_STRING) {
            id = parser.getText();
          } else if ("doc".equals(name) && value == JsonToken.START_OBJECT
              && !(excludeDesignDocuments && isDesign(id))) {
            // CouchDB writes "id" before "doc", so design documents are skipped without decoding them.
            doc = codec.decode(parser, type);
          } else {
            parser.skipChildren();
          }
        }
        if (doc != null) {
          result.add(doc);
        }
      });
    }
    return result;
  }

  static List<ViewRow> readRows(InputStream body, boolean excludeDesignDocuments) throws IOException {
    var result = new ArrayList<ViewRow>();
    try (JsonParser parser = MAPPER.createParser(body)) {
      forEachArrayElement(parser, "rows", () -> {
        String id = null;
        JsonNode key = null;
        JsonNode value = null;
        boolean error = false;
        while (parser.nextToken() == JsonToken.FIELD_NAME) {
          String name = parser.currentName();
          JsonToken token = parser.nextToken();
          switch (name) {
            case "id" -> id = token == JsonToken.VALUE_STRING ? parser.getText() : null;
            case "key" -> key = MAPPER.readTree(parser);
            case "value" -> value = MAPPER.readTree(parser);
            case "error" -> {
              error = true;
              parser.skipChildren();
            }
            default -> parser.skipChildren();
          }
        }
        if (!error && !(excludeDesignDocuments && isDesign(id))) {
          result.add(new ViewRow(id, key, value));
        }
      });
    }
    return result;
  }

  static <T> FindResult<T> readFindResult(InputStream body, DocumentCodec codec, Type type) throws IOException {
    var docs = new ArrayList<T>();
    String bookmark = null;
    String warning = null;
    try (JsonParser parser = MAPPER.createParser(body)) {
      expect(parser.nextToken(), JsonToken.START_OBJECT);
      while (parser.nextToken() == JsonToken.FIELD_NAME) {
        String name = parser.currentName();
        JsonToken token = parser.nextToken();
        if ("docs".equals(name) && token == JsonToken.START_ARRAY) {
          while (parser.nextToken() == JsonToken.START_OBJECT) {
            docs.add(codec.decode(parser, type));
          }
        } else if ("bookmark".equals(name) && token == JsonToken.VALUE_STRING) {
          bookmark = parser.getText();
        } else if ("warning".equals(name) && token == JsonToken.VALUE_STRING) {
          warning = parser.getText();
        } else {
          parser.skipChildren();
        }
      }
    }
    return new FindResult<>(docs, bookmark, warning);
  }

  static CouchDbException toException(CouchRequest request, CouchResponse response) {
    int status = response.status();
    String error = null;
    String reason = null;
    if (!"HEAD".equals(request.method())) {
      try {
        JsonNode node = MAPPER.readTree(response.bodyBytes());
        if (node != null) {
          error = node.path("error").asText(null);
          reason = node.path("reason").asText(null);
        }
      } catch (IOException | RuntimeException e) {
        // Not a CouchDB error body; status alone has to do.
      }
    }
    String message = request.method() + " " + pathOnly(request.path()) + " failed with " + status
        + (error != null ? " " + error : "")
        + (reason != null ? ": " + reason : "");
    return switch (status) {
      case 400 -> new BadRequestException(message, status, error, reason);
      case 401, 403 -> new CouchDbAuthenticationException(message, status, error, reason);
      case 404 -> reason != null && reason.startsWith("Database does not exist")
          ? new DatabaseNotFoundException(message, status, error, reason)
          : new DocumentNotFoundException(message, status, error, reason);
      case 409 -> new DocumentConflictException(message, status, error, reason);
      case 412 -> new PreconditionFailedException(message, status, error, reason);
      default -> new CouchDbException(message, status, error, reason);
    };
  }

  /**
   * Strips the query string, which may carry keys such as token hashes that must not end up in logs.
   */
  static String pathOnly(String path) {
    int query = path.indexOf('?');
    return query < 0 ? path : path.substring(0, query);
  }

  private static boolean isDesign(String id) {
    return id != null && id.startsWith(DESIGN_PREFIX);
  }

  @FunctionalInterface
  private interface ElementReader {
    void read() throws IOException;
  }

  /**
   * Calls the reader for every object in the top-level array {@code field}, with the parser on its
   * {@code START_OBJECT}; the reader must consume the object.
   */
  private static void forEachArrayElement(JsonParser parser, String field, ElementReader reader) throws IOException {
    expect(parser.nextToken(), JsonToken.START_OBJECT);
    while (parser.nextToken() == JsonToken.FIELD_NAME) {
      String name = parser.currentName();
      JsonToken token = parser.nextToken();
      if (field.equals(name) && token == JsonToken.START_ARRAY) {
        while (parser.nextToken() == JsonToken.START_OBJECT) {
          reader.read();
        }
      } else {
        parser.skipChildren();
      }
    }
  }

  private static void expect(JsonToken actual, JsonToken expected) {
    if (actual != expected) {
      throw new DocumentMappingException(
          "Unexpected response from CouchDB: expected " + expected + " but got " + actual);
    }
  }
}
