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

public class MatchingStaticProperty extends StaticProperty {

  private static final long serialVersionUID = -6033310221105761979L;

  private URI matchLeft;

  private URI matchRight;

  public MatchingStaticProperty() {
    super(StaticPropertyType.MatchingStaticProperty);
  }

  public MatchingStaticProperty(MatchingStaticProperty other) {
    super(other);
    this.matchLeft = other.getMatchLeft();
    this.matchRight = other.getMatchRight();
  }

  public MatchingStaticProperty(String internalName, String label, String description) {
    super(StaticPropertyType.MatchingStaticProperty, internalName, label, description);
  }

  public MatchingStaticProperty(String internalName, String label, String description, URI matchLeft, URI matchRight) {
    super(StaticPropertyType.MatchingStaticProperty, internalName, label, description);
    this.matchLeft = matchLeft;
    this.matchRight = matchRight;
  }

  public URI getMatchLeft() {
    return matchLeft;
  }

  public void setMatchLeft(URI matchLeft) {
    this.matchLeft = matchLeft;
  }

  public URI getMatchRight() {
    return matchRight;
  }

  public void setMatchRight(URI matchRight) {
    this.matchRight = matchRight;
  }

  @Override
  public void accept(StaticPropertyVisitor visitor) {
    visitor.visit(this);
  }
}
