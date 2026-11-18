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

import org.apache.streampipes.couchdb.client.codec.GsonCodec;

import com.google.gson.GsonBuilder;

import java.util.function.Supplier;

/**
 * A document type as a storage persists it today.
 *
 * @param database    database name
 * @param fixtureType class instantiated for fixtures
 * @param readType    class the storage passes to LightCouch when reading
 * @param gsonBuilder the builder the storage's {@code Utils} client uses
 * @param view        LightCouch view name ({@code design/view}) the storage's {@code findAll} reads, or
 *                    {@code null} for {@code _all_docs}
 */
record StoredModel(String database,
                   Class<?> fixtureType,
                   Class<?> readType,
                   Supplier<GsonBuilder> gsonBuilder,
                   String view) {

  String name() {
    return database + "/" + fixtureType.getSimpleName();
  }

  /**
   * The codec phase 3 gives the storage: the same builder, without LightCouch's additions.
   */
  GsonCodec newCodec() {
    return GsonCodec.of(gsonBuilder.get().create());
  }

  String designDocument() {
    return view.substring(0, view.indexOf('/'));
  }

  String viewName() {
    return view.substring(view.indexOf('/') + 1);
  }

  @Override
  public String toString() {
    return name();
  }
}
