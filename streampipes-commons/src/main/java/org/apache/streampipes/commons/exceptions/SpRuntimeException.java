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

package org.apache.streampipes.commons.exceptions;

public class SpRuntimeException extends SpException {

  private static final long serialVersionUID = 193141189399279147L;

  /**
   * Creates a new Exception with the given message and null as the cause.
   *
   * @param message The exception message
   */
  public SpRuntimeException(String message) {
    super(message);
  }

  /**
   * Creates a new exception with a null message and the given cause.
   *
   * @param cause The exception that caused this exception
   */
  public SpRuntimeException(Throwable cause) {
    super(cause);
  }

  /**
   * Creates a new exception with the given message and cause
   *
   * @param message The exception message
   * @param cause   The exception that caused this exception
   */
  public SpRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }
}
