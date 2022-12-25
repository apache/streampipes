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
import org.apache.streampipes.smp.constants.PeType;
import org.apache.streampipes.smp.model.AssetModel;

import j2html.tags.DomContent;

import static j2html.TagCreator.a;
import static j2html.TagCreator.div;
import static j2html.TagCreator.i;
import static j2html.TagCreator.img;
import static j2html.TagCreator.text;

public class PipelineElementGridGenerator {

  private static final String IMG_PATH_PREFIX = "/docs/img/pipeline-elements/";
  private static final String IMG_PATH_APPENDIX = "/icon.png";

  private static final String DOCUMENTATION = "Documentation";
  private static final String GITHUB = "Code on Github";
  private static final String DOCKER_HUB = "Docker Container";

  private static final String STREAMPIPES_GITHUB_URL = "https://www.github"
      + ".com/apache/incubator-streampipes-extensions/tree/dev/";

  private static final String DOCKER_HUB_URL = "https://hub.docker.com/r/streampipes/";

  private AssetModel assetModel;

  public PipelineElementGridGenerator(AssetModel assetModel) {
    this.assetModel = assetModel;
  }

  public DomContent makeGrid() {
    return makeContainerItem();
  }

  private DomContent makeContainerItem() {
    return div(makeContainerItemHeader(),
        makeContainerItemBody(),
        makeContainerItemFooter()).withClasses(PeGridConst.PE_CONTAINER_ITEM,
        getContainerItemType());
  }

  private DomContent makeContainerItemFooter() {
    return div(makeFile(), makeDocker(), makeGithub()).withClass(PeGridConst.PE_CONTAINER_ITEM_FOOTER);
  }

  private DomContent makeGithub() {
    return div(makeGithubIcon(), makeBlank(), makeGithubLink());
  }

  private DomContent makeGithubLink() {
    return a(GITHUB).withHref(STREAMPIPES_GITHUB_URL + assetModel.getModuleName());
  }

  private DomContent makeGithubIcon() {
    return i().withClasses(PeGridConst.FAB, PeGridConst.FA_GITHUB);
  }

  private DomContent makeDocker() {
    return div(makeDockerIcon(), makeBlank(), makeDockerLink());
  }

  private DomContent makeDockerLink() {
    return a(DOCKER_HUB).withHref(DOCKER_HUB_URL + assetModel.getModuleName().replace("streampipes-", ""));
  }

  private DomContent makeDockerIcon() {
    return i().withClasses(PeGridConst.FAB, PeGridConst.FA_DOCKER);
  }

  private DomContent makeFile() {
    return div(makeFileIcon(), makeBlank(), makeDocumentationLink());
  }

  private DomContent makeDocumentationLink() {
    return a(DOCUMENTATION).withHref("/docs/docs/pe/" + assetModel.getAppId());
  }

  private DomContent makeBlank() {
    return text("  ");
  }

  private DomContent makeFileIcon() {
    return i().withClasses(PeGridConst.FAS, PeGridConst.FA_FILE);
  }

  private DomContent makeContainerItemBody() {
    return div(assetModel.getPipelineElementDescription()).withClass(PeGridConst.PE_CONTAINER_ITEM_BODY);
  }

  private DomContent makeContainerItemHeader() {
    return div(makeContainerItemIconWrapper(),
        makeContainerItemHeaderPe()).withClass(PeGridConst.PE_CONTAINER_ITEM_HEADER);
  }

  private DomContent makeContainerItemHeaderPe() {
    return div(makeContainerItemLabel(), makeContainerItemLabelName()).withClass(
        PeGridConst.PE_CONTAINER_ITEM_HEADER_PE);
  }

  private DomContent makeContainerItemLabelName() {
    return div(assetModel.getPipelineElementName()).withClass(PeGridConst.PE_CONTAINER_ITEM_LABEL_NAME);
  }

  private DomContent makeContainerItemLabel() {
    return div(makeLabelText()).withClasses(PeGridConst.PE_CONTAINER_ITEM_LABEL, makeLabelType());
  }

  private String makeLabelText() {
    return assetModel.getPeType().equals(PeType.PROCESSOR) ? "Data Processor" : "Data Sink";
  }

  private String makeLabelType() {
    return assetModel.getPeType().equals(PeType.PROCESSOR) ? PeGridConst.PE_CONTAINER_ITEM_LABEL_PROCESSOR :
        PeGridConst.PE_CONTAINER_ITEM_LABEL_SINK;
  }

  private DomContent makeContainerItemIconWrapper() {
    return div(makeIconImg()).withClasses(PeGridConst.PE_CONTAINER_ITEM_ICON, makeIconType());
  }

  private DomContent makeIconImg() {
    return img().withClass(PeGridConst.PE_ICON).withSrc(IMG_PATH_PREFIX + assetModel.getAppId() + IMG_PATH_APPENDIX);
  }

  private String makeIconType() {
    return assetModel.getPeType().equals(PeType.PROCESSOR) ? PeGridConst.PE_ITEM_ICON_PROCESSOR :
        PeGridConst.PE_ITEM_ICON_SINK;
  }

  private String getContainerItemType() {
    return assetModel.getPeType().equals(PeType.PROCESSOR) ? PeGridConst.PE_CONTAINER_ITEM_PROCESSOR :
        PeGridConst.PE_CONTAINER_ITEM_SINK;
  }

}