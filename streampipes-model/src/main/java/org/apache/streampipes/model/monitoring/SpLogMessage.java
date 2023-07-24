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

package org.apache.streampipes.model.monitoring;

import org.apache.streampipes.model.shared.annotation.TsModel;

import org.apache.commons.lang3.exception.ExceptionUtils;

@TsModel
public class SpLogMessage {

  private SpLogLevel level;
  private String title;
  private String detail;

  private String cause;
  private String fullStackTrace;

  public static SpLogMessage from(Exception exception) {
    return from(exception, "");
  }

  public static SpLogMessage from(Exception exception,
                                  String detail) {
    String cause = exception.getCause() != null ? exception.getCause().getMessage() : exception.getMessage();
    return new SpLogMessage(
        SpLogLevel.ERROR,
        exception.getMessage(),
        detail,
        ExceptionUtils.getStackTrace(exception),
        cause);
  }

  public static SpLogMessage info(String title,
                                  String details) {
    return new SpLogMessage(
        SpLogLevel.INFO,
        title,
        details
    );
  }

  public static SpLogMessage warn(String title,
                                  String details) {
    return new SpLogMessage(
        SpLogLevel.WARN,
        title,
        details
    );
  }

  public SpLogMessage() {

  }

  public SpLogMessage(SpLogMessage other) {
    this.level = other.getLevel();
    this.detail = other.getDetail();
    this.title = other.getTitle();
    this.cause = other.getCause();
    this.fullStackTrace = other.getFullStackTrace();
  }

  public SpLogMessage(SpLogLevel level,
                      String title,
                      String detail) {
    this.level = level;
    this.title = title;
    this.detail = detail;
  }

  public SpLogMessage(SpLogLevel level,
                      String title,
                      String detail,
                      String fullStackTrace,
                      String cause) {
    this.level = level;
    this.title = title;
    this.detail = detail;
    this.fullStackTrace = fullStackTrace;
    this.cause = cause;
  }

  public SpLogLevel getLevel() {
    return level;
  }

  public void setLevel(SpLogLevel level) {
    this.level = level;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDetail() {
    return detail;
  }

  public void setDetail(String detail) {
    this.detail = detail;
  }

  public String getFullStackTrace() {
    return fullStackTrace;
  }

  public void setFullStackTrace(String fullStackTrace) {
    this.fullStackTrace = fullStackTrace;
  }

  public String getCause() {
    return cause;
  }

  public void setCause(String cause) {
    this.cause = cause;
  }
}
