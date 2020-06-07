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

import * as angular from 'angular';
import {JsplumbBridge} from "../../services/jsplumb-bridge.service";
import {JsplumbService} from "../../services/jsplumb.service";
import {PipelineValidationService} from "../../services/pipeline-validation.service";
import {RestApi} from "../../../services/rest-api.service";
import {Component, EventEmitter, Input, OnInit, Output} from "@angular/core";
import {PipelineElementRecommendationService} from "../../services/pipeline-element-recommendation.service";
import {ObjectProvider} from "../../services/object-provider.service";
import {
  InvocablePipelineElementUnion,
  PipelineElementConfig, PipelineElementType,
  PipelineElementUnion
} from "../../model/editor.model";
import {SpDataStream, WildcardTopicDefinition} from "../../../core-model/gen/streampipes-model";
import {PipelineElementTypeUtils} from "../../utils/editor.utils";

@Component({
  selector: 'pipeline-element-options',
  templateUrl: './pipeline-element-options.component.html',
  styleUrls: ['./pipeline-element-options.component.css']
})
export class PipelineElementOptionsComponent implements OnInit{

  recommendationsAvailable: any;
  possibleElements: any;
  recommendedElements: any;
  recommendationsShown: any;
  pipelineElementCssType: string;

  @Input()
  currentMouseOverElement: string;

  @Input()
  pipelineElementId: string;

  @Input()
  pipelineValid: boolean;

  @Input()
  internalId: string;

  @Input()
  pipelineElement: PipelineElementConfig;

  @Input()
  rawPipelineModel: PipelineElementConfig[];

  @Input()
  allElements: PipelineElementUnion[];

  @Output()
  delete: EventEmitter<PipelineElementConfig> = new EventEmitter<PipelineElementConfig>();

  @Output()
  customize: EventEmitter<PipelineElementConfig> = new EventEmitter<PipelineElementConfig>();

  constructor(private ObjectProvider: ObjectProvider,
              private PipelineElementRecommendationService: PipelineElementRecommendationService,
              //private InitTooltips: InitTooltips,
              private JsplumbBridge: JsplumbBridge,
              //private EditorDialogManager: EditorDialogManager,
              private JsplumbService: JsplumbService,
              //private TransitionService: TransitionService,
              private PipelineValidationService: PipelineValidationService,
              private RestApi: RestApi) {
    this.recommendationsAvailable = false;
    this.possibleElements = [];
    this.recommendedElements = [];
    this.recommendationsShown = false;

  }

  ngOnInit() {
    // this.$rootScope.$on("SepaElementConfigured", (event, item) => {
    //   this.pipelineElement.settings.openCustomize = false;
    //   this.RestApi.updateCachedPipeline(this.rawPipelineModel);
    //   if (item === this.pipelineElement.payload.DOM) {
    //     this.initRecs(this.pipelineElement.payload.DOM, this.rawPipelineModel);
    //   }
    // });
    let pipelineElementType = PipelineElementTypeUtils.fromType(this.pipelineElement.payload);
    this.pipelineElementCssType = PipelineElementTypeUtils.toCssShortHand(pipelineElementType);

    if (this.pipelineElement.type === 'stream') {
      this.initRecs(this.pipelineElement.payload.dom, this.rawPipelineModel);
    }
  }

  removeElement(pipelineElement: PipelineElementConfig) {
    this.delete.emit(pipelineElement);
    //this.$rootScope.$broadcast("pipeline.validate");
  }

  customizeElement(pipelineElement: PipelineElementConfig) {
    this.customize.emit(pipelineElement);
  }

  openCustomizeDialog() {
    let restrictedEditMode = ! (this.isRootElement());
    // this.EditorDialogManager.showCustomizeDialog($("#" + this.pipelineElement.payload.dom), "", this.pipelineElement.payload, restrictedEditMode)
    //     .then(() => {
    //       this.JsplumbService.activateEndpoint(this.pipelineElement.payload.dom, !this.pipelineElement.payload.uncompleted);
    //     }, () => {
    //       this.JsplumbService.activateEndpoint(this.pipelineElement.payload.dom, !this.pipelineElement.payload.uncompleted);
    //     });
  }

  openHelpDialog() {
    //this.EditorDialogManager.openHelpDialog(this.pipelineElement.payload);
  }

  openCustomizeStreamDialog() {
    //this.EditorDialogManager.showCustomizeStreamDialog(this.pipelineElement.payload);
  }

  initRecs(elementId, currentPipelineElements) {
    // var currentPipeline = this.ObjectProvider.makePipeline(angular.copy(currentPipelineElements));
    // this.PipelineElementRecommendationService.getRecommendations(this.allElements, currentPipeline).then((result) => {
    //   if (result.success) {
    //     this.possibleElements = result.possibleElements;
    //     this.recommendedElements = result.recommendations;
    //     this.recommendationsAvailable = true;
    //     //this.InitTooltips.initTooltips();
    //   }
    // });
  }

  showRecommendations(e) {
    this.recommendationsShown = !this.recommendationsShown;
    e.stopPropagation();
  }

  isRootElement() {
    return this.JsplumbBridge.getConnections({source: this.pipelineElement.payload.dom}).length === 0;
  }

  isConfigured() {
    if (this.pipelineElement.type == 'stream') return true;
    else {
      return (this.pipelineElement.payload as InvocablePipelineElementUnion).configured;
    }
  }

  isWildcardTopic() {
    return (this.pipelineElement
        .payload as SpDataStream)
        .eventGrounding
        .transportProtocols[0]
        .topicDefinition instanceof WildcardTopicDefinition;
  }
}