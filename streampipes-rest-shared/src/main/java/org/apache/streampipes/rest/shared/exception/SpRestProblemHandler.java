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

import org.apache.streampipes.commons.exceptions.NoServiceEndpointsAvailableException;
import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.commons.exceptions.connect.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.NoSuchElementException;

/**
 * The one error contract of the REST API: every exception that leaves a resource becomes an RFC 9457
 * problem detail (see {@link SpProblemDetails}) with the HTTP status that describes the failure.
 *
 * <p>The handler runs with the lowest precedence and ends with a catch-all for {@link Exception}.
 * A {@code @ControllerAdvice} that must take precedence for its own exception types has to declare a
 * higher precedence (a lower {@link Order} value), otherwise this handler answers first.
 */
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class SpRestProblemHandler extends ResponseEntityExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(SpRestProblemHandler.class);

  @ExceptionHandler(SpProblemException.class)
  protected ResponseEntity<Object> handleSpProblemException(SpProblemException ex, WebRequest request) {
    log(ex.getStatus(), ex);
    return respond(ex.toProblemDetail(), request);
  }

  @ExceptionHandler(SpLogMessageException.class)
  protected ResponseEntity<Object> handleSpLogMessageException(SpLogMessageException ex, WebRequest request) {
    log(ex.getStatus(), ex);
    return respond(SpProblemDetails.fromLogMessage(ex.getStatus(), ex.getSpMessage()), request);
  }

  @ExceptionHandler({AdapterException.class, ParseException.class})
  protected ResponseEntity<Object> handleAdapterException(Exception ex, WebRequest request) {
    log(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    return respond(SpProblemDetails.fromException(HttpStatus.INTERNAL_SERVER_ERROR, ex), request);
  }

  @ExceptionHandler(NoServiceEndpointsAvailableException.class)
  protected ResponseEntity<Object> handleNoServiceEndpointsAvailableException(
      NoServiceEndpointsAvailableException ex,
      WebRequest request) {
    log(HttpStatus.SERVICE_UNAVAILABLE, ex);
    return respond(SpProblemDetails.fromException(HttpStatus.SERVICE_UNAVAILABLE, ex), request);
  }

  @SuppressWarnings("deprecation")
  @ExceptionHandler(SpMessageException.class)
  protected ResponseEntity<Object> handleSpMessageException(SpMessageException ex, WebRequest request) {
    log(ex.getStatus(), ex);
    return respond(SpProblemDetails.fromMessage(ex.getStatus(), ex.getSpMessage()), request);
  }

  @SuppressWarnings("deprecation")
  @ExceptionHandler(SpNotificationException.class)
  protected ResponseEntity<Object> handleSpNotificationException(SpNotificationException ex, WebRequest request) {
    log(ex.getStatus(), ex);
    return respond(SpProblemDetails.fromNotification(ex.getStatus(), ex.getNotification()), request);
  }

  @SuppressWarnings("deprecation")
  @ExceptionHandler(BadRequestException.class)
  protected ResponseEntity<Object> handleBadRequestException(BadRequestException ex, WebRequest request) {
    log(HttpStatus.BAD_REQUEST, ex);
    return respond(SpProblemDetails.of(HttpStatus.BAD_REQUEST, ex.getMessage()), request);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  protected ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
    log(HttpStatus.BAD_REQUEST, ex);
    return respond(SpProblemDetails.of(HttpStatus.BAD_REQUEST, ex.getMessage()), request);
  }

  @ExceptionHandler(NoSuchElementException.class)
  protected ResponseEntity<Object> handleNoSuchElementException(NoSuchElementException ex, WebRequest request) {
    log(HttpStatus.NOT_FOUND, ex);
    return respond(SpProblemDetails.of(HttpStatus.NOT_FOUND, ex.getMessage()), request);
  }

  @ExceptionHandler(AccessDeniedException.class)
  protected ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
    log(HttpStatus.FORBIDDEN, ex);
    return respond(SpProblemDetails.of(HttpStatus.FORBIDDEN, ex.getMessage()), request);
  }

  @ExceptionHandler(AuthenticationException.class)
  protected ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
    log(HttpStatus.UNAUTHORIZED, ex);
    return respond(SpProblemDetails.of(HttpStatus.UNAUTHORIZED, ex.getMessage()), request);
  }

  @ExceptionHandler(Exception.class)
  protected ResponseEntity<Object> handleUnexpectedException(Exception ex, WebRequest request) {
    log(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    return respond(SpProblemDetails.of(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()), request);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                HttpHeaders headers,
                                                                HttpStatusCode status,
                                                                WebRequest request) {
    var problem = SpProblemDetails.of(status, ProblemType.VALIDATION_FAILED, null,
        "The request body has " + ex.getErrorCount() + " invalid field(s)");
    var errors = ex.getBindingResult().getAllErrors().stream()
        .map(error -> error instanceof FieldError fieldError
            ? ProblemError.of(fieldError.getDefaultMessage(), "/" + fieldError.getField().replace('.', '/'))
            : ProblemError.of(error.getDefaultMessage()))
        .toList();
    problem.setProperty(SpProblemDetails.ERRORS, errors);
    return handleExceptionInternal(ex, problem, headers, status, request);
  }

  /**
   * Problem details that Spring MVC builds for its own exceptions get the StreamPipes {@code type}
   * and {@code instance} so that all errors look alike.
   */
  @Override
  protected ResponseEntity<Object> createResponseEntity(Object body,
                                                        HttpHeaders headers,
                                                        HttpStatusCode statusCode,
                                                        WebRequest request) {
    if (body instanceof ProblemDetail problem) {
      if (problem.getType() == null || "about:blank".equals(problem.getType().toString())) {
        var type = ProblemType.forStatus(statusCode);
        problem.setType(type.uri());
        if (problem.getTitle() == null) {
          problem.setTitle(type.title());
        }
      }
      SpProblemDetails.withInstance(problem, requestPath(request));
    }
    return super.createResponseEntity(body, headers, statusCode, request);
  }

  protected ResponseEntity<Object> respond(ProblemDetail problem, WebRequest request) {
    SpProblemDetails.withInstance(problem, requestPath(request));
    return ResponseEntity.status(problem.getStatus()).body(problem);
  }

  private static String requestPath(WebRequest request) {
    if (request instanceof ServletWebRequest servletWebRequest) {
      return servletWebRequest.getRequest().getRequestURI();
    }
    return null;
  }

  private static void log(HttpStatusCode status, Exception ex) {
    if (status.is5xxServerError()) {
      LOG.error("Request failed with status {}: {}", status.value(), ex.getMessage(), ex);
    } else {
      LOG.debug("Request rejected with status {}: {}", status.value(), ex.getMessage());
    }
  }
}
