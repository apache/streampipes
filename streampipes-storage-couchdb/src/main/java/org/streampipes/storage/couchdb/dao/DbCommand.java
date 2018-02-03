/*
Copyright 2018 FZI Forschungszentrum Informatik

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
package org.streampipes.storage.couchdb.dao;

import org.lightcouch.CouchDbClient;

import java.util.function.Supplier;

public abstract class DbCommand<T, S> {

  private Supplier<CouchDbClient> couchDbClientSupplier;
  protected Class<S> clazz;

  public DbCommand(Supplier<CouchDbClient> couchDbClientSupplier, Class<S> clazz) {
   this.couchDbClientSupplier = couchDbClientSupplier;
    this.clazz = clazz;
  }

  protected abstract T executeCommand(CouchDbClient couchDbClient);


  public T execute() {
    CouchDbClient couchDbClient = couchDbClientSupplier.get();
    T result = executeCommand(couchDbClient);
    couchDbClient.shutdown();

    return result;
  }
}
