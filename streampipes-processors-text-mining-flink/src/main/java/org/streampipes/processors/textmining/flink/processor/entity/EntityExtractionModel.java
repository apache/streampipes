package org.streampipes.processors.textmining.flink.processor.entity;

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

public enum EntityExtractionModel {

  PERSON("Person", "en-ner-location.bin"),
  ORGANIZATION("Organization", "en-ner-organization.bin"),
  LOCATION("Location", "en-ner-location.bin");

  private String modelName;
  private String filename;

  EntityExtractionModel(String modelName, String filename) {
    this.modelName = modelName;
    this.filename = filename;
  }

  public String getModelName() {
    return modelName;
  }

  public String getFilename() {
    return filename;
  }
}
