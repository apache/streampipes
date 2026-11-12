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

package org.apache.streampipes.units;

/**
 * Namespaces of unit and quantity kind identifiers.
 */
public final class UnitNamespace {

  /**
   * StreamPipes unit identifiers, followed by a unit code (e.g. {@code DEG_C}).
   */
  public static final String STREAMPIPES_UNIT = "https://streampipes.apache.org/vocabulary/unit/";

  /**
   * StreamPipes quantity kind identifiers.
   */
  public static final String STREAMPIPES_QUANTITY_KIND = "https://streampipes.apache.org/vocabulary/quantitykind/";

  /**
   * QUDT 1.1 unit identifiers. Stored in existing event schemas and adapters.
   */
  public static final String QUDT1_UNIT = "http://qudt.org/vocab/unit#";

  /**
   * QUDT 1.1 unit classes, used as quantity kind identifiers.
   */
  public static final String QUDT1_QUANTITY_KIND = "http://qudt.org/schema/qudt#";

  /**
   * QUDT 2.x unit identifiers. Accepted as aliases.
   */
  public static final String QUDT2_UNIT = "http://qudt.org/vocab/unit/";

  private UnitNamespace() {
  }
}
