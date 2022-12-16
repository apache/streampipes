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
package org.apache.streampipes.sdk.helpers;

public class Tuple2<K, V> {

  public final K k;
  public final V v;

  public Tuple2(K k, V v) {
    this.k = k;
    this.v = v;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Tuple2<?, ?> tuple = (Tuple2<?, ?>) o;
    if (!k.equals(tuple.k)) {
      return false;
    }
    return v.equals(tuple.v);
  }

  @Override
  public int hashCode() {
    int result = k.hashCode();
    result = 31 * result + v.hashCode();
    return result;
  }
}
