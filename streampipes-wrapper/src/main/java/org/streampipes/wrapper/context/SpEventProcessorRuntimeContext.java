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
package org.streampipes.wrapper.context;

import org.streampipes.model.runtime.SchemaInfo;
import org.streampipes.model.runtime.SourceInfo;

import java.util.List;

public class SpEventProcessorRuntimeContext extends SpRuntimeContext implements EventProcessorRuntimeContext {

  private SchemaInfo outputSchemaInfo;
  private SourceInfo outputSourceInfo;

  public SpEventProcessorRuntimeContext(List<SourceInfo> inputSourceInfo, List<SchemaInfo>
          inputSchemaInfo, SourceInfo outputSourceInfo, SchemaInfo outputSchemaInfo) {
    super(inputSourceInfo, inputSchemaInfo);
    this.outputSchemaInfo = outputSchemaInfo;
    this.outputSourceInfo = outputSourceInfo;
  }

  @Override
  public SchemaInfo getOutputSchemaInfo() {
    return outputSchemaInfo;
  }

  @Override
  public SourceInfo getOutputSourceInfo() {
    return outputSourceInfo;
  }
}
