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

package org.apache.streampipes.model.output;

public class KeepOutputStrategy extends OutputStrategy {

  private static final long serialVersionUID = 7643705399683055563L;

  @Deprecated
  String eventName;

  private boolean keepBoth;

  public KeepOutputStrategy() {
    super();
  }

  public KeepOutputStrategy(KeepOutputStrategy other) {
    super(other);
    this.eventName = other.getEventName();
    this.keepBoth = other.isKeepBoth();
  }

  public KeepOutputStrategy(String name, String eventName) {
    super(name);
    this.eventName = eventName;
    this.keepBoth = true;
  }

  public KeepOutputStrategy(String name, boolean keepBoth) {
    super(name);
    this.keepBoth = keepBoth;
  }

  public String getEventName() {
    return eventName;
  }

  public void setEventName(String eventName) {
    this.eventName = eventName;
  }

  public boolean isKeepBoth() {
    return keepBoth;
  }

  public void setKeepBoth(boolean keepBoth) {
    this.keepBoth = keepBoth;
  }
}
