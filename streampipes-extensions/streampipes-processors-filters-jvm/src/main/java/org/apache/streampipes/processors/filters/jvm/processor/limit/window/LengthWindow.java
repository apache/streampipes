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
package org.apache.streampipes.processors.filters.jvm.processor.limit.window;

import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.processors.filters.jvm.processor.limit.util.EventSelection;

import java.util.ArrayList;
import java.util.List;

public class LengthWindow implements Window {
  private Integer windowSize;
  private EventSelection eventSelection;
  private SpOutputCollector outputCollector;
  private List<Event> events;

  public LengthWindow(Integer windowSize,
                      EventSelection eventSelection,
                      SpOutputCollector outputCollector) {
    this.windowSize = windowSize;
    this.eventSelection = eventSelection;
    this.outputCollector = outputCollector;
    this.events = new ArrayList<>();
  }

  @Override
  public void init() {
    // do nothing.
  }

  @Override
  public void onEvent(Event event) {
    events.add(event);
    onTrigger();
  }

  @Override
  public void destroy() {
    events.clear();
  }

  @Override
  public void onTrigger() {
    if (events.size() == 1) {
      if (eventSelection == EventSelection.FIRST) {
        emit(events.get(0));
      }
    } else if (events.size() == windowSize) {
      if (eventSelection == EventSelection.LAST) {
        emit(events.get(windowSize - 1));
      } else if (eventSelection == EventSelection.ALL) {
        events.forEach(this::emit);
      }
      events.clear();
    }
  }

  private void emit(Event e) {
    outputCollector.collect(e);
  }

}
