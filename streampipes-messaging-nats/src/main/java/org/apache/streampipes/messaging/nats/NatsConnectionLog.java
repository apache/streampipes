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
package org.apache.streampipes.messaging.nats;

import io.nats.client.AuthenticationException;
import io.nats.client.Connection;
import io.nats.client.ConnectionListener;
import io.nats.client.impl.ErrorListenerLoggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.function.LongSupplier;

/** Logs transient connection outages without hiding unrelated NATS errors. */
public class NatsConnectionLog extends ErrorListenerLoggerImpl implements ConnectionListener {

  private static final long REPORT_INTERVAL_NANOS = Duration.ofSeconds(30).toNanos();
  private final String context;
  private final Logger logger;
  private final LongSupplier nanoTime;
  private boolean stopped;
  private boolean outage;
  private long lastReport;
  private long suppressed;

  public NatsConnectionLog(String context) {
    this(context, LoggerFactory.getLogger(NatsConnectionLog.class), System::nanoTime);
  }

  NatsConnectionLog(String context, Logger logger, LongSupplier nanoTime) {
    this.context = context;
    this.logger = logger;
    this.nanoTime = nanoTime;
  }

  @Override
  public void exceptionOccurred(Connection connection, Exception exception) {
    Throwable cause = exception;
    while ((cause instanceof ExecutionException || cause instanceof CompletionException)
        && cause.getCause() != null) {
      cause = cause.getCause();
    }
    if (cause instanceof IOException && !(cause instanceof AuthenticationException)
        && !(cause instanceof SSLException)) {
      unavailable(cause.getClass().getSimpleName());
    } else {
      super.exceptionOccurred(connection, exception);
    }
  }

  public synchronized void unavailable(String reason) {
    if (stopped) {
      return;
    }
    long now = nanoTime.getAsLong();
    if (!outage || now - lastReport >= REPORT_INTERVAL_NANOS) {
      logger.warn("NATS unavailable for {} ({}); suppressed {} repeated outage messages", context, reason, suppressed);
      lastReport = now;
      suppressed = 0;
    } else {
      suppressed++;
    }
    outage = true;
  }

  @Override
  public synchronized void connectionEvent(Connection connection, Events event) {
    if (stopped) {
      return;
    }
    if (event == Events.DISCONNECTED || event == Events.CLOSED) {
      unavailable(event.name());
    } else if (event == Events.RECONNECTED || event == Events.CONNECTED) {
      if (outage || event == Events.RECONNECTED) {
        logger.info("NATS connection restored for {}; suppressed {} repeated outage messages", context, suppressed);
      }
      outage = false;
      suppressed = 0;
    }
  }

  public synchronized void stop() {
    stopped = true;
  }
}
