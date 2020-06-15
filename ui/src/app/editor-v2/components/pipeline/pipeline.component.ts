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

import * as angular from "angular";

import {PipelineValidationService} from "../../../editor-v2/services/pipeline-validation.service";
import {RestApi} from "../../../services/rest-api.service";
import {JsplumbService} from "../../services/jsplumb.service";
import {PipelineEditorService} from "../../services/pipeline-editor.service";
import {JsplumbBridge} from "../../services/jsplumb-bridge.service";
import {ShepherdService} from "../../../services/tour/shepherd.service";
import {Component, EventEmitter, Input, OnInit, Output} from "@angular/core";
import {
  InvocablePipelineElementUnion,
  PipelineElementConfig,
  PipelineElementUnion
} from "../../model/editor.model";
import {
  CustomOutputStrategy,
  DataProcessorInvocation,
  Pipeline,
  SpDataStream
} from "../../../core-model/gen/streampipes-model";
import {ObjectProvider} from "../../services/object-provider.service";
import {CustomizeComponent} from "../../dialog/customize/customize.component";
import {PanelType} from "../../../core-ui/dialog/base-dialog/base-dialog.model";
import {DialogService} from "../../../core-ui/dialog/base-dialog/base-dialog.service";
import {EditorService} from "../../services/editor.service";
import {MatchingResultMessage} from "../../../core-model/gen/streampipes-model-client";
import {MatchingErrorComponent} from "../../dialog/matching-error/matching-error.component";

@Component({
  selector: 'pipeline',
  templateUrl: './pipeline.component.html',
  styleUrls: ['./pipeline.component.css']
})
export class PipelineComponent implements OnInit {

  @Input()
  pipelineValid: boolean;

  @Input()
  canvasId: string;

  @Input()
  rawPipelineModel: PipelineElementConfig[];

  @Input()
  allElements: PipelineElementUnion[];

  @Input()
  preview: boolean;

  @Input()
  pipelineCached: boolean;

  @Output()
  pipelineCachedChanged: EventEmitter<boolean> = new EventEmitter<boolean>();

  @Input()
  pipelineCacheRunning: boolean;

  @Output()
  pipelineCacheRunningChanged: EventEmitter<boolean> = new EventEmitter<boolean>();

  availablePipelineElementCache: PipelineElementUnion[];

  DialogBuilder: any;
  plumbReady: any;
  EditorDialogManager: any;
  currentMouseOverElement: string;
  currentPipelineModel: Pipeline;
  idCounter: any;
  currentZoomLevel: any;
  TransitionService: any;

  // remove later

  constructor(private JsplumbService: JsplumbService,
              private PipelineEditorService: PipelineEditorService,
              private JsplumbBridge: JsplumbBridge,
              private ObjectProvider: ObjectProvider,
              private EditorService: EditorService,
              //DialogBuilder,
              //EditorDialogManager,
              // TransitionService,
              private ShepherdService: ShepherdService,
              private PipelineValidationService: PipelineValidationService,
              private RestApi: RestApi,
              private dialogService: DialogService) {
    this.plumbReady = false;
    this.currentMouseOverElement = "";
    this.currentPipelineModel = new Pipeline();
    this.idCounter = 0;

    this.currentZoomLevel = 1;
  }

  ngOnInit() {
    this.JsplumbBridge.setContainer(this.canvasId);
    this.initAssembly();
    this.initPlumb();
  }

  validatePipeline() {
      this.pipelineValid = this.PipelineValidationService.isValidPipeline(this.rawPipelineModel);
  }

  ngOnDestroy() {
    this.JsplumbBridge.deleteEveryEndpoint();
    this.plumbReady = false;
  }

  updateMouseover(elementId) {
    this.currentMouseOverElement = elementId;
  }

  updateOptionsClick(elementId) {
    if (this.currentMouseOverElement == elementId) {
      this.currentMouseOverElement = "";
    } else {
      this.currentMouseOverElement = elementId;
    }
  }

  getElementCss(currentPipelineElementSettings) {
    return "position:absolute;"
        + (this.preview ? "width:75px;" : "width:110px;")
        + (this.preview ? "height:75px;" : "height:110px;")
        + "left: " + currentPipelineElementSettings.position.x + "px; "
        + "top: " + currentPipelineElementSettings.position.y + "px; "
  }

  getElementCssClasses(currentPipelineElement) {
    return currentPipelineElement.type + " " + (currentPipelineElement.settings.openCustomize ? "" : "")
        + currentPipelineElement.settings.connectable + " "
        + currentPipelineElement.settings.displaySettings;
  }

  isStreamInPipeline() {
    return this.isInPipeline('stream');
  }

  isSetInPipeline() {
    return this.isInPipeline('set');
  }

