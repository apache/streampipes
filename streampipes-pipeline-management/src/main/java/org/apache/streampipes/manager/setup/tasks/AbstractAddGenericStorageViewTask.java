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

package org.apache.streampipes.manager.setup.tasks;

import org.apache.streampipes.storage.couchdb.utils.Utils;

import org.lightcouch.DesignDocument;

import java.util.HashMap;
import java.util.Map;

import static org.apache.streampipes.manager.setup.design.DesignDocumentUtils.prepareDocument;

public abstract class AbstractAddGenericStorageViewTask implements InstallationTask {

  public abstract String getDesignDocument();

  public abstract String getViewName();

  public abstract String getMapFunction();

  public void execute() {
    DesignDocument doc = prepareDocument(getDesignDocument());
    Map<String, DesignDocument.MapReduce> views = new HashMap<>();

    DesignDocument.MapReduce byNameFn = new DesignDocument.MapReduce();
    byNameFn.setMap(getMapFunction());

    views.put(getViewName(), byNameFn);
    doc.setViews(views);
    Utils.getCouchDbClient("genericstorage", true).design().synchronizeWithDb(doc);
  }
}
