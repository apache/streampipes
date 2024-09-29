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
package org.apache.streampipes.rest.shared.impl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class AbstractSharedRestInterface {

  protected <T> ResponseEntity<T> ok(T entity) {
    return ResponseEntity.ok(entity);
  }

  protected <T> ResponseEntity<T> badRequest(T entity) {
    return error(entity, HttpStatus.BAD_REQUEST.value());
  }

  protected <T> ResponseEntity<T> notFound(T entity) {
    return error(entity, HttpStatus.NOT_FOUND.value());
  }

  protected <T> ResponseEntity<Void> notFound() {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

  protected <T> ResponseEntity<T> serverError(T entity) {
    return error(entity, HttpStatus.INTERNAL_SERVER_ERROR.value());
  }

  protected <T> ResponseEntity<T> error(T entity, Integer statusCode) {
    return ResponseEntity.status(statusCode).body(entity);
  }

  protected ResponseEntity<Void> badRequest() {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
  }

  protected ResponseEntity<Void> ok() {
    return ResponseEntity.ok().build();
  }

  protected ResponseEntity<Void> created() {
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  protected ResponseEntity<Void> fail() {
    return ResponseEntity.internalServerError().build();
  }

}
