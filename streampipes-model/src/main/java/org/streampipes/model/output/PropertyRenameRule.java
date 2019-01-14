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
package org.streampipes.model.output;

import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.UnnamedStreamPipesEntity;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;

@RdfsClass(StreamPipes.RENAME_RULE)
@Entity
public class PropertyRenameRule extends UnnamedStreamPipesEntity {

  private String runtimeId;

  private String newRuntimeName;

  public PropertyRenameRule(String runtimeId, String newRuntimeName) {
    super();
    this.runtimeId = runtimeId;
    this.newRuntimeName = newRuntimeName;
  }

  public PropertyRenameRule(PropertyRenameRule other) {
    this.runtimeId = other.getRuntimeId();
    this.newRuntimeName = other.getNewRuntimeName();
  }

  public String getRuntimeId() {
    return runtimeId;
  }

  public void setRuntimeId(String runtimeId) {
    this.runtimeId = runtimeId;
  }

  public String getNewRuntimeName() {
    return newRuntimeName;
  }

  public void setNewRuntimeName(String newRuntimeName) {
    this.newRuntimeName = newRuntimeName;
  }
}
