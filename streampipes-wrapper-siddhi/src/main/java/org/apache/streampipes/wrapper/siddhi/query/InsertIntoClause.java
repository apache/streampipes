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
package org.apache.streampipes.wrapper.siddhi.query;

import org.apache.streampipes.wrapper.siddhi.constants.SiddhiConstants;

public class InsertIntoClause extends SiddhiStatement {

  private final boolean insertAllEvents;
  private final String streamName;

  public static InsertIntoClause create(String streamName) {
    return new InsertIntoClause(streamName, false);
  }

  public static InsertIntoClause create(String streamName, boolean insertAllEvents) {
    return new InsertIntoClause(streamName, insertAllEvents);
  }

  public InsertIntoClause(String streamName,
                          boolean insertAllEvents) {
    this.streamName = streamName;
    this.insertAllEvents = insertAllEvents;
  }

  @Override
  public String toSiddhiEpl() {
    return join(SiddhiConstants.WHITESPACE, this.insertAllEvents
        ? "insert all events into" : "insert into", streamName);
  }
}
