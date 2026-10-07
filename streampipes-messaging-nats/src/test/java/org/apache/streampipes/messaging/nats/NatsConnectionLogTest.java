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
import io.nats.client.ConnectionListener.Events;
import io.nats.client.impl.ErrorListenerLoggerImpl;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import javax.net.ssl.SSLHandshakeException;

import java.io.IOException;
import java.net.ConnectException;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

public class NatsConnectionLogTest {

  private static final String WARNING =
      "NATS unavailable for {} ({}); suppressed {} repeated outage messages";
  private static final String RECOVERY =
      "NATS connection restored for {}; suppressed {} repeated outage messages";

  @Test
  public void limitsOutageMessagesAndReportsSuppressedCountOnRecovery() {
    var logger = mock(Logger.class);
    var now = new AtomicLong();
    var log = new NatsConnectionLog("test", logger, now::get);
    log.exceptionOccurred(null, new ConnectException());
    for (int i = 0; i < 100; i++) {
      log.exceptionOccurred(null, new ExecutionException(new IOException()));
    }
    verify(logger).warn(WARNING, "test", "ConnectException", 0L);
    verify(logger, never()).warn(WARNING, "test", "IOException", 0L);
    now.set(Duration.ofSeconds(30).toNanos());
    log.exceptionOccurred(null, new ConnectException());
    verify(logger).warn(WARNING, "test", "ConnectException", 100L);
    log.exceptionOccurred(null, new ConnectException());
    log.connectionEvent(null, Events.RECONNECTED);
    verify(logger).info(RECOVERY, "test", 1L);
    log.exceptionOccurred(null, new ConnectException());
    verify(logger, times(2)).warn(WARNING, "test", "ConnectException", 0L);
  }

  @Test
  public void initialConnectionDoesNotClaimRecoveryButReplacementDoes() {
    var logger = mock(Logger.class);
    var log = new NatsConnectionLog("test", logger, () -> 0L);
    log.connectionEvent(null, Events.CONNECTED);
    verifyNoInteractions(logger);
    log.connectionEvent(null, Events.CLOSED);
    log.connectionEvent(null, Events.CONNECTED);
    verify(logger).info(RECOVERY, "test", 0L);
  }

  @Test
  public void intentionalShutdownDoesNotReportOutageOrRecovery() {
    var logger = mock(Logger.class);
    var log = new NatsConnectionLog("test", logger, () -> 0L);
    log.stop();
    log.connectionEvent(null, Events.DISCONNECTED);
    log.connectionEvent(null, Events.CLOSED);
    log.connectionEvent(null, Events.RECONNECTED);
    log.exceptionOccurred(null, new IOException());
    verifyNoInteractions(logger);
  }

  @Test
  public void applicationAndTlsErrorsAreNotSuppressedDuringOutage() {
    var logger = mock(Logger.class);
    var log = new NatsConnectionLog("test", logger, () -> 0L);
    var jul = java.util.logging.Logger.getLogger(ErrorListenerLoggerImpl.class.getName());
    var handler = mock(Handler.class);
    jul.addHandler(handler);
    try {
      log.unavailable("DISCONNECTED");
      log.exceptionOccurred(null, new IllegalStateException("application failure"));
      log.exceptionOccurred(null, new SSLHandshakeException("TLS failure"));
      log.errorOccurred(null, "Permissions Violation");
      log.exceptionOccurred(null, new AuthenticationException("authentication failed"));
      verify(handler, times(4)).publish(any(LogRecord.class));
    } finally {
      jul.removeHandler(handler);
    }
  }
}
