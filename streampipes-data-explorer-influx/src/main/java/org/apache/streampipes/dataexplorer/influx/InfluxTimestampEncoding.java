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

package org.apache.streampipes.dataexplorer.influx;

import org.apache.streampipes.dataexplorer.api.query.QueryTimestampFormat;

/** Influx wire encoding, separate from the storage-independent timestamp contract. */
final class InfluxTimestampEncoding {
  private InfluxTimestampEncoding() {
  }

  static QueryTimestampFormat supported(QueryTimestampFormat requested) {
    return requested == QueryTimestampFormat.EPOCH_DAYS ? QueryTimestampFormat.EPOCH_HOURS : requested;
  }

  static QueryTimestampFormat nativeClient(QueryTimestampFormat requested) {
    // influxdb-java decodes numeric cells as doubles. Use lossless strings for fine precision on borrowed clients.
    return switch (requested) {
      case EPOCH_NANOS, EPOCH_MICROS -> QueryTimestampFormat.RFC3339;
      default -> supported(requested);
    };
  }

  static String epoch(QueryTimestampFormat format) {
    return switch (format) {
      case RFC3339 -> null;
      case EPOCH_NANOS -> "ns";
      case EPOCH_MICROS -> "u";
      case EPOCH_MILLIS -> "ms";
      case EPOCH_SECONDS -> "s";
      case EPOCH_MINUTES -> "m";
      case EPOCH_HOURS -> "h";
      case EPOCH_DAYS -> throw new IllegalArgumentException("Influx does not support epoch days");
    };
  }
}
