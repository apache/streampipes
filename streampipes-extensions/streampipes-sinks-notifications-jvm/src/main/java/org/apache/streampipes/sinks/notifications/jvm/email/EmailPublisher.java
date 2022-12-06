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

package org.apache.streampipes.sinks.notifications.jvm.email;

import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.mail.SpEmail;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.pe.shared.PlaceholderExtractor;
import org.apache.streampipes.wrapper.context.EventSinkRuntimeContext;
import org.apache.streampipes.wrapper.runtime.EventSink;

import java.time.Instant;
import java.util.Collections;

public class EmailPublisher implements EventSink<EmailParameters> {

  private SpEmail preparedEmail;
  private long silentPeriodInSeconds;
  private long lastMailEpochSecond = -1;

  private String originalContent;

  private StreamPipesClient client;

  @Override
  public void onInvocation(EmailParameters parameters, EventSinkRuntimeContext runtimeContext) {
    this.preparedEmail = new SpEmail();
    this.preparedEmail.setRecipients(Collections.singletonList(parameters.getToEmailAddress()));
    this.preparedEmail.setSubject(parameters.getSubject());

    this.silentPeriodInSeconds = parameters.getSilentPeriod() * 60;
    this.client = runtimeContext.getStreamPipesClient();
    this.originalContent = parameters.getContent();
  }

  @Override
  public void onEvent(Event inputEvent) {
    if (shouldSendMail()) {
      String message = PlaceholderExtractor.replacePlaceholders(inputEvent, this.originalContent);
      this.preparedEmail.setMessage(message);
      this.client.deliverEmail(this.preparedEmail);
      this.lastMailEpochSecond = Instant.now().getEpochSecond();
    }
  }

  private boolean shouldSendMail() {
    if (this.lastMailEpochSecond == -1) {
      return true;
    } else {
      return Instant.now().getEpochSecond() >= (this.lastMailEpochSecond + this.silentPeriodInSeconds);
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {
  }
}
