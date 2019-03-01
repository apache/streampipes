/*
Copyright 2019 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.sdk.helpers;

public class Tuple3<A, B, C> extends Tuple2<A, B> {

  public final C c;

  public Tuple3(A a, B b, C c) {
    super(a, b);
    this.c = c;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Tuple3<?, ?, ?> tuple = (Tuple3<?, ?, ?>) o;
    if (!a.equals(tuple.a)) return false;
    if (!b.equals(tuple.b)) return false;
    return c.equals(tuple.c);
  }

  @Override
  public int hashCode() {
    int result = a != null ? a.hashCode() : 0;
    result = 31 * result + (b != null ? b.hashCode() : 0);
    result = 31 * result + (c != null ? c.hashCode() : 0);
    return result;
  }
}
