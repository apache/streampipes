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

package org.apache.streampipes.rest.shared.exception;

import org.apache.streampipes.model.message.Message;
import org.apache.streampipes.model.message.Notification;
import org.apache.streampipes.model.monitoring.SpLogMessage;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Builds RFC 9457 problem details. All error responses of the REST API go through this class so
 * that they share one shape: {@code type} (a {@link ProblemType} URN), {@code title}, {@code status},
 * {@code detail}, {@code instance}, an optional {@code errors} list of {@link ProblemError} and, for
 * problems raised from an {@link SpLogMessage}, the members {@code level}, {@code cause} and
 * {@code fullStackTrace}.
 */
public final class SpProblemDetails {

  public static final String ERRORS = "errors";
  public static final String LEVEL = "level";
  public static final String CAUSE = "cause";
  public static final String FULL_STACK_TRACE = "fullStackTrace";

  private SpProblemDetails() {
  }

  public static ProblemDetail of(HttpStatusCode status,
                                 ProblemType type,
                                 String title,
                                 String detail) {
    var resolvedType = Objects.requireNonNullElse(type, ProblemType.forStatus(status));
    var problem = ProblemDetail.forStatus(status);
    problem.setType(resolvedType.uri());
    problem.setTitle(title != null ? title : resolvedType.title());
    problem.setDetail(detail);
    return problem;
  }

  public static ProblemDetail of(HttpStatusCode status, String detail) {
    return of(status, null, null, detail);
  }

  /**
   * A problem that carries the fields of a log message so that the UI's exception viewers keep
   * working: {@code title} and {@code detail} map directly, {@code level}, {@code cause} and
   * {@code fullStackTrace} become extension members.
   */
  public static ProblemDetail fromLogMessage(HttpStatusCode status, SpLogMessage message) {
    var type = ProblemType.forStatus(status);
    if (type == ProblemType.INTERNAL_ERROR) {
      type = ProblemType.OPERATION_FAILED;
    }
    var problem = of(status, type, message.getTitle(), message.getDetail());
    if (message.getLevel() != null) {
      problem.setProperty(LEVEL, message.getLevel());
    }
    if (message.getCause() != null) {
      problem.setProperty(CAUSE, message.getCause());
    }
    if (message.getFullStackTrace() != null) {
      problem.setProperty(FULL_STACK_TRACE, message.getFullStackTrace());
    }
    return problem;
  }

  public static ProblemDetail fromException(HttpStatusCode status, Exception exception) {
    return fromLogMessage(status, SpLogMessage.from(exception));
  }

  /**
   * Converts a legacy {@link Message} envelope. The first notification becomes the {@code detail},
   * further notifications are listed in {@code errors}.
   */
  public static ProblemDetail fromMessage(HttpStatusCode status, Message message) {
    var notifications = message != null && message.getNotifications() != null
        ? message.getNotifications()
        : List.<Notification>of();
    if (notifications.isEmpty()) {
      return of(status, null);
    }
    var problem = of(status, describe(notifications.get(0)));
    if (notifications.size() > 1) {
      var errors = new ArrayList<ProblemError>();
      notifications.stream().skip(1).map(n -> ProblemError.of(describe(n))).forEach(errors::add);
      problem.setProperty(ERRORS, errors);
    }
    return problem;
  }

  public static ProblemDetail fromNotification(HttpStatusCode status, Notification notification) {
    return of(status, describe(notification));
  }

  public static ProblemDetail withInstance(ProblemDetail problem, String requestPath) {
    if (problem.getInstance() == null && requestPath != null && !requestPath.isBlank()) {
      problem.setInstance(URI.create(requestPath));
    }
    return problem;
  }

  /**
   * The sentence a legacy {@link Message} envelope carries: the title, description and additional
   * information of its first notification.
   */
  public static String describe(Message message) {
    if (message == null || message.getNotifications() == null || message.getNotifications().isEmpty()) {
      return null;
    }
    return describe(message.getNotifications().get(0));
  }

  public static String describe(Notification notification) {
    if (notification == null) {
      return null;
    }
    var parts = Stream.of(notification.getTitle(), notification.getDescription(),
            notification.getAdditionalInformation())
        .filter(part -> part != null && !part.isBlank())
        .distinct()
        .toList();
    return parts.isEmpty() ? null : String.join(". ", parts);
  }
}
