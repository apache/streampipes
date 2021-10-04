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
package org.apache.streampipes.model.client.user;

public enum Privilege {
  // Pipelines
  PRIVILEGE_CREATE_PIPELINE,
  PRIVILEGE_READ_PIPELINE,
  PRIVILEGE_UPDATE_PIPELINE,
  PRIVILEGE_DELETE_PIPELINE,

  // Adapters
  PRIVILEGE_CREATE_ADAPTER,
  PRIVILEGE_READ_ADAPTER,
  PRIVILEGE_UPDATE_ADAPTER,
  PRIVILEGE_DELETE_ADAPTER,

  // Pipeline Elements
  PRIVILEGE_CREATE_PIPELINE_ELEMENT,
  PRIVILEGE_READ_PIPELINE_ELEMENT,
  PRIVILEGE_UPDATE_PIPELINE_ELEMENT,
  PRIVILEGE_DELETE_PIPELINE_ELEMENT,

  // Dashboard
  PRIVILEGE_CREATE_DASHBOARD,
  PRIVILEGE_READ_DASHBOARD,
  PRIVILEGE_UPDATE_DASHBOARD,
  PRIVILEGE_DELETE_DASHBOARD,

  // Dashboard widget
  PRIVILEGE_CREATE_DASHBOARD_WIDGET,
  PRIVILEGE_READ_DASHBOARD_WIDGET,
  PRIVILEGE_UPDATE_DASHBOARD_WIDGET,
  PRIVILEGE_DELETE_DASHBOARD_WIDGET,

  // Data Explorer view
  PRIVILEGE_CREATE_DATA_EXPLORER_VIEW,
  PRIVILEGE_READ_DATA_EXPLORER_VIEW,
  PRIVILEGE_UPDATE_DATA_EXPLORER_VIEW,
  PRIVILEGE_DELETE_DATA_EXPLORER_VIEW,

  // Data Explorer widget
  PRIVILEGE_CREATE_DATA_EXPLORER_WIDGET,
  PRIVILEGE_READ_DATA_EXPLORER_WIDGET,
  PRIVILEGE_UPDATE_DATA_EXPLORER_WIDGET,
  PRIVILEGE_DELETE_DATA_EXPLORER_WIDGET,

  // Apps
  PRIVILEGE_READ_APPS,

  // NOTIFICATIONS
  PRIVILEGE_READ_NOTIFICATIONS,

  // FILES
  PRIVILEGE_READ_FILES,
  PRIVILEGE_CREATE_FILES,
  PRIVILEGE_UPDATE_FILES,
  PRIVILEGE_DELETE_FILES,

  // Admin
  PRIVILEGE_ADMIN
}
