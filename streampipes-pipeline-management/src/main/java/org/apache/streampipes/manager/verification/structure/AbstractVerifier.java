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

package org.apache.streampipes.manager.verification.structure;

import org.apache.streampipes.manager.verification.messages.VerificationError;
import org.apache.streampipes.manager.verification.messages.VerificationResult;
import org.apache.streampipes.manager.verification.messages.VerificationWarning;
import org.apache.streampipes.model.message.NotificationType;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractVerifier implements Verifier {

  protected List<VerificationResult> validationResults;

  public AbstractVerifier() {
    this.validationResults = new ArrayList<>();
  }

  protected void addWarning(NotificationType notificationType) {
    validationResults.add(new VerificationWarning(notificationType));
  }

  protected void addError(NotificationType notificationType) {
    validationResults.add(new VerificationError(notificationType));
  }
}
