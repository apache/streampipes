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

package org.apache.streampipes.wrapper.standalone;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.wrapper.params.compat.SinkParams;

import java.time.Instant;

/**
 * An abstract class representing a StreamPipesNotificationSink in StreamPipes.
 * <p>
 * It provides some share functionalities for all notification sinks in StreamPipes.
 * Thereby, it slightly modifies the interfaces to implement for the actual data sink compared
 * to sinks directly inheriting from {@link StreamPipesDataSink}.
 */

public abstract class StreamPipesNotificationSink extends StreamPipesDataSink {

  /**
   * Default waiting time in minutes between two consecutive notifications.
   */
  public static final int DEFAULT_WAITING_TIME_MINUTES = 10;

  /**
   * Key for the silent period parameter.
   */
  public static final String KEY_SILENT_PERIOD = "silentPeriod";

  /**
   * The epoch second of the last message sent.
   */
  private long lastMessageEpochSecond = -1;

  /**
   * The silent period in seconds between two consecutive notifications.
   */
  private long silentPeriodInSeconds;

  /**
   * Checks if a notification should be sent based on the silent period.
   *
   * @return true if a notification should be sent, false otherwise.
   */
  private boolean shouldSendNotification() {
    if (this.lastMessageEpochSecond == -1) {
      return true;
    } else {
      return Instant.now()
                    .getEpochSecond() >= (this.lastMessageEpochSecond + this.silentPeriodInSeconds);
    }
  }

  /**
   * This method is meant to be overridden by child classes.
   * It contains some important logic, and child classes are expected to call
   * the super.onInvocation() to ensure proper behavior.
   */
  public void onInvocation(
      SinkParams parameters,
      EventSinkRuntimeContext runtimeContext
  ) throws SpRuntimeException {
    // convert input given in minutes to seconds
    // this is later used to determine if a notification should be sent
    this.silentPeriodInSeconds = parameters.extractor()
                                           .singleValueParameter(KEY_SILENT_PERIOD, Integer.class) * 60
    ;
  }

  public final void onEvent(Event inputEvent) {
    if (shouldSendNotification()) {
      onNotificationEvent(inputEvent);

      lastMessageEpochSecond = Instant.now()
                                      .getEpochSecond();
    }
  }

  /**
   * Abstract method to be implemented by subclasses for handling notification events.
   * This should only contain logic that is directly related to the notification and
   * is only executed when the silent period is over.
   *
   * @param inputEvent The event triggering the notification.
   */
  public abstract void onNotificationEvent(Event inputEvent);

  @Override
  public DataSinkDescription declareModel() {

    var builder = declareModelWithoutSilentPeriod();

    builder.requiredIntegerParameter(
        Labels.from(
            KEY_SILENT_PERIOD,
            "Silent Period [min]",
            "The minimum number of minutes between two consecutive notifications that are sent"
        ),
        DEFAULT_WAITING_TIME_MINUTES
    );

    return builder.build();

  }

  /**
   * Abstract method to be implemented by subclasses for declaring the description of the data sink.
   * Unlike {@link StreamPipesDataSink#declareModel()} it is expected to return a {@link DataSinkBuilder}
   *
   * @return The DataSinkBuilder representing the notification model.
   */

  public abstract DataSinkBuilder declareModelWithoutSilentPeriod();

}
