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
package org.apache.streampipes.rest.impl;

import org.apache.streampipes.model.client.user.User;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

public class AbstractAuthGuardedRestResource extends AbstractRestResource {

  @Context
  protected SecurityContext securityContext;

  protected boolean isAuthenticated() {
    return this.securityContext.getUserPrincipal() != null;
  }

  protected String getAuthenticatedUsername() {
    return this.securityContext.getUserPrincipal().getName();
  }

  protected User getAuthenticatedUser() {
    return getUserStorage().getUser(getAuthenticatedUsername());
  }
}
