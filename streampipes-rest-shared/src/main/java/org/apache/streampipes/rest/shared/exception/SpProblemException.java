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
import org.springframework.http.ProblemDetail;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The exception a REST resource throws to answer with an RFC 9457 problem detail.
 * {@link SpRestProblemHandler} turns it into the response; resources never build error bodies
 * themselves.
 *
 * <p>Use the factories for the common cases ({@link #notFound(String)}, {@link #forbidden(String)},
 * {@link #badRequest(String)}, {@link #validationFailed(String, List)}, {@link #conflict(String, List)})
 * and {@link #of(HttpStatus, String)} for everything else. {@code detail} is the sentence a user or an
 * LLM reads; {@code errors} lists individual inputs that were rejected.
 */
public class SpProblemException extends RuntimeException {

  private final HttpStatus status;
  private final ProblemType type;
  private final String title;
  private final List<ProblemError> errors;
  private final Map<String, Object> properties = new LinkedHashMap<>();

  public SpProblemException(HttpStatus status,
                            ProblemType type,
                            String title,
                            String detail,
                            List<ProblemError> errors,
                            Throwable cause) {
    super(detail, cause);
    this.status = Objects.requireNonNull(status, "status");
    this.type = Objects.requireNonNullElse(type, ProblemType.forStatus(status));
    this.title = title != null ? title : this.type.title();
    this.errors = errors != null ? List.copyOf(errors) : List.of();
  }

  public SpProblemException(HttpStatus status,
                            ProblemType type,
                            String detail) {
    this(status, type, null, detail, null, null);
  }

  public static SpProblemException of(HttpStatus status, String detail) {
    return new SpProblemException(status, ProblemType.forStatus(status), detail);
  }

  public static SpProblemException of(HttpStatus status, Throwable cause) {
    return new SpProblemException(status, ProblemType.forStatus(status), null, messageOf(cause), null, cause);
  }

  public static SpProblemException of(HttpStatus status, String detail, Throwable cause) {
    return new SpProblemException(status, ProblemType.forStatus(status), null, detail, null, cause);
  }

  public static SpProblemException badRequest(String detail) {
    return new SpProblemException(HttpStatus.BAD_REQUEST, ProblemType.BAD_REQUEST, detail);
  }

  public static SpProblemException badRequest(String detail, Throwable cause) {
    return new SpProblemException(HttpStatus.BAD_REQUEST, ProblemType.BAD_REQUEST, null, detail, null, cause);
  }

  public static SpProblemException validationFailed(String detail, List<ProblemError> errors) {
    return new SpProblemException(HttpStatus.BAD_REQUEST, ProblemType.VALIDATION_FAILED, null, detail, errors, null);
  }

  public static SpProblemException unauthorized(String detail) {
    return new SpProblemException(HttpStatus.UNAUTHORIZED, ProblemType.UNAUTHORIZED, detail);
  }

  public static SpProblemException forbidden(String detail) {
    return new SpProblemException(HttpStatus.FORBIDDEN, ProblemType.FORBIDDEN, detail);
  }

  public static SpProblemException notFound(String detail) {
    return new SpProblemException(HttpStatus.NOT_FOUND, ProblemType.NOT_FOUND, detail);
  }

  public static SpProblemException notFound(String resourceName, String id) {
    return notFound(String.format("%s with id %s not found", resourceName, id));
  }

  public static SpProblemException conflict(String detail) {
    return new SpProblemException(HttpStatus.CONFLICT, ProblemType.CONFLICT, detail);
  }

  public static SpProblemException conflict(String detail, List<ProblemError> errors) {
    return new SpProblemException(HttpStatus.CONFLICT, ProblemType.CONFLICT, null, detail, errors, null);
  }

  public static SpProblemException internalError(String detail, Throwable cause) {
    return new SpProblemException(HttpStatus.INTERNAL_SERVER_ERROR, ProblemType.INTERNAL_ERROR, null, detail, null, cause);
  }

  public static SpProblemException operationFailed(String detail, Throwable cause) {
    return new SpProblemException(HttpStatus.INTERNAL_SERVER_ERROR, ProblemType.OPERATION_FAILED, null, detail, null, cause);
  }

  public static SpProblemException serviceUnavailable(String detail, Throwable cause) {
    return new SpProblemException(HttpStatus.SERVICE_UNAVAILABLE, ProblemType.SERVICE_UNAVAILABLE, null, detail, null, cause);
  }

  /**
   * Adds an extension member to the problem detail. {@code type}, {@code title}, {@code status},
   * {@code detail}, {@code instance} and {@code errors} cannot be overridden this way.
   */
  public SpProblemException withProperty(String name, Object value) {
    if (value != null) {
      this.properties.put(name, value);
    }
    return this;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public ProblemType getType() {
    return type;
  }

  public String getTitle() {
    return title;
  }

  public String getDetail() {
    return getMessage();
  }

  public List<ProblemError> getErrors() {
    return errors;
  }

  public Map<String, Object> getProperties() {
    return Map.copyOf(properties);
  }

  public ProblemDetail toProblemDetail() {
    var problem = SpProblemDetails.of(status, type, title, getDetail());
    properties.forEach(problem::setProperty);
    if (!errors.isEmpty()) {
      problem.setProperty(SpProblemDetails.ERRORS, new ArrayList<>(errors));
    }
    return problem;
  }

  private static String messageOf(Throwable cause) {
    return cause != null && cause.getMessage() != null ? cause.getMessage() : "";
  }
}
