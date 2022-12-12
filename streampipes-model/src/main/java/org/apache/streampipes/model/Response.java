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

package org.apache.streampipes.model;

public class Response {

  private Boolean success;
  private String elementId;
  private String optionalMessage;

  public Response() {

  }

  public Response(String elementId, boolean success) {
    this(elementId, success, "");
  }

  public Response(String elementId, boolean success, String optionalMessage) {
    this.elementId = elementId;
    this.success = success;
    this.optionalMessage = optionalMessage;
  }

  public Boolean isSuccess() {
    return success;
  }

  public String getElementId() {
    return elementId;
  }

  public void setElementId(String elementId) {
    this.elementId = elementId;
  }

  public String getOptionalMessage() {
    return optionalMessage;
  }

  public void setOptionalMessage(String optionalMessage) {
    this.optionalMessage = optionalMessage;
  }

  public void setSuccess(Boolean success) {
    this.success = success;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((elementId == null) ? 0 : elementId.hashCode());
    result = prime * result + ((optionalMessage == null) ? 0 : optionalMessage.hashCode());
    result = prime * result + (success ? 1231 : 1237);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Response other = (Response) obj;
    if (elementId == null) {
      if (other.elementId != null) {
        return false;
      }
    } else if (!elementId.equals(other.elementId)) {
      return false;
    }
    if (optionalMessage == null) {
      if (other.optionalMessage != null) {
        return false;
      }
    } else if (!optionalMessage.equals(other.optionalMessage)) {
      return false;
    }
    return success == other.success;
  }

}
