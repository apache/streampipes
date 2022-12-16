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

package org.apache.streampipes.model.message;

public enum NotificationType {
  STORAGE_SUCCESS("Success", "Entity successfully stored"),
  URIOFFLINE("URI not available", "Content could not be retrieved."),
  NOSEPAFORMAT("Wrong Format", "Entity could not be recognized."),
  UNKNOWN_ERROR("Unknown Error", "An unforeseen error has occurred."),
  STORAGE_ERROR("Storage Error", "Entity could not be stored."),
  PIPELINE_STORAGE_SUCCESS("Success", "Pipeline stored successfully"),
  PIPELINE_START_SUCCESS("Started", "Pipeline successfully started"),
  PIPELINE_STOP_SUCCESS("Stopped", "Pipeline stopped successfully"),
  NO_VALID_CONNECTION("Not a valid connection", "Expected input event type does not match computed output type"),
  NO_SEPA_FOUND("No element found", "Could not find any element that matches the output of this element."),
  NO_MATCHING_FORMAT_CONNECTION("Not a valid connection", "No supported input format matches produced output format"),
  NO_MATCHING_PROTOCOL_CONNECTION("Not a valid connection",
      "No supported input protocol matches provided output protocol"),
  REMOTE_SERVER_NOT_ACCESSIBLE("Can't connect to remote server", "Please contact the admin of the system"),
  NO_MATCHING_SCHEME("The JSON from the server is not valid",
      "The keys in the element description don't map the keys in the JSON response"),

  LOGIN_FAILED("Login failed", "Please re-enter your password"),
  LOGIN_SUCCESS("Login success", ""),
  REGISTRATION_FAILED("Registration failed", "Please re-enter your password"),
  REGISTRATION_SUCCESS("Registered user successfully", ""),
  UNAUTHORIZED("Not authorized", ""),

  ALREADY_LOGGED_IN("User already logged in", ""),
  NOT_LOGGED_IN("User not logged in", ""),
  LOGOUT_SUCCESS("Successfully logged out", ""),

  OPERATION_SUCCESS("Success", ""),

  VIRTUAL_SENSOR_STORAGE_SUCCESS("Success", "Pipeline block stored successfully"),

  PARSE_ERROR("Parse Exception", "Could not parse element description"),
  WARNING_NO_ICON("Icon missing", ""),
  WARNING_NO_NAME("Name missing", ""),
  WARNING_NO_LABEL("Description missing", ""),

  NOT_REMEMBERED("User not remembered", ""),
  REMEMBERED("User remembered", ""),

  NOT_REMOVED("Could not remove element", ""),
  REMOVED_ACTION("Action removed", ""),
  REMOVED_SOURCE("Source removed", ""),
  REMOVED_SEPA("Sepa removed", ""),

  ADDED_CONFIGURATION("Configuration added", ""),
  INSTALLATION_SUCCESSFUL("Installation successful", ""),

  PROPERTY_FILE_WRITTEN("Writing properties file...", ""),
  ADMIN_USER_CREATED("Creating admin user...", ""),

  PASSWORD_RECOVERY_LINK_SENT("Check your mail inbox for instructions how to reset your password", "");

  private final String title;
  private final String description;

  NotificationType(String title, String description) {
    this.title = title;
    this.description = description;
  }

  public String title() {
    return title;
  }

  public String description() {
    return description;
  }

  public Notification uiNotification() {
    return new Notification(title, description);
  }
}
