/*
Copyright 2018 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.model.runtime;

public class RuntimeOptions {

  private String label;
  private String additionalPayload;

  public RuntimeOptions(String label, String additionalPayload) {
    this.label = label;
    this.additionalPayload = additionalPayload;
  }

  public RuntimeOptions() {

  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getAdditionalPayload() {
    return additionalPayload;
  }

  public void setAdditionalPayload(String additionalPayload) {
    this.additionalPayload = additionalPayload;
  }
}
