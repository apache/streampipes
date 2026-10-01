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

package org.apache.streampipes.dataexplorer.api;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.runtime.Event;

import java.util.List;

public interface ITimeSeriesStorage {

  void onEvent(Event event) throws SpRuntimeException;

  /**
   * Writes a batch of events. Implementations that support bulk writes should override this method and write all
   * events in a single request to the storage; the default falls back to writing the events one by one.
   */
  default void onEvents(List<Event> events) throws SpRuntimeException {
    for (var event : events) {
      onEvent(event);
    }
  }

  void close() throws SpRuntimeException;
}
