/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.sdk.builder;

import org.streampipes.model.base.NamedStreamPipesEntity;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.sdk.helpers.Label;
import org.streampipes.sdk.helpers.Locales;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractPipelineElementBuilder<BU extends AbstractPipelineElementBuilder<BU, T>, T extends NamedStreamPipesEntity> {

  protected T elementDescription;

  protected AbstractPipelineElementBuilder(String appId, String label, String description, T element) {
    this(appId, element);
    this.elementDescription.setName(label);
    this.elementDescription.setDescription(description);
  }

  protected AbstractPipelineElementBuilder(String appId, T element) {
    this.elementDescription = element;
    this.elementDescription.setElementId(appId);
    this.elementDescription.setAppId(appId);
  }

  public BU iconUrl(String iconUrl) {
    elementDescription.setIconUrl(iconUrl);
    return me();
  }

  @Deprecated
  /**
   * @deprecated: Use {@link #withAssets(String...)} instead
   */
  public BU providesAssets(String... assets) {
    return withAssets(assets);
  }

  public BU withAssets(String... assets) {
    this.elementDescription.setIncludesAssets(true);
    this.elementDescription.setIncludedAssets(Arrays.asList(assets));
    return me();
  }

  public BU withLocales(Locales... locales) {
    this.elementDescription.setIncludesLocales(true);
    this.elementDescription.setIncludedLocales(Stream
            .of(locales)
            .map(Locales::toFilename)
            .collect(Collectors.toList()));

    return me();
  }

  protected <SP extends StaticProperty> SP prepareStaticProperty(Label label, SP element) {
    element.setInternalName(label.getInternalId());
    element.setDescription(label.getDescription());
    element.setLabel(label.getLabel());

    return element;
  }

  protected abstract BU me();

  protected abstract void prepareBuild();

  public T build() {
    prepareBuild();
    return elementDescription;
  }
}
