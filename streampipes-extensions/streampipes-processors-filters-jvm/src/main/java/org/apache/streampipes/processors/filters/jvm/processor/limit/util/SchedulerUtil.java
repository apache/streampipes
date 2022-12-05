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

import org.apache.streampipes.processors.filters.jvm.processor.limit.window.Window;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

public class SchedulerUtil {
  static final String WINDOW_KEY = "window";

  public static JobDetail createJob(Window window) {
    JobDataMap dataMap = new JobDataMap();
    dataMap.put(WINDOW_KEY, window);
    return JobBuilder.newJob(ProcessJob.class)
        .setJobData(dataMap)
        .build();
  }

  public static Trigger getFixedRateTrigger(int intervalInMilliseconds) {
    final SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder
        .simpleSchedule()
        .withIntervalInMilliseconds(intervalInMilliseconds)
        .repeatForever();
    return TriggerBuilder
        .newTrigger()
        .withSchedule(scheduleBuilder)
        .startNow()
        .build();
  }

  public static Trigger getCronTrigger(String cronExpression) {
    return TriggerBuilder.newTrigger()
        .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
        .build();
  }

}
