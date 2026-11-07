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

package org.apache.streampipes.couchdb.client.exception;

/**
 * Base class of all errors raised by the CouchDB client.
 *
 * <p>Carries the HTTP status and the {@code error}/{@code reason} pair CouchDB returns. Transport failures use
 * status {@code 0}.</p>
 */
public class CouchDbException extends RuntimeException {

  private final int status;
  private final String error;
  private final String reason;

  public CouchDbException(String message, int status, String error, String reason) {
    super(message);
    this.status = status;
    this.error = error;
    this.reason = reason;
  }

  public CouchDbException(String message, Throwable cause) {
    super(message, cause);
    this.status = 0;
    this.error = null;
    this.reason = null;
  }

  public int getStatus() {
    return status;
  }

  public String getError() {
    return error;
  }

  public String getReason() {
    return reason;
  }
}
