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

import org.apache.streampipes.commons.exceptions.UserNotFoundException;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.shared.exception.SpMessageException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/activate-account")
public class AccountActivationResource extends AbstractAuthGuardedRestResource {

  @GetMapping(
      path = "{recoveryCode}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> activateUserAccount(@PathVariable("recoveryCode") String recoveryCode) {
    try {
      getSpResourceManager().manageUsers().activateAccount(recoveryCode);
      return ok();
    } catch (UserNotFoundException e) {
      throw new SpMessageException(HttpStatus.BAD_REQUEST, e);
    }
  }
}
