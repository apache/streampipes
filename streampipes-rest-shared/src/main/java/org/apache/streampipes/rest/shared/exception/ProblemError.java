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

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * One entry of the {@code errors} member of a problem detail.
 *
 * @param detail  what is wrong, in a sentence a user can act on
 * @param pointer where it is wrong: a JSON pointer into the request body (for example
 *                {@code /configuration/0/host}), a query parameter name, or {@code null} when the
 *                error does not belong to one input
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProblemError(String detail, String pointer) {

  public static ProblemError of(String detail) {
    return new ProblemError(detail, null);
  }

  public static ProblemError of(String detail, String pointer) {
    return new ProblemError(detail, pointer);
  }
}
