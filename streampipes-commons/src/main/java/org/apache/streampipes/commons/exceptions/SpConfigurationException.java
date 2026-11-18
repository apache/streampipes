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

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public class SpConfigurationException extends Exception {

  /**
   * Creates a new Exception with the given message and null as the cause.
   *
   * @param message The exception message
   */
  public SpConfigurationException(String message) {
    super(message);
  }

  /**
   * Creates a new exception with a null message and the given cause.
   *
   * @param cause The exception that caused this exception
   */
  public SpConfigurationException(Throwable cause) {
    super(cause);
  }

  /**
   * Creates a new exception with the given message and cause
   *
   * @param message The exception message
   * @param cause   The exception that caused this exception
   */
  public SpConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * A copy of this exception that any JSON mapper can serialize: the same message and stack trace, and each cause
   * replaced by a plain {@link Exception} carrying the cause's class name and message.
   *
   * <p>An extension service sends this exception to the core when resolving runtime options fails. Serialized as it
   * is, a cause such as {@link java.security.cert.CertPathValidatorException} - in the chain of every TLS connection
   * to a server with an untrusted certificate - holds a {@code sun.security} certificate path that Jackson cannot
   * access on current JDKs. The whole response then fails and the UI shows only "An unknown error has occurred"
   * instead of the adapter's message.</p>
   *
   * @return a copy safe to serialize
   */
  public SpConfigurationException toSerializable() {
    var copy = new SpConfigurationException(getMessage(),
        plainCopy(getCause(), Collections.newSetFromMap(new IdentityHashMap<>())));
    copy.setStackTrace(getStackTrace());
    return copy;
  }

  private static Exception plainCopy(Throwable cause, Set<Throwable> seen) {
    if (cause == null || !seen.add(cause)) {
      return null;
    }
    var message = cause.getMessage() == null
        ? cause.getClass().getName()
        : cause.getClass().getName() + ": " + cause.getMessage();
    var copy = new Exception(message, plainCopy(cause.getCause(), seen));
    copy.setStackTrace(cause.getStackTrace());
    return copy;
  }
}
