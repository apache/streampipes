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
import org.lightcouch.NoDocumentException;

import java.util.Optional;

public class FindCommand<T> extends DbCommand<Optional<T>, T> {

  private String id;

  public FindCommand(CouchDbClient couchDbClient, String id, Class<T> clazz) {
    super(couchDbClient, clazz);
    this.id = id;
  }

  @Override
  protected Optional<T> executeCommand() {
    try {
      T result = couchDbClient.find(clazz, id);
      return Optional.of(result);
    } catch (NoDocumentException e) {
      return Optional.empty();
    }
  }
}
