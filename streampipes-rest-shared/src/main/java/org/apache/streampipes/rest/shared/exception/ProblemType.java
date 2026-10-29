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

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.net.URI;

/**
 * Stable problem types of the REST API. The {@link #uri()} of a type is the {@code type} member of
 * the RFC 9457 problem detail a client receives, so clients can branch on it without parsing the
 * human-readable {@code detail}.
 */
public enum ProblemType {

  BAD_REQUEST("bad-request", "Bad request", HttpStatus.BAD_REQUEST),
  VALIDATION_FAILED("validation-failed", "Validation failed", HttpStatus.BAD_REQUEST),
  UNAUTHORIZED("unauthorized", "Unauthorized", HttpStatus.UNAUTHORIZED),
  FORBIDDEN("forbidden", "Forbidden", HttpStatus.FORBIDDEN),
  NOT_FOUND("not-found", "Not found", HttpStatus.NOT_FOUND),
  CONFLICT("conflict", "Conflict", HttpStatus.CONFLICT),
  /**
   * An operation that was accepted could not be completed, typically because an extension service
   * or an external system reported an error. The problem carries the {@code level}, {@code cause}
   * and {@code fullStackTrace} members of the underlying log message.
   */
  OPERATION_FAILED("operation-failed", "Operation failed", HttpStatus.INTERNAL_SERVER_ERROR),
  SERVICE_UNAVAILABLE("service-unavailable", "Service unavailable", HttpStatus.SERVICE_UNAVAILABLE),
  INTERNAL_ERROR("internal-error", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);

  public static final String URN_PREFIX = "urn:streampipes:problem:";

  private final String slug;
  private final String title;
  private final HttpStatus defaultStatus;

  ProblemType(String slug, String title, HttpStatus defaultStatus) {
    this.slug = slug;
    this.title = title;
    this.defaultStatus = defaultStatus;
  }

  public String slug() {
    return slug;
  }

  public URI uri() {
    return URI.create(URN_PREFIX + slug);
  }

  public String title() {
    return title;
  }

  public HttpStatus defaultStatus() {
    return defaultStatus;
  }

  /**
   * The generic type for a status code, used for problems that are not raised with an explicit type.
   */
  public static ProblemType forStatus(HttpStatusCode status) {
    var resolved = HttpStatus.resolve(status.value());
    if (resolved == null) {
      return status.is4xxClientError() ? BAD_REQUEST : INTERNAL_ERROR;
    }
    return switch (resolved) {
      case BAD_REQUEST -> BAD_REQUEST;
      case UNAUTHORIZED -> UNAUTHORIZED;
      case FORBIDDEN -> FORBIDDEN;
      case NOT_FOUND -> NOT_FOUND;
      case CONFLICT -> CONFLICT;
      case SERVICE_UNAVAILABLE -> SERVICE_UNAVAILABLE;
      default -> resolved.is4xxClientError() ? BAD_REQUEST : INTERNAL_ERROR;
    };
  }
}
