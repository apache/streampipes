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

package org.apache.streampipes.model.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

public class ListUtils {

  public static boolean isEqualList(final Collection<?> list1, final Collection<?> list2) {
    if (list1 == list2) {
      return true;
    }
    if (list1 == null || list2 == null || list1.size() != list2.size()) {
      return false;
    }

    final Iterator<?> it1 = list1.iterator();
    final Iterator<?> it2 = list2.iterator();

    while (it1.hasNext() && it2.hasNext()) {
      final Object obj1 = it1.next();
      final Object obj2 = it2.next();

      if (!Objects.equals(obj1, obj2)) {
        return false;
      }
    }

    return !(it1.hasNext() || it2.hasNext());
  }
}
