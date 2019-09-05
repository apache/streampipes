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
package org.streampipes.config.backend;


import org.streampipes.vocabulary.MessageFormat;

public enum SpDataFormat {

  CBOR("Cbor", MessageFormat.Cbor),
  JSON("JSON", MessageFormat.Json),
  FST("Fast-Serializer", MessageFormat.Fst),
  SMILE("Smile", MessageFormat.Smile);

  private String name;
  private String messageFormat;

  SpDataFormat(String name, String messageFormat) {
    this.name = name;
    this.messageFormat = messageFormat;
  }

  public String getName() {
    return name;
  }

  public String getMessageFormat() {
    return messageFormat;
  }
}
