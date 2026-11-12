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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The error page of the service, replacing Spring Boot's {@code BasicErrorController}. Whatever
 * reaches the container's error dispatch, an exception no {@code @ControllerAdvice} handled, a
 * {@code sendError} from the security filter chain, or an unknown path, is answered with the same
 * RFC 9457 problem detail as every other error of the API.
 */
@RestController
@RequestMapping("${server.error.path:${error.path:/error}}")
public class SpProblemErrorController implements ErrorController {

  private static final Logger LOG = LoggerFactory.getLogger(SpProblemErrorController.class);

  @RequestMapping
  public ResponseEntity<ProblemDetail> error(HttpServletRequest request) {
    var status = status(request);
    var exception = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
    var requestUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

    ProblemDetail problem;
    if (exception instanceof SpProblemException problemException) {
      problem = problemException.toProblemDetail();
    } else {
      problem = SpProblemDetails.of(status, detail(request, exception));
    }
    SpProblemDetails.withInstance(problem, requestUri != null ? requestUri : request.getRequestURI());

    if (status.is5xxServerError() && exception != null) {
      LOG.debug("Answering unhandled exception on {} with status {}", requestUri, status.value(), exception);
    }

    return ResponseEntity.status(problem.getStatus())
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(problem);
  }

  private static HttpStatus status(HttpServletRequest request) {
    var statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
    var status = statusCode != null ? HttpStatus.resolve(statusCode) : null;
    return status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR;
  }

  private static String detail(HttpServletRequest request, Throwable exception) {
    var rootCause = exception;
    while (rootCause != null && rootCause.getCause() != null && rootCause.getCause() != rootCause) {
      rootCause = rootCause.getCause();
    }
    if (rootCause != null && rootCause.getMessage() != null && !rootCause.getMessage().isBlank()) {
      return rootCause.getMessage();
    }
    var message = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
    return message != null && !message.isBlank() ? message : null;
  }
}
