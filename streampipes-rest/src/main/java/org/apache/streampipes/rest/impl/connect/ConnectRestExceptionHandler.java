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

package org.apache.streampipes.rest.impl.connect;

import org.apache.streampipes.extensions.api.connect.exception.WorkerAdapterException;
import org.apache.streampipes.rest.shared.exception.SpProblemDetails;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

/**
 * Maps the worker exception of the connect module, which the shared handler cannot see, onto the
 * same problem detail shape. Ordered before {@code SpRestProblemHandler} so that its catch-all does
 * not answer first.
 */
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE - 100)
public class ConnectRestExceptionHandler {

  @ExceptionHandler(value = {WorkerAdapterException.class})
  public ProblemDetail handleWorkerAdapterException(WorkerAdapterException ex, WebRequest request) {
    var problem = ex.getExceptionMessage() != null
        ? SpProblemDetails.fromLogMessage(HttpStatus.INTERNAL_SERVER_ERROR, ex.getExceptionMessage())
        : SpProblemDetails.of(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    if (request instanceof ServletWebRequest servletWebRequest) {
      SpProblemDetails.withInstance(problem, servletWebRequest.getRequest().getRequestURI());
    }
    return problem;
  }
}
