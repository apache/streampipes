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

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.processors.filters.jvm.processor.limit.util.EventSelection;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public abstract class ScheduleWindow implements Window {
  private EventSelection eventSelection;
  private SpOutputCollector outputCollector;
  private List<Event> events;
  private Scheduler scheduler;
  private static int schedulerCount = 0;

  ScheduleWindow(EventSelection eventSelection,
                 SpOutputCollector outputCollector) {
    this.eventSelection = eventSelection;
    this.outputCollector = outputCollector;
    this.events = new ArrayList<>();
  }

  abstract JobDetail getJob();

  abstract Trigger getTrigger();

  @Override
  public void init() throws SpRuntimeException {
    try {
      scheduler = new StdSchedulerFactory(getSchedulerProps()).getScheduler();
      scheduler.start();
      scheduler.scheduleJob(getJob(), getTrigger());
    } catch (SchedulerException e) {
      throw new SpRuntimeException("Unable to initialize time window scheduler.", e);
    }
  }

  @Override
  public void onEvent(Event event) {
    events.add(event);
  }

  @Override
  public void onTrigger() {
    if (!events.isEmpty()) {
      switch (eventSelection) {
        case FIRST:
          emit(events.get(0));
          break;
        case LAST:
          emit(events.get(events.size() - 1));
          break;
        case ALL:
          events.forEach(this::emit);
          break;
      }
      events.clear();
    }
  }

  @Override
  public void destroy() throws SpRuntimeException {
    events.clear();
    if (scheduler != null) {
      try {
        scheduler.clear();
        scheduler.shutdown();
        scheduler = null;
      } catch (SchedulerException e) {
        throw new SpRuntimeException("Unable to shutdown time window scheduler.", e);
      }
    }
  }

  private void emit(Event e) {
    outputCollector.collect(e);
  }

  private Properties getSchedulerProps() {
    Properties properties = new Properties();
    properties.setProperty("org.quartz.scheduler.instanceName", "WindowSchedulerName:" + (++schedulerCount));
    properties.setProperty("org.quartz.scheduler.instanceId", "WindowSchedulerID:" + schedulerCount);
    properties.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
    properties.setProperty("org.quartz.threadPool.threadCount", "3");
    properties.setProperty("org.quartz.threadPool.threadPriority", "5");
    return properties;
  }

}
