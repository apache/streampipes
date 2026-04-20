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

import org.apache.streampipes.commons.constants.GenericDocTypes;

public class AddFunctionStateViewTask extends AbstractAddGenericStorageViewTask {

  public static final String DESIGN_DOCUMENT = "_design/function-states";
  public static final String VIEW_NAME = "all-function-states";

  @Override
  public String getDesignDocument() {
    return DESIGN_DOCUMENT;
  }

  @Override
  public String getViewName() {
    return VIEW_NAME;
  }

  @Override
  public String getMapFunction() {
    return String.format("function(doc) { if(doc.appDocType === '%s') { emit(doc._id, doc); } }",
        GenericDocTypes.DOC_FUNCTION_STATE);
  }
}
