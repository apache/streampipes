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

import com.fasterxml.jackson.databind.JsonNode;

/**
 * A row of a view or {@code _all_docs} result.
 *
 * @param id    id of the emitting document, {@code null} for reduced rows
 * @param key   emitted key
 * @param value emitted value
 */
public record ViewRow(String id, JsonNode key, JsonNode value) {
}
