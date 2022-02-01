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

import {JsplumbBridge} from "../../services/jsplumb-bridge.service";
import {JsplumbService} from "../../services/jsplumb.service";
import {PipelineValidationService} from "../../services/pipeline-validation.service";
import {RestApi} from "../../../services/rest-api.service";
import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from "@angular/core";
import {PipelineElementRecommendationService} from "../../services/pipeline-element-recommendation.service";
import {ObjectProvider} from "../../services/object-provider.service";
import {
  InvocablePipelineElementUnion,
  PipelineElementConfig,
  PipelineElementUnion
} from "../../model/editor.model";
import {SpDataStream, WildcardTopicDefinition} from "../../../../../projects/streampipes/platform-services/src/lib/model/gen/streampipes-model";
import {EditorService} from "../../services/editor.service";
import {PanelType} from "../../../core-ui/dialog/base-dialog/base-dialog.model";
import {DialogService} from "../../../core-ui/dialog/base-dialog/base-dialog.service";
import {CompatibleElementsComponent} from "../../dialog/compatible-elements/compatible-elements.component";
import {Tuple2} from "../../../core-model/base/Tuple2";
import { cloneDeep } from "lodash";
import {Observable, Subscription} from "rxjs";
import {JsplumbFactoryService} from "../../services/jsplumb-factory.service";

@Component({
  selector: 'pipeline-element-options',
  templateUrl: './pipeline-element-options.component.html',
  styleUrls: ['./pipeline-element-options.component.css']
})
export class PipelineElementOptionsComponent implements OnInit, OnDestroy {

  recommendationsAvailable: any = false;
  possibleElements: PipelineElementUnion[];
  recommendedElements: PipelineElementUnion[];
  recommendationsShown: any = false;
  pipelineElementCssType: string;
  isDataSource: boolean;

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
  customize: EventEmitter<Tuple2<Boolean, PipelineElementConfig>> = new EventEmitter<Tuple2<Boolean, PipelineElementConfig>>();

  pipelineElementConfiguredObservable: Subscription;

  JsplumbBridge: JsplumbBridge;

  constructor(private ObjectProvider: ObjectProvider,
              private PipelineElementRecommendationService: PipelineElementRecommendationService,
              private DialogService: DialogService,
              private EditorService: EditorService,
              private JsplumbFactoryService: JsplumbFactoryService,
              private JsplumbService: JsplumbService,
              private PipelineValidationService: PipelineValidationService,
              private RestApi: RestApi) {
    this.recommendationsAvailable = false;
    this.possibleElements = [];
    this.recommendedElements = [];
    this.recommendationsShown = false;
    this.JsplumbBridge = this.JsplumbFactoryService.getJsplumbBridge(false);
  }

  ngOnInit() {
    this.pipelineElementConfiguredObservable = this.EditorService.pipelineElementConfigured$.subscribe(pipelineElementDomId => {
      this.pipelineElement.settings.openCustomize = false;
      this.RestApi.updateCachedPipeline(this.rawPipelineModel);
      if (pipelineElementDomId === this.pipelineElement.payload.dom) {
        this.initRecs(this.pipelineElement.payload.dom);
      }
    });
    this.pipelineElementCssType = this.pipelineElement.type;

    this.isDataSource = this.pipelineElement.type === 'stream' || this.pipelineElement.type === 'set';

    if (this.isDataSource || this.pipelineElement.settings.completed) {
      this.initRecs(this.pipelineElement.payload.dom);
    }
  }

  removeElement(pipelineElement: PipelineElementConfig) {
    this.delete.emit(pipelineElement);
  }

  customizeElement(pipelineElement: PipelineElementConfig) {
    let restrictedEditMode = ! (this.isRootElement());
    let customizeInfo = {a: restrictedEditMode, b: pipelineElement} as Tuple2<Boolean, PipelineElementConfig>;
    this.customize.emit(customizeInfo);
  }

  openHelpDialog() {
    this.EditorService.openHelpDialog(this.pipelineElement.payload);
  }

  openCustomizeStreamDialog() {
    //this.EditorDialogManager.showCustomizeStreamDialog(this.pipelineElement.payload);
  }

  initRecs(pipelineElementDomId) {
    let clonedModel: PipelineElementConfig[] = cloneDeep(this.rawPipelineModel);
    clonedModel.forEach(pe => {
      if (pe.payload.dom === pipelineElementDomId && (pe.type !== 'stream'  && pe.type !== 'set')) {
        pe.settings.completed = false;
        (pe.payload as InvocablePipelineElementUnion).configured = false;
      }
    })
    var currentPipeline = this.ObjectProvider.makePipeline(clonedModel);
    this.EditorService.recommendPipelineElement(currentPipeline).subscribe((result) => {
      if (result.success) {
        this.possibleElements = cloneDeep(this.PipelineElementRecommendationService.collectPossibleElements(this.allElements, result.possibleElements));
        this.recommendedElements = cloneDeep(this.PipelineElementRecommendationService.populateRecommendedList(this.allElements, result.recommendedElements));
        this.recommendationsAvailable = true;
      }
    });
  }

  openPossibleElementsDialog() {
    const dialogRef = this.DialogService.open(CompatibleElementsComponent,{
      panelType: PanelType.SLIDE_IN_PANEL,
      title: 'Compatible Elements',
      data: {
        'rawPipelineModel': this.rawPipelineModel,
        'possibleElements': this.possibleElements,
        'pipelineElementDomId': this.pipelineElement.payload.dom
      }
    });

    dialogRef.afterClosed().subscribe(c => {

    });
  }

  showRecommendations(e) {
    this.recommendationsShown = !this.recommendationsShown;
    e.stopPropagation();
  }

  isRootElement() {
    return this.JsplumbBridge.getConnections({source: this.pipelineElement.payload.dom}).length === 0;
  }

  isConfigured() {
    if (this.isDataSource) return true;
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

  ngOnDestroy(): void {
    this.pipelineElementConfiguredObservable.unsubscribe();
  }
}
