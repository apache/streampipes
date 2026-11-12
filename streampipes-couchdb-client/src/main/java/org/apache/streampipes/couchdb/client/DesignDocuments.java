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

import org.apache.streampipes.couchdb.client.design.DesignDocument;
import org.apache.streampipes.couchdb.client.design.SyncResult;
import org.apache.streampipes.couchdb.client.design.ViewDefinition;
import org.apache.streampipes.couchdb.client.exception.DocumentConflictException;
import org.apache.streampipes.couchdb.client.exception.DocumentNotFoundException;
import org.apache.streampipes.couchdb.client.transport.CouchRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Design documents of one database.
 */
public final class DesignDocuments {

  private static final int SYNC_ATTEMPTS = 3;
  private static final Set<String> KNOWN_FIELDS = Set.of("_id", "_rev", "language", "views");

  private final CouchDatabase database;

  DesignDocuments(CouchDatabase database) {
    this.database = database;
  }

  /**
   * Reads a design document.
   *
   * @param name name with or without {@code _design/}
   * @return the design document, empty if it does not exist
   */
  public Optional<DesignDocument> get(String name) {
    try {
      return Optional.of(database.execute(CouchRequest.get(database.documentPath(DesignDocument.toId(name))),
          response -> fromJson(CouchJson.readTree(response))));
    } catch (DocumentNotFoundException e) {
      return Optional.empty();
    }
  }

  /**
   * Makes the stored design document define exactly the given language and views. Other stored fields
   * (filters, validation functions) are kept unless the given document sets them. Safe to call concurrently from
   * several instances; conflicts are retried.
   *
   * @param desired design document with the views it must contain
   * @return what was done
   */
  public SyncResult sync(DesignDocument desired) {
    DocumentConflictException lastConflict = null;
    for (int attempt = 0; attempt < SYNC_ATTEMPTS; attempt++) {
      Optional<DesignDocument> existing = get(desired.id());
      try {
        if (existing.isEmpty()) {
          put(desired.withRev(null));
          return SyncResult.CREATED;
        }
        if (existing.get().sameDefinitionAs(desired) && containsOtherFields(existing.get(), desired)) {
          return SyncResult.UNCHANGED;
        }
        var otherFields = new LinkedHashMap<>(existing.get().otherFields());
        otherFields.putAll(desired.otherFields());
        put(desired.withOtherFields(otherFields).withRev(existing.get().rev()));
        return SyncResult.UPDATED;
      } catch (DocumentConflictException e) {
        lastConflict = e;
      }
    }
    throw lastConflict;
  }

  /**
   * Deletes a design document.
   *
   * @param name name with or without {@code _design/}
   * @return true if it was deleted, false if it did not exist
   */
  public boolean delete(String name) {
    return database.raw().delete(DesignDocument.toId(name));
  }

  private static boolean containsOtherFields(DesignDocument existing, DesignDocument desired) {
    return desired.otherFields().entrySet().stream()
        .allMatch(entry -> entry.getValue().equals(existing.otherFields().get(entry.getKey())));
  }

  private void put(DesignDocument document) {
    database.execute(CouchRequest.put(database.documentPath(document.id()), CouchJson.write(toJson(document))),
        CouchJson::readRef);
  }

  static ObjectNode toJson(DesignDocument document) {
    ObjectNode node = CouchJson.MAPPER.createObjectNode();
    node.put("_id", document.id());
    if (document.rev() != null) {
      node.put("_rev", document.rev());
    }
    node.put("language", document.language() == null ? DesignDocument.DEFAULT_LANGUAGE : document.language());
    ObjectNode views = node.putObject("views");
    document.views().forEach((name, view) -> {
      ObjectNode viewNode = views.putObject(name);
      viewNode.put("map", view.map());
      if (view.reduce() != null) {
        viewNode.put("reduce", view.reduce());
      }
    });
    document.otherFields().forEach(node::set);
    return node;
  }

  static DesignDocument fromJson(JsonNode node) {
    var views = new LinkedHashMap<String, ViewDefinition>();
    node.path("views").properties().forEach(entry -> views.put(entry.getKey(),
        new ViewDefinition(entry.getValue().path("map").asText(null), entry.getValue().path("reduce").asText(null))));
    Map<String, JsonNode> otherFields = new LinkedHashMap<>();
    node.properties().forEach(entry -> {
      if (!KNOWN_FIELDS.contains(entry.getKey())) {
        otherFields.put(entry.getKey(), entry.getValue());
      }
    });
    return new DesignDocument(node.path("_id").asText(),
        node.path("_rev").asText(null),
        node.path("language").asText(null),
        views,
        otherFields);
  }
}
