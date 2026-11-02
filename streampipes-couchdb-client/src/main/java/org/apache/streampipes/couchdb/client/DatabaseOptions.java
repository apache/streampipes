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

/**
 * Options for a {@link CouchDatabase} handle.
 */
public final class DatabaseOptions {

  private static final DatabaseOptions CREATE_IF_MISSING = new DatabaseOptions(true);
  private static final DatabaseOptions EXISTING = new DatabaseOptions(false);

  private final boolean createIfMissing;

  private DatabaseOptions(boolean createIfMissing) {
    this.createIfMissing = createIfMissing;
  }

  /**
   * Creates the database before its first use, and again if it disappears. The check runs once per server
   * instance, not per request.
   *
   * @return the options
   */
  public static DatabaseOptions createIfMissing() {
    return CREATE_IF_MISSING;
  }

  public static DatabaseOptions existing() {
    return EXISTING;
  }

  public boolean isCreateIfMissing() {
    return createIfMissing;
  }
}
