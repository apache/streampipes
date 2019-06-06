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
package org.streampipes.dataformat.fst;

import org.nustaq.serialization.FSTConfiguration;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.dataformat.SpDataFormatDefinition;

import java.util.Map;

public class FstDataFormatDefinition implements SpDataFormatDefinition {

  private static FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();

  public FstDataFormatDefinition() {

  }

  @Override
  public Map<String, Object> toMap(byte[] event) throws SpRuntimeException {
    return (Map<String, Object>) conf.asObject(event);
  }

  @Override
  public byte[] fromMap(Map<String, Object> event) throws SpRuntimeException {
    return conf.asByteArray(event);
  }
}
