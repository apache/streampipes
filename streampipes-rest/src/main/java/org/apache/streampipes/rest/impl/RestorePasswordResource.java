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

import org.apache.streampipes.model.client.user.UserRegistrationData;
import org.apache.streampipes.rest.core.base.impl.AbstractRestResource;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@RestController
@RequestMapping("/api/v2/restore-password")
public class RestorePasswordResource extends AbstractRestResource {

  @GetMapping(path = "{recoveryCode}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> checkTokenValidity(@PathVariable("recoveryCode") String recoveryCode) {
    try {
      getSpResourceManager().manageUsers().checkPasswordRecoveryCode(recoveryCode);
      return ok();
    } catch (IllegalArgumentException e) {
      return badRequest();
    }
  }

  @PostMapping(
      path = "{recoveryCode}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> changePassword(@PathVariable("recoveryCode") String recoveryCode,
                                             @RequestBody UserRegistrationData userRegistrationData
  ) {
    try {
      getSpResourceManager().manageUsers().changePassword(recoveryCode, userRegistrationData);
      return ok();
    } catch (IllegalArgumentException e) {
      return badRequest();
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      return fail();
    }
  }

}
