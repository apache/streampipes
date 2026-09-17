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

package org.apache.streampipes.storage.api.system;

import java.io.IOException;
import java.util.List;

/** Revision-preserving access to resources containing concrete event groundings. */
public interface IGroundingMigrationStorage {
  List<String> collections() throws IOException;

  List<String> readPage(String collection, String afterId, int limit) throws IOException;

  String read(String collection, String id) throws IOException;

  /** Returns false on a revision conflict; other failures propagate. */
  boolean update(String collection, String id, String document) throws IOException;
}
