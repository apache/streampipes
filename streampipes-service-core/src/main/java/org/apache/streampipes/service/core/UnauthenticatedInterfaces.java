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
package org.apache.streampipes.service.core;

import java.util.Arrays;
import java.util.Collection;

public class UnauthenticatedInterfaces {

  public static Collection<String> get() {
    return Arrays.asList(
        "/api/svchealth/*",
        "/api/v2/setup/configured",
        "/api/v2/auth/login",
        "/api/v2/auth/register",
        "/api/v2/auth/settings",
        "/api/v2/auth/restore/*",
        "/api/v2/asset-dashboards/images/*",
        "/api/v2/restore-password/*",
        "/api/v2/activate-account/*",
        "/api/v2/pe/*/assets/icon",
        "/api/v2/pe/*/assets/icon.png",
        "/api/v2/connect/master/description/*/assets/icon",
        "/api/openapi.json",
        "/api/auth/**",
        "/oauth2/**",
        "/api/all",
        "/error",
        "/",
        "/streampipes-backend/",
        "/streampipes-backend/index.html"
    );
  }
}
