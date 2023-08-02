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

import jakarta.ws.rs.core.Response;

public abstract class AbstractSharedRestInterface {

  protected <T> Response ok(T entity) {
    return Response
        .ok(entity)
        .build();
  }

  protected <T> Response badRequest(T entity) {
    return error(entity, 400);
  }

  protected <T> Response notFound(T entity) {
    return error(entity, 404);
  }

  protected <T> Response notFound() {
    return Response.status(404).build();
  }

  protected <T> Response serverError(T entity) {
    return error(entity, 500);
  }

  protected <T> Response error(T entity, Integer statusCode) {
    return Response
        .status(statusCode)
        .entity(entity)
        .build();
  }

  protected Response badRequest() {
    return Response.status(400).build();
  }

  protected Response ok() {
    return Response.ok().build();
  }

  protected Response created() {
    return Response.status(201).build();
  }

  protected Response fail() {
    return Response.serverError().build();
  }

}
