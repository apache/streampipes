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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.streampipes.connect.shared.DatatypeUtils;
import org.apache.streampipes.vocabulary.XSD;

import java.util.Locale;

import org.junit.jupiter.api.Test;

public class DatatypeUtilsTest {

  /**
   * The following tests ensure that timestamps represented as strings are correctly parsed. Often they are first parsed
   * into floating point number before transformed back to long. The data type for those values should be Double and not
   * Float, because the transformation to Float might change the value
   */
  @Test
  public void convertValue_StringToStringValue() {
    var inputValue = "testString";
    var actualValue = DatatypeUtils.convertValue(inputValue, XSD.STRING.toString());

    assertEquals(inputValue, actualValue);
  }

  @Test
  public void convertValue_StringToDoubleValue() {
    var actualValue = DatatypeUtils.convertValue("1667904471000", XSD.DOUBLE.toString());

    assertEquals(1.667904471E12, actualValue);
  }

  @Test
  public void convertValue_StringToFloatValue() {
    var actualValue = DatatypeUtils.convertValue("123.45", XSD.FLOAT.toString());

    assertEquals(123.45f, actualValue);
  }

  @Test
  public void convertValue_StringToInteger() {
    var actualValue = DatatypeUtils.convertValue("1623871500", XSD.INTEGER.toString());

    assertEquals(1623871500, actualValue);
  }

  @Test
  public void convertValue_StringToIntegerValue() {
    var actualValue = DatatypeUtils.convertValue("123", XSD.INTEGER.toString());

    assertEquals(123, actualValue);
  }

  @Test
  public void convertValue_StringToLongValue() {
    var actualValue = DatatypeUtils.convertValue("1623871500000", XSD.LONG.toString());

    assertEquals(1623871500000L, actualValue);
  }

  @Test
  public void convertValue_StringToBooleanTrueValue() {
    var actualValue = DatatypeUtils.convertValue("true", XSD.BOOLEAN.toString());

    assertEquals(true, actualValue);
  }

  @Test
  public void convertValue_StringToBooleanFalseValue() {
    var actualValue = DatatypeUtils.convertValue("false", XSD.BOOLEAN.toString());

    assertEquals(false, actualValue);
  }

  @Test
  public void convertValue_FloatToIntegerValue_Rounding() {
    var actualValue = DatatypeUtils.convertValue(123.45f, XSD.INTEGER.toString());

    assertEquals(123, actualValue);
  }

  @Test
  public void convertValue_DoubleToLongValue_Rounding1() {
    var actualValue = DatatypeUtils.convertValue(1234567890.12345, XSD.LONG.toString());

    assertEquals(1234567890L, actualValue);
  }

  @Test
  public void convertValue_DoubleToLongValue() {
    var actualValue = DatatypeUtils.convertValue(1.667904471E12, XSD.LONG.toString());

    assertEquals(1667904471000L, actualValue);
  }

  @Test
  public void convertValue_DoubleToLongValue_Rounding() {
    var actualValue = DatatypeUtils.convertValue(1234567890.12345, XSD.LONG.toString());

    assertEquals(1234567890L, actualValue);
  }

  String booleanInputValue = "true";

  @Test
  public void getTypeClass_NoPrefereFloatingPointBoolean() {
    var result = DatatypeUtils.getTypeClass(booleanInputValue, false);
    assertEquals(Boolean.class, result);
  }

  @Test
  public void getTypeClass_WithPrefereFloatingPointBoolean() {
    var result = DatatypeUtils.getTypeClass(booleanInputValue, true);
    assertEquals(Boolean.class, result);
  }

  String integerInputValue = "1";

  @Test
  public void getTypeClass_NoPrefereFloatingPointInteger() {
    var result = DatatypeUtils.getTypeClass(integerInputValue, false);
    assertEquals(Integer.class, result);
  }

  @Test
  public void getTypeClass_WithPrefereFloatingPointInteger() {
    var result = DatatypeUtils.getTypeClass(integerInputValue, true);
    assertEquals(Float.class, result);
  }

  String floatInputValue = "1.0";

  @Test
  public void getTypeClass_NoPrefereFloatingPointFloat() {
    var result = DatatypeUtils.getTypeClass(floatInputValue, false);
    assertEquals(Float.class, result);
  }

  @Test
  public void getTypeClass_WithPrefereFloatingPointFloat() {
    var result = DatatypeUtils.getTypeClass(floatInputValue, true);
    assertEquals(Float.class, result);
  }

  String doubleInputValue = String.format(Locale.US, "%.2f", Double.MAX_VALUE);

  @Test
  public void getTypeClass_NoPrefereFloatingPointDouble() {
    var result = DatatypeUtils.getTypeClass(doubleInputValue, false);
    assertEquals(Double.class, result);
  }

  @Test
  public void getTypeClass_WithPrefereFloatingPointDouble() {
    var result = DatatypeUtils.getTypeClass(doubleInputValue, true);
    assertEquals(Double.class, result);
  }

  String longInputValue = "1667904471000";

  @Test
  public void getTypeClass_NoPrefereFloatingPointLong() {
    var result = DatatypeUtils.getTypeClass(longInputValue, false);
    assertEquals(Long.class, result);
  }

  @Test
  public void getTypeClass_WithPrefereFloatingPointLong() {
    var result = DatatypeUtils.getTypeClass(longInputValue, true);
    assertEquals(Double.class, result);
  }

  String stringInputValue = "one";

  @Test
  public void getTypeClass_NoPrefereFloatingPointString() {
    var result = DatatypeUtils.getTypeClass(stringInputValue, false);

    assertEquals(String.class, result);
  }

  @Test
  public void getTypeClass_WithPrefereFloatingPointString() {
    var result = DatatypeUtils.getTypeClass(stringInputValue, true);

    assertEquals(String.class, result);
  }

}
