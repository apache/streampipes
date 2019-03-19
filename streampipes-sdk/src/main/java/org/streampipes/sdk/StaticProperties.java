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

package org.streampipes.sdk;

import org.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.staticproperty.PropertyValueSpecification;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.model.staticproperty.SupportedProperty;
import org.streampipes.sdk.helpers.Label;
import org.streampipes.sdk.utils.Datatypes;

import java.net.URI;

public class StaticProperties {

  public static FreeTextStaticProperty stringFreeTextProperty(Label label) {
    return freeTextProperty(label, Datatypes.String);
  }

  public static FreeTextStaticProperty integerFreeTextProperty(Label label) {
    return freeTextProperty(label, Datatypes.Integer);
  }

  public static FreeTextStaticProperty doubleFreeTextProperty(Label label) {
    return freeTextProperty(label, Datatypes.Double);
  }

  public static FreeTextStaticProperty freeTextProperty(Label label, Datatypes datatype) {
    FreeTextStaticProperty fsp = new FreeTextStaticProperty(label.getInternalId(), label.getLabel(),
            label.getDescription());
    fsp.setRequiredDatatype(URI.create(datatype.toString()));
    return fsp;
  }

  public static StaticProperty integerFreeTextProperty(Label label,
                                                       PropertyValueSpecification propertyValueSpecification) {
    FreeTextStaticProperty fsp = integerFreeTextProperty(label);
    fsp.setValueSpecification(propertyValueSpecification);
    return fsp;
  }

  public static SupportedProperty supportedDomainProperty(String rdfPropertyUri, boolean required) {
    return new SupportedProperty(rdfPropertyUri, required);
  }
}
