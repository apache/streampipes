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

package org.apache.streampipes.model.staticproperty;

import java.net.URI;
import java.util.Objects;

public class FreeTextStaticProperty extends StaticProperty {

  private static final long serialVersionUID = 1L;

  private String value;

  private URI requiredDatatype;

  private URI requiredDomainProperty;

  private String mapsTo;

  private boolean multiLine;

  private boolean htmlAllowed;

  private boolean htmlFontFormat;

  private boolean placeholdersSupported;

  private PropertyValueSpecification valueSpecification;

  public FreeTextStaticProperty() {
    super(StaticPropertyType.FreeTextStaticProperty);
  }

  public FreeTextStaticProperty(FreeTextStaticProperty other) {
    super(other);
    this.requiredDomainProperty = other.getRequiredDomainProperty();
    this.requiredDatatype = other.getRequiredDatatype();
    if (other.getValueSpecification() != null) {
      this.valueSpecification = new PropertyValueSpecification(other.getValueSpecification());
    }
    this.value = other.getValue();
    this.htmlAllowed = other.isHtmlAllowed();
    this.htmlFontFormat = other.isHtmlFontFormat();
    this.multiLine = other.isMultiLine();
    this.placeholdersSupported = other.isPlaceholdersSupported();
    this.mapsTo = other.getMapsTo();
  }

  public FreeTextStaticProperty(String internalName, String label, String description) {
    super(StaticPropertyType.FreeTextStaticProperty, internalName, label, description);
  }

  public FreeTextStaticProperty(String internalName, String label, String description, URI type) {
    super(StaticPropertyType.FreeTextStaticProperty, internalName, label, description);
    this.requiredDatatype = type;
  }

  public FreeTextStaticProperty(String internalName, String label, String description, URI type, String mapsTo) {
    super(StaticPropertyType.FreeTextStaticProperty, internalName, label, description);
    this.mapsTo = mapsTo;
  }

  public FreeTextStaticProperty(String internalName, String label, String description,
                                PropertyValueSpecification valueSpecification) {
    super(StaticPropertyType.FreeTextStaticProperty, internalName, label, description);
    this.valueSpecification = valueSpecification;
  }

  public static FreeTextStaticProperty of(String internalName, String value) {
    FreeTextStaticProperty result = new FreeTextStaticProperty(internalName, "", "");
    result.setValue(value);
    return result;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public URI getRequiredDomainProperty() {
    return requiredDomainProperty;
  }

  public void setRequiredDomainProperty(URI type) {
    this.requiredDomainProperty = type;
  }

  public PropertyValueSpecification getValueSpecification() {
    return valueSpecification;
  }

  public void setValueSpecification(PropertyValueSpecification valueSpecification) {
    this.valueSpecification = valueSpecification;
  }

  public URI getRequiredDatatype() {
    return requiredDatatype;
  }

  public void setRequiredDatatype(URI requiredDatatype) {
    this.requiredDatatype = requiredDatatype;
  }

  public boolean isMultiLine() {
    return multiLine;
  }

  public void setMultiLine(boolean multiLine) {
    this.multiLine = multiLine;
  }

  public boolean isHtmlAllowed() {
    return htmlAllowed;
  }

  public void setHtmlAllowed(boolean htmlAllowed) {
    this.htmlAllowed = htmlAllowed;
  }

  public boolean isHtmlFontFormat() {
    return htmlFontFormat;
  }

  public void setHtmlFontFormat(boolean htmlFontFormat) {
    this.htmlFontFormat = htmlFontFormat;
  }

  public boolean isPlaceholdersSupported() {
    return placeholdersSupported;
  }

  public void setPlaceholdersSupported(boolean placeholdersSupported) {
    this.placeholdersSupported = placeholdersSupported;
  }

  public String getMapsTo() {
    return mapsTo;
  }

  public void setMapsTo(String mapsTo) {
    this.mapsTo = mapsTo;
  }

  @Override
  public void accept(StaticPropertyVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof FreeTextStaticProperty that)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    if (multiLine != that.multiLine) {
      return false;
    }
    if (htmlAllowed != that.htmlAllowed) {
      return false;
    }
    if (htmlFontFormat != that.htmlFontFormat) {
      return false;
    }
    if (placeholdersSupported != that.placeholdersSupported) {
      return false;
    }
    if (!Objects.equals(value, that.value)) {
      return false;
    }
    if (!Objects.equals(requiredDatatype, that.requiredDatatype)) {
      return false;
    }
    if (!Objects.equals(requiredDomainProperty, that.requiredDomainProperty)) {
      return false;
    }
    if (!Objects.equals(mapsTo, that.mapsTo)) {
      return false;
    }
    return Objects.equals(valueSpecification, that.valueSpecification);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (value != null ? value.hashCode() : 0);
    result = 31 * result + (requiredDatatype != null ? requiredDatatype.hashCode() : 0);
    result = 31 * result + (requiredDomainProperty != null ? requiredDomainProperty.hashCode() : 0);
    result = 31 * result + (mapsTo != null ? mapsTo.hashCode() : 0);
    result = 31 * result + (multiLine ? 1 : 0);
    result = 31 * result + (htmlAllowed ? 1 : 0);
    result = 31 * result + (htmlFontFormat ? 1 : 0);
    result = 31 * result + (placeholdersSupported ? 1 : 0);
    result = 31 * result + (valueSpecification != null ? valueSpecification.hashCode() : 0);
    return result;
  }
}
