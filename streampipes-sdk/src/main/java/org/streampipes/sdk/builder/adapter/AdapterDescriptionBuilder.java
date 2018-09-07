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
package org.streampipes.sdk.builder.adapter;

import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.sdk.builder.AbstractConfigurablePipelineElementBuilder;

public abstract class AdapterDescriptionBuilder<BU extends
        AdapterDescriptionBuilder<BU, T>, T extends AdapterDescription> extends
        AbstractConfigurablePipelineElementBuilder<BU, T> {

  protected AdapterDescriptionBuilder(String id, String label, String description,
                                   T adapterTypeInstance) {
    super(id, label, description, adapterTypeInstance);
    this.elementDescription.setAdapterId(id);
  }
}
