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

package org.apache.streampipes.storage.couchdb.utils;
import org.apache.streampipes.commons.environment.Environment;

import com.google.common.net.UrlEscapers;

/**
 * Utility class for working with CouchDB URLs.
 * <p>
 * Provides helper methods to escape URL path segments and to construct
 * CouchDB database routes from the configured {@link Environment}.
 */

public final class CouchDbUrlUtils {

  private CouchDbUrlUtils() {}

  public static String escapePathSegment(String segment) {
    return UrlEscapers.urlPathSegmentEscaper().escape(segment);
  }

  public static String buildDatabaseRoute(Environment env, String dbName) {
    return env.getCouchDbProtocol().getValueOrDefault()
        + "://" + env.getCouchDbHost().getValueOrDefault()
        + ":" + env.getCouchDbPort().getValueOrDefault()
        + "/" + dbName;
  }
}
