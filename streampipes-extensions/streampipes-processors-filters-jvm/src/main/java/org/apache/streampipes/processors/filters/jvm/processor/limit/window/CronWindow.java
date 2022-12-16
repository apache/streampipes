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

import org.apache.streampipes.processors.filters.jvm.processor.limit.util.EventSelection;
import org.apache.streampipes.processors.filters.jvm.processor.limit.util.SchedulerUtil;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;

import org.quartz.JobDetail;
import org.quartz.Trigger;

public class CronWindow extends ScheduleWindow {
  private String cronExpression;

  public CronWindow(String cronExpression,
                    EventSelection eventSelection,
                    SpOutputCollector outputCollector) {
    super(eventSelection, outputCollector);
    this.cronExpression = cronExpression;
  }

  @Override
  JobDetail getJob() {
    return SchedulerUtil.createJob(this);
  }

  @Override
  Trigger getTrigger() {
    return SchedulerUtil.getCronTrigger(cronExpression);
  }

}
