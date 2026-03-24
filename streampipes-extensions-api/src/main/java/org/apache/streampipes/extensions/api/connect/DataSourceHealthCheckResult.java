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

package org.apache.streampipes.extensions.api.connect;

public record DataSourceHealthCheckResult(
    boolean healthy,
    String message,
    String details
) {
  public static DataSourceHealthCheckResult healthy(String message) {
    return new DataSourceHealthCheckResult(true, message, null);
  }

  public static DataSourceHealthCheckResult unhealthy(String message) {
    return new DataSourceHealthCheckResult(false, message, null);
  }

  public static DataSourceHealthCheckResult unhealthy(String message, String details) {
    return new DataSourceHealthCheckResult(false, message, details);
  }

  public static DataSourceHealthCheckResult unhealthyWithException(String message, Throwable t) {
    return new DataSourceHealthCheckResult(false, message, getStackTrace(t));
  }

  private static String getStackTrace(Throwable t) {
    var sb = new StringBuilder();
    sb.append(t.toString()).append("\n");
    for (var element : t.getStackTrace()) {
      sb.append("\tat ").append(element).append("\n");
    }
    if (t.getCause() != null && t.getCause() != t) {
      sb.append("Caused by: ");
      sb.append(getStackTrace(t.getCause()));
    }
    return sb.toString();
  }
}
