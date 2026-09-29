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

package org.apache.streampipes.dataexplorer.api.query;

import java.util.Iterator;

/**
 * Single-consumer cursor over one execution, including every result series in backend order.
 * Closing releases database resources and cancels unfinished work. Consumers must always close it.
 * A cursor does not imply snapshot isolation beyond the database's own query guarantees.
 */
public interface DatasetQueryCursor extends Iterator<QueryBatch>, AutoCloseable {
  /** Stable for the entire execution, including all batches and series. */
  default QueryTimestampFormat timestampFormat() {
    return QueryTimestampFormat.EPOCH_MILLIS;
  }

  @Override
  void close();
}
