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

package org.apache.streampipes.model.client.matching;

import org.apache.streampipes.model.shared.annotation.TsModel;

import java.util.Objects;

@TsModel
public class MatchingResultMessage {

  private boolean matchingSuccessful;

  private String title;
  private String description;

  private String offerSubject;
  private String requirementSubject;

  private String reasonText;


  public MatchingResultMessage() {

  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public boolean isMatchingSuccessful() {
    return matchingSuccessful;
  }

  public void setMatchingSuccessful(boolean matchingSuccessful) {
    this.matchingSuccessful = matchingSuccessful;
  }

  public String getOfferSubject() {
    return offerSubject;
  }

  public void setOfferSubject(String offerSubject) {
    this.offerSubject = offerSubject;
  }

  public String getRequirementSubject() {
    return requirementSubject;
  }

  public void setRequirementSubject(String requirementSubject) {
    this.requirementSubject = requirementSubject;
  }

  public String getReasonText() {
    return reasonText;
  }

  public void setReasonText(String reasonText) {
    this.reasonText = reasonText;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String toString() {
    return title + " - " + description + "\n" + "(required: " + requirementSubject + ")";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MatchingResultMessage that = (MatchingResultMessage) o;
    return matchingSuccessful == that.matchingSuccessful && Objects.equals(title, that.title)
        && Objects.equals(description, that.description) && Objects.equals(offerSubject, that.offerSubject)
        && Objects.equals(requirementSubject, that.requirementSubject) && Objects.equals(reasonText, that.reasonText);
  }

  @Override
  public int hashCode() {
    return Objects.hash(matchingSuccessful, title, description, offerSubject, requirementSubject, reasonText);
  }
}
