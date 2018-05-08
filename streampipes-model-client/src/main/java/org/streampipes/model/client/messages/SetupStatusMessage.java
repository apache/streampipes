/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.streampipes.model.client.messages;

import java.util.List;

public class SetupStatusMessage {

  private Integer currentInstallationStep;
  private Integer installationStepCount;

  private List<Message> statusMessages;

  private String nextTaskTitle;

  public SetupStatusMessage() {

  }

  public SetupStatusMessage(Integer currentInstallationStep, Integer installationStepCount, List<Message> statusMessages, String nextTaskTitle) {
    this.currentInstallationStep = currentInstallationStep;
    this.installationStepCount = installationStepCount;
    this.statusMessages = statusMessages;
    this.nextTaskTitle = nextTaskTitle;
  }

  public Integer getCurrentInstallationStep() {
    return currentInstallationStep;
  }

  public Integer getInstallationStepCount() {
    return installationStepCount;
  }

  public List<Message> getStatusMessages() {
    return statusMessages;
  }

  public void setCurrentInstallationStep(Integer currentInstallationStep) {
    this.currentInstallationStep = currentInstallationStep;
  }

  public void setInstallationStepCount(Integer installationStepCount) {
    this.installationStepCount = installationStepCount;
  }

  public void setStatusMessages(List<Message> statusMessages) {
    this.statusMessages = statusMessages;
  }

  public String getNextTaskTitle() {
    return nextTaskTitle;
  }

  public void setNextTaskTitle(String nextTaskTitle) {
    this.nextTaskTitle = nextTaskTitle;
  }
}
