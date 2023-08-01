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

package org.apache.streampipes.extensions.api.monitoring;

import java.util.ArrayList;
import java.util.List;

public class FixedSizeList<T> {
  private final int maxSize;
  private final List<T> list;

  public FixedSizeList(int maxSize) {
    if (maxSize <= 0) {
      throw new IllegalArgumentException("Max size must be greater than zero.");
    }
    this.maxSize = maxSize;
    this.list = new ArrayList<>();
  }

  public void add(T element) {
    list.add(0, element);
    if (list.size() > maxSize) {
      list.remove(list.size() - 1);
    }
  }

  public T get(int index) {
    return list.get(index);
  }

  public int size() {
    return list.size();
  }

  public List<T> getAllItems() {
    return this.list;
  }

  public void clear() {
    this.list.clear();
  }
}




