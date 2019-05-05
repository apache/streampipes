/*
Copyright 2019 FZI Forschungszentrum Informatik

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
package org.streampipes.model;

public enum AdapterType {

  Generic("Generic Adapters", ""),
  Finance("Finance", ""),
  Environment("Environmental Data", ""),
  News("News", ""),
  SocialMedia("Social Media", ""),
  OpenData("Open Data", ""),
  Manufacturing("Production & Manufacturing", "");

  private String label;
  private String description;

  AdapterType(String label, String description) {
    this.label = label;
    this.description = description;
  }

  public String getLabel() {
    return label;
  }

  public String getDescription() {
    return description;
  }
}
