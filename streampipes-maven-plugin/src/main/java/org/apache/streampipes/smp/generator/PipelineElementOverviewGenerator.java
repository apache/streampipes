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

package org.apache.streampipes.smp.generator;

import org.apache.streampipes.smp.constants.PeGridConst;
import org.apache.streampipes.smp.model.AssetModel;

import j2html.tags.ContainerTag;
import j2html.tags.DomContent;

import java.util.List;

import static j2html.TagCreator.div;

public class PipelineElementOverviewGenerator extends OutputGenerator {

  public PipelineElementOverviewGenerator(List<AssetModel> assetModels) {
    super(assetModels);
  }

  public String generate() {

    ContainerTag gridContent = div().withClass(PeGridConst.PE_GRID_CONTAINER);
    for (AssetModel am : assetModels) {
      gridContent.with(makeGrid(am));
    }
    String markdownContent = gridContent.render();
    return markdownContent;
    //return new MarkdownHeaderGenerator(assetModel, markdownContent).createHeaders();
  }

  private DomContent makeGrid(AssetModel assetModel) {
    return new PipelineElementGridGenerator(assetModel).makeGrid();
  }


}
