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
package org.apache.streampipes.storage.couchdb.impl;

import org.apache.streampipes.model.shared.api.Storable;

import java.util.List;
import java.util.function.Supplier;

import org.lightcouch.CouchDbClient;

public class DefaultViewCrudStorage<T extends Storable> extends DefaultCrudStorage<T> {

  protected final String viewName;

  public DefaultViewCrudStorage(Supplier<CouchDbClient> couchDbClientSupplier, Class<T> clazz, String viewName) {
    super(couchDbClientSupplier, clazz);
    this.viewName = viewName;
  }

  @Override
  public List<T> findAll() {
    return findAll(viewName, clazz);
  }

}