  isInPipeline(type) {
    return this.rawPipelineModel.some(x => (x.type == type && !(x.settings.disabled)));
  }

  showMixedStreamAlert() {
    this.EditorDialogManager.showMixedStreamAlert();
  }

  findPipelineElementByElementId(elementId: string) {
    return this.allElements.find(a => a.elementId === elementId);
  }


  initAssembly() {
    ($('#assembly') as any).droppable({
      tolerance: "fit",
      drop: (element, ui) => {
        let pipelineElementId = ui.draggable.data("pe");
        let pipelineElement: PipelineElementUnion = this.findPipelineElementByElementId(pipelineElementId);
        if (ui.draggable.hasClass('draggable-icon')) {
          this.EditorService.makePipelineAssemblyEmpty(false);
          var pipelineElementConfig = this.JsplumbService.createNewPipelineElementConfig(pipelineElement, this.PipelineEditorService.getCoordinates(ui, this.currentZoomLevel), false);
          if ((this.isStreamInPipeline() && pipelineElementConfig.type == 'set') ||
              this.isSetInPipeline() && pipelineElementConfig.type == 'stream') {
            this.showMixedStreamAlert();
          } else {
            this.rawPipelineModel.push(pipelineElementConfig);
            if (ui.draggable.hasClass('set')) {
              setTimeout(() => {
                setTimeout(() => {
                  this.JsplumbService.setDropped(pipelineElementConfig.payload.dom, pipelineElementConfig.payload, true, false);
                });
              });
            }
            else if (ui.draggable.hasClass('stream')) {
              this.checkTopicModel(pipelineElementConfig);
            } else if (ui.draggable.hasClass('sepa')) {
              setTimeout(() => {
                setTimeout(() => {
                  this.JsplumbService.sepaDropped(pipelineElementConfig.payload.dom, pipelineElementConfig.payload, true, false);
                });
              });
              //Droppable Actions
            } else if (ui.draggable.hasClass('action')) {
              setTimeout(() => {
                setTimeout(() => {
                  this.JsplumbService.actionDropped(pipelineElementConfig.payload.dom, pipelineElementConfig.payload, true, false);
                });
              });
            }
            if (this.ShepherdService.isTourActive()) {
              this.ShepherdService.trigger("drop-" +pipelineElementConfig.type);
            }
          }
        }
        this.JsplumbBridge.repaintEverything();
        this.validatePipeline();
        this.triggerPipelineCacheUpdate();
      }

    }); //End #assembly.droppable()
  }

  checkTopicModel(pipelineElementConfig: PipelineElementConfig) {
    setTimeout(() => {
      setTimeout(() => {
        this.JsplumbService.streamDropped(pipelineElementConfig.payload.dom, pipelineElementConfig.payload, true, false);
      });
    });

    var streamDescription = pipelineElementConfig.payload as SpDataStream;
    if (streamDescription
        .eventGrounding
        .transportProtocols[0]
        .topicDefinition["@class"] === "org.apache.streampipes.model.grounding.WildcardTopicDefinition") {
      this.EditorDialogManager.showCustomizeStreamDialog(streamDescription);
    }
  }

  handleDeleteOption(pipelineElement: PipelineElementConfig) {
    this.JsplumbBridge.removeAllEndpoints(pipelineElement.payload.dom);
    angular.forEach(this.rawPipelineModel, pe => {
      if (pe.payload.dom == pipelineElement.payload.dom) {
        pe.settings.disabled = true;
      }
    });
    if (this.rawPipelineModel.every(pe => pe.settings.disabled)) {
      this.EditorService.makePipelineAssemblyEmpty(true);
    }
    this.JsplumbBridge.repaintEverything();
    this.triggerPipelineCacheUpdate();
  }

