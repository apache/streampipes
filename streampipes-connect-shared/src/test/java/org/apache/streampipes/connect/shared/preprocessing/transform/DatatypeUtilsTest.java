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

package org.apache.streampipes.connect.shared.preprocessing.transform;

import org.apache.streampipes.connect.shared.DatatypeUtils;
import org.apache.streampipes.vocabulary.XSD;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class DatatypeUtilsTest {

  @Test
  /**
   * This test ensures that timestamps represented as strings are correctly parsed.
   * Often they are first parsed into floating point number before transformed back to long.
   * The data type for those values should be Double and not Float, because the transformation to Float might change
   * the value
   */
  public void convertTimestampValue() {
    var inputValue = "1667904471000";

    var floatValue = DatatypeUtils.convertValue(inputValue, XSD.DOUBLE.toString());
    var longValue = DatatypeUtils.convertValue(floatValue, XSD.LONG.toString());

    assertEquals(Long.parseLong(inputValue), longValue);
  }

  String booleanInputValue = "true";
  @Test
  public void getTypeClassNoPrefereFloatingPointBoolean() {
    var result = DatatypeUtils.getTypeClass(booleanInputValue, false);
    assertEquals(Boolean.class, result);
  }

  @Test
  public void getTypeClassWithPrefereFloatingPointBoolean() {
    var result = DatatypeUtils.getTypeClass(booleanInputValue, true);
    assertEquals(Boolean.class, result);
  }

  String integerInputValue = "1";
  @Test
  public void getTypeClassNoPrefereFloatingPointInteger() {
    var result = DatatypeUtils.getTypeClass(integerInputValue, false);
    assertEquals(Integer.class, result);
  }

  @Test
  public void getTypeClassWithPrefereFloatingPointInteger() {
    var result = DatatypeUtils.getTypeClass(integerInputValue, true);
    assertEquals(Float.class, result);
  }

  String floatInputValue = "1.0";
  @Test
  public void getTypeClassNoPrefereFloatingPointFloat() {
    var result = DatatypeUtils.getTypeClass(floatInputValue, false);
    assertEquals(Float.class, result);
  }

  @Test
  public void getTypeClassWithPrefereFloatingPointFloat() {
    var result = DatatypeUtils.getTypeClass(floatInputValue, true);
    assertEquals(Float.class, result);
  }


  String doubleInputValue = String.format(Locale.US, "%.2f", Double.MAX_VALUE);
  @Test
  public void getTypeClassNoPrefereFloatingPointDouble() {
    var result = DatatypeUtils.getTypeClass(doubleInputValue, false);
    assertEquals(Double.class, result);
  }

  @Test
  public void getTypeClassWithPrefereFloatingPointDouble() {
    var result = DatatypeUtils.getTypeClass(doubleInputValue, true);
    assertEquals(Double.class, result);
  }


  String longInputValue = "1667904471000";
  @Test
  public void getTypeClassNoPrefereFloatingPointLong() {
    var result = DatatypeUtils.getTypeClass(longInputValue, false);
    assertEquals(Long.class, result);
  }

  @Test
  public void getTypeClassWithPrefereFloatingPointLong() {
    var result = DatatypeUtils.getTypeClass(longInputValue, true);
    assertEquals(Double.class, result);
  }

  String stringInputValue = "one";
  @Test
  public void getTypeClassNoPrefereFloatingPointString() {
    var result = DatatypeUtils.getTypeClass(stringInputValue, false);

    assertEquals(String.class, result);
  }

  @Test
  public void getTypeClassWithPrefereFloatingPointString() {
    var result = DatatypeUtils.getTypeClass(stringInputValue, true);

    assertEquals(String.class, result);
  }


}
