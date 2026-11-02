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
import org.apache.streampipes.couchdb.client.codec.JacksonCodec;
import org.apache.streampipes.couchdb.client.exception.CouchDbException;
import org.apache.streampipes.couchdb.client.exception.DatabaseNotFoundException;
import org.apache.streampipes.couchdb.client.exception.PreconditionFailedException;
import org.apache.streampipes.couchdb.client.transport.CouchRequest;
import org.apache.streampipes.couchdb.client.transport.CouchResponse;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Handle for one database. Cheap to create and thread-safe.
 */
public final class CouchDatabase {

  private static final Type RAW_TYPE = CouchJson.MAPPER.getTypeFactory()
      .constructMapType(LinkedHashMap.class, String.class, Object.class);

  private final CouchServer server;
  private final String name;
  private final DatabaseOptions options;
  private final String path;

  CouchDatabase(CouchServer server, String name, DatabaseOptions options) {
    this.server = server;
    this.name = name;
    this.options = options;
    this.path = CouchPaths.database(name);
  }

  public String name() {
    return name;
  }

  public CouchServer server() {
    return server;
  }

  public boolean exists() {
    try {
      return server.execute(CouchRequest.head(path), response -> true);
    } catch (CouchDbException e) {
      if (e.getStatus() == 404) {
        return false;
      }
      throw e;
    }
  }

  /**
   * Creates the database.
   *
   * @return true if it was created, false if it existed
   */
  public boolean create() {
    try {
      server.execute(new CouchRequest("PUT", path, CouchRequest.JSON, null, null), response -> null);
      return true;
    } catch (PreconditionFailedException e) {
      return false;
    }
  }

  public void delete() {
    server.forgetDatabase(name);
    server.execute(CouchRequest.delete(path), response -> null);
  }

  /**
   * Collection view of this database for one document type.
   *
   * @param type  document class
   * @param codec codec that matches how documents of this type are stored
   * @param <T>   document type
   * @return the collection
   */
  public <T> TypedCollection<T> typed(Class<T> type, DocumentCodec codec) {
    return new TypedCollection<>(this, type, codec, null);
  }

  /**
   * Collection of untyped documents as {@code Map<String, Object>}, read and written with
   * {@link JacksonCodec#defaults()}.
   *
   * @return the collection
   */
  public TypedCollection<Map<String, Object>> raw() {
    return new TypedCollection<>(this, RAW_TYPE, JacksonCodec.defaults(), null);
  }

  public DesignDocuments design() {
    return new DesignDocuments(this);
  }

  public Attachments attachments() {
    return new Attachments(this);
  }

  public MangoIndexes indexes() {
    return new MangoIndexes(this);
  }

  String path(String suffix) {
    return path + suffix;
  }

  String documentPath(String id) {
    return CouchPaths.document(path, id);
  }

  <T> T execute(CouchRequest request, CouchServer.ResponseHandler<T> handler) {
    if (!options.isCreateIfMissing()) {
      return server.execute(request, handler);
    }
    server.ensureDatabase(name);
    try {
      return server.execute(request, handler);
    } catch (DatabaseNotFoundException e) {
      // Deleted behind our back: create it again and retry once.
      server.forgetDatabase(name);
      server.ensureDatabase(name);
      return server.execute(request, handler);
    }
  }

  CouchResponse open(CouchRequest request) {
    if (options.isCreateIfMissing()) {
      server.ensureDatabase(name);
    }
    return server.open(request);
  }
}
