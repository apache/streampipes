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

package org.apache.streampipes.sdk.builder;

import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.util.ElementIdGenerator;
import org.apache.streampipes.sdk.helpers.Label;
import org.apache.streampipes.sdk.helpers.Locales;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractPipelineElementBuilder<X extends AbstractPipelineElementBuilder<X, T>,
    T extends NamedStreamPipesEntity> {

  protected T elementDescription;

  protected AbstractPipelineElementBuilder(String appId, String label, String description, T element) {
    this(appId, element);
    this.elementDescription.setName(label);
    this.elementDescription.setDescription(description);
  }

  protected AbstractPipelineElementBuilder(String appId, T element) {
    this.elementDescription = element;
    this.elementDescription.setElementId(ElementIdGenerator.makeElementIdFromAppId(appId));
    this.elementDescription.setAppId(appId);
  }

  @Deprecated
  /**
   * @deprecated: Use {@link #withAssets(String...)} instead
   */
  public X iconUrl(String iconUrl) {
    elementDescription.setIconUrl(iconUrl);
    return me();
  }

  @Deprecated
  /**
   * @deprecated: Use {@link #withAssets(String...)} instead
   */
  public X providesAssets(String... assets) {
    return withAssets(assets);
  }

  public X withAssets(String... assets) {
    this.elementDescription.setIncludesAssets(true);
    this.elementDescription.setIncludedAssets(Arrays.asList(assets));
    return me();
  }

  public X withLocales(Locales... locales) {
    this.elementDescription.setIncludesLocales(true);
    this.elementDescription.setIncludedLocales(Stream
        .of(locales)
        .map(Locales::toFilename)
        .collect(Collectors.toList()));

    return me();
  }

  protected <K extends StaticProperty> K prepareStaticProperty(Label label, K element) {
    element.setInternalName(label.getInternalId());
    element.setDescription(label.getDescription());
    element.setLabel(label.getLabel());

    return element;
  }

  protected abstract X me();

  protected abstract void prepareBuild();

  public T build() {
    prepareBuild();
    return elementDescription;
  }
}
