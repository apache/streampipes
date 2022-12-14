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

public class Tuple3<V, W, X> extends Tuple2<V, W> {

  public final X x;

  public Tuple3(V v, W w, X x) {
    super(v, w);
    this.x = x;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Tuple3<?, ?, ?> tuple = (Tuple3<?, ?, ?>) o;
    if (!k.equals(tuple.k)) {
      return false;
    }
    if (!v.equals(tuple.v)) {
      return false;
    }
    return x.equals(tuple.x);
  }

  @Override
  public int hashCode() {
    int result = k != null ? k.hashCode() : 0;
    result = 31 * result + (v != null ? v.hashCode() : 0);
    result = 31 * result + (x != null ? x.hashCode() : 0);
    return result;
  }
}