  initPlumb() {

    this.JsplumbService.prepareJsplumb();

    this.JsplumbBridge.unbind("connection");

    this.JsplumbBridge.bind("connectionMoved", (info, originalEvent) => {
      var pe = this.ObjectProvider.findElement(info.newTargetEndpoint.elementId, this.rawPipelineModel);
      var oldPe = this.ObjectProvider.findElement(info.originalTargetEndpoint.elementId, this.rawPipelineModel);
      (oldPe.payload as InvocablePipelineElementUnion).configured = false;
      (pe.payload as InvocablePipelineElementUnion).configured = false;
    });

    this.JsplumbBridge.bind("connectionDetached", (info, originalEvent) => {
      var pe = this.ObjectProvider.findElement(info.targetEndpoint.elementId, this.rawPipelineModel);
      (pe.payload as InvocablePipelineElementUnion).configured = false;
      pe.settings.openCustomize = true;
      info.targetEndpoint.setType("empty");
      this.validatePipeline();
    });

    this.JsplumbBridge.bind("connectionDrag", connection => {
      this.JsplumbBridge.selectEndpoints().each(function (endpoint) {
        if (endpoint.isTarget && endpoint.connections.length === 0) {
          endpoint.setType("highlight");
        }
      });

    });
    this.JsplumbBridge.bind("connectionAborted", connection => {
      this.JsplumbBridge.selectEndpoints().each(endpoint => {
        if (endpoint.isTarget && endpoint.connections.length === 0) {
          endpoint.setType("empty");
        }
      });
    })

    this.JsplumbBridge.bind("connection", (info, originalEvent) => {
      var pe = this.ObjectProvider.findElement(info.target.id, this.rawPipelineModel);
      if (pe.settings.openCustomize) {
        this.currentPipelineModel = this.ObjectProvider.makePipeline(this.rawPipelineModel);
        pe.settings.loadingStatus = true;
        this.ObjectProvider.updatePipeline(this.currentPipelineModel)
            .subscribe(pipelineModificationMessage => {
              pe.settings.loadingStatus = false;
              info.targetEndpoint.setType("token");
              this.validatePipeline();
              this.modifyPipeline(pipelineModificationMessage.pipelineModifications);
              var sourceEndpoint = this.JsplumbBridge.selectEndpoints({element: info.targetEndpoint.elementId});
              if (this.PipelineEditorService.isFullyConnected(pe)) {
                let payload = pe.payload as InvocablePipelineElementUnion;
                if ((payload.staticProperties && payload.staticProperties.length > 0) || this.isCustomOutput(pe)) {
                  this.showCustomizeDialog(pe);
                } else {
                  this.announceConfiguredElement(pe);
                  //this.$rootScope.$broadcast("SepaElementConfigured", pe.payload.DOM);
                  (pe.payload as InvocablePipelineElementUnion).configured = true;
                }
              }
            }, status => {
              pe.settings.loadingStatus = false;
              this.JsplumbBridge.detach(info.connection);
              let matchingResultMessage = (status.error as any[]).map(e => MatchingResultMessage.fromData(e as MatchingResultMessage));
              this.showMatchingErrorDialog(matchingResultMessage);
            });
      }
    });

    window.onresize = (event) => {
      this.JsplumbBridge.repaintEverything();
    };

    setTimeout(() => {
      this.plumbReady = true;
    }, 100);
  }

  modifyPipeline(pipelineModifications) {
    for (var i = 0, modification; modification = pipelineModifications[i]; i++) {
      var id = modification.domId;
      if (id !== "undefined") {
        var pe = this.ObjectProvider.findElement(id, this.rawPipelineModel);
        (pe.payload as InvocablePipelineElementUnion).staticProperties = modification.staticProperties;
        (pe.payload as DataProcessorInvocation).outputStrategies = modification.outputStrategies;
        (pe.payload as InvocablePipelineElementUnion).inputStreams = modification.inputStreams;
      }
    }
  }

  isCustomOutput(pe) {
    var custom = false;
    angular.forEach(pe.payload.outputStrategies, strategy => {
      if (strategy instanceof CustomOutputStrategy) {
        custom = true;
      }
    });
    return custom;
  }

  triggerPipelineCacheUpdate() {
    this.pipelineCacheRunning = true;
    this.pipelineCacheRunningChanged.emit(this.pipelineCacheRunning);
    this.EditorService.updateCachedPipeline(this.rawPipelineModel).subscribe(msg => {
      this.pipelineCacheRunning = false;
      this.pipelineCacheRunningChanged.emit(this.pipelineCacheRunning)
      this.pipelineCached = true;
      this.pipelineCachedChanged.emit(this.pipelineCached);
    });
  }

  showMatchingErrorDialog(matchingResultMessage: MatchingResultMessage[]) {
    this.dialogService.open(MatchingErrorComponent, {
      panelType: PanelType.STANDARD_PANEL,
      title: "Invalid Connection",
      data: {
        "matchingResultMessage": matchingResultMessage
      }
    });
  }

  showCustomizeDialog(pipelineElement: PipelineElementConfig) {
    const dialogRef = this.dialogService.open(CustomizeComponent,{
      panelType: PanelType.SLIDE_IN_PANEL,
      title: "Customize " + pipelineElement.payload.name,
      data: {
        "pipelineElement": pipelineElement
      }
    });

    dialogRef.afterClosed().subscribe(c => {
      if (c) {
        pipelineElement.settings.openCustomize = false;
        this.JsplumbService.activateEndpoint(pipelineElement.payload.dom, pipelineElement.settings.completed);
        this.triggerPipelineCacheUpdate();
      }
    });
  }

  announceConfiguredElement(pe: PipelineElementConfig) {
    this.EditorService.announceConfiguredElement(pe.payload.dom);
  }


}