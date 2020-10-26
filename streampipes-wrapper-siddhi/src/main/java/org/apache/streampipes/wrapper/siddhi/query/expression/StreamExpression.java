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
package org.apache.streampipes.wrapper.siddhi.query.expression;

import org.apache.streampipes.wrapper.siddhi.constants.SiddhiConstants;
import org.apache.streampipes.wrapper.siddhi.query.expression.window.WindowExpression;

public class StreamExpression extends Expression {

  private String streamName;
  private String streamAlias;
  private WindowExpression windowExpression;

  public StreamExpression() {

  }

  public StreamExpression(String streamName) {
    this.streamName = streamName;
  }

  public StreamExpression(String streamName, WindowExpression windowExpression) {
    this.streamName = streamName;
    this.windowExpression = windowExpression;
  }

  public StreamExpression(String streamAlias, String streamName) {
    this(streamName);
    this.streamAlias = streamAlias;
  }

  @Override
  public String toSiddhiEpl() {
    String streamName = this.streamAlias == null ? this.streamName :
            join(SiddhiConstants.EMPTY, this.streamAlias, SiddhiConstants.EQUALS, this.streamName);

    if (this.windowExpression != null) {
      streamName = join(SiddhiConstants.EMPTY, streamName, this.windowExpression.toSiddhiEpl());
    }

    return streamName;
  }
}
