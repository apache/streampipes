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

import java.util.Map;

/**
 * Writes id and revision back into a document after a successful write. Opt-in per collection via
 * {@link TypedCollection#withMetadataWriter(MetadataWriter)}; without it, documents are left unchanged.
 *
 * @param <T> document type
 */
@FunctionalInterface
public interface MetadataWriter<T> {

  void write(T document, DocumentRef ref);

  /**
   * Writer for mutable map documents, setting {@code _id} and {@code _rev}.
   *
   * @return the writer
   */
  static MetadataWriter<Map<String, Object>> forMaps() {
    return (document, ref) -> {
      document.put("_id", ref.id());
      document.put("_rev", ref.rev());
    };
  }
}
