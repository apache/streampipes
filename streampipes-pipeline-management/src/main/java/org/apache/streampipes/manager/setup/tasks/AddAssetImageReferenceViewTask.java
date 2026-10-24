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

public class AddAssetImageReferenceViewTask extends AbstractAddGenericStorageViewTask {

  public static final String DESIGN_DOCUMENT = "_design/asset-images";
  public static final String VIEW_NAME = "by-image";

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
    return """
        function(doc) {
          if (doc.appDocType !== '%s') return;
          function visit(asset) {
            if (!asset) return;
            (asset.assetImages || []).forEach(function(image) {
              if (!image) return;
              var id = image._id || image.fileId || image.elementId;
              if (typeof id === 'string' && id.length > 0) emit(id, null);
            });
            (asset.assets || []).forEach(visit);
          }
          visit(doc);
        }
        """.formatted(GenericDocTypes.DOC_ASSET_MANAGEMENT);
  }
}
