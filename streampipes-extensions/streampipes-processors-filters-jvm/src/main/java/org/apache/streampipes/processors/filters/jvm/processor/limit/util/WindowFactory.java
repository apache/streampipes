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
package org.apache.streampipes.processors.filters.jvm.processor.limit.util;

import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.processors.filters.jvm.processor.limit.window.CronWindow;
import org.apache.streampipes.processors.filters.jvm.processor.limit.window.LengthWindow;
import org.apache.streampipes.processors.filters.jvm.processor.limit.window.TimeWindow;
import org.apache.streampipes.processors.filters.jvm.processor.limit.window.Window;

public class WindowFactory {
  private final WindowType windowType;
  private final Object windowExpression;
  private final EventSelection eventSelection;
  private final SpOutputCollector outputCollector;

  public WindowFactory(WindowType windowType,
                       Object windowExpression,
                       EventSelection eventSelection,
                       SpOutputCollector outputCollector) {
    this.windowType = windowType;
    this.windowExpression = windowExpression;
    this.eventSelection = eventSelection;
    this.outputCollector = outputCollector;
  }

  public Window create() {
    if (WindowType.TIME == windowType) {
      return new TimeWindow((Integer) windowExpression, eventSelection, outputCollector);
    } else if (WindowType.LENGTH == windowType) {
      return new LengthWindow((Integer) windowExpression, eventSelection, outputCollector);
    } else if (WindowType.CRON == windowType) {
      return new CronWindow((String) windowExpression, eventSelection, outputCollector);
    } else {
      return null;
    }
  }

}
