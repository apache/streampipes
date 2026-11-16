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
package org.apache.streampipes.sdk.extractor;

import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class StaticPropertyLookupTest {

  private static FreeTextStaticProperty text(String name) {
    var property = new FreeTextStaticProperty();
    property.setInternalName(name);
    return property;
  }

  private static StaticPropertyGroup group(String name, StaticProperty... members) {
    var group = new StaticPropertyGroup(name, name, name);
    group.setStaticProperties(List.of(members));
    return group;
  }

  private static StaticPropertyAlternatives alternatives(String name, StaticPropertyAlternative... options) {
    var alternatives = new StaticPropertyAlternatives();
    alternatives.setInternalName(name);
    alternatives.setAlternatives(List.of(options));
    return alternatives;
  }

  private static StaticPropertyAlternative alternative(String name, boolean selected, StaticProperty property) {
    var alternative = new StaticPropertyAlternative(name, name, name);
    alternative.setSelected(selected);
    alternative.setStaticProperty(property);
    return alternative;
  }

  private static StaticProperty lookup(String name, StaticProperty... properties) {
    return StaticPropertyExtractor.from(List.of(properties), List.of()).getStaticPropertyByName(name);
  }

  @Test
  public void aPropertyAfterATopLevelGroupIsFound() {
    var after = text("after");
    assertSame(after, lookup("after", group("group", text("inside")), after));
  }

  @Test
  public void aPropertyInsideAGroupIsFound() {
    var inside = text("inside");
    assertSame(inside, lookup("inside", group("group", inside), text("after")));
  }

  @Test
  public void aPropertyAfterSeveralGroupsIsFound() {
    var last = text("last");
    assertSame(last, lookup("last", group("first", text("a")), group("second", text("b")), last));
  }

  @Test
  public void anUnknownNameIsNull() {
    assertNull(lookup("unknown", group("group", text("inside")), text("after")));
  }

  @Test
  public void alternativesWithoutASelectionAreSkippedNotFailed() {
    var after = text("after");
    assertSame(after, lookup("after",
        alternatives("choice", alternative("one", false, text("in-one")), alternative("two", false, text("in-two"))),
        after));
  }

  @Test
  public void thePropertyOfTheSelectedAlternativeIsFound() {
    var inTwo = text("in-two");
    assertSame(inTwo, lookup("in-two",
        alternatives("choice", alternative("one", false, text("in-one")), alternative("two", true, inTwo))));
  }
}
