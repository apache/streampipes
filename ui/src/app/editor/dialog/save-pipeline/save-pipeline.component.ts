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

import {Component, Input, OnInit} from "@angular/core";
import {DialogRef} from "../../../core-ui/dialog/base-dialog/dialog-ref";
import {
  DataProcessorInvocation,
  Message,
  Pipeline,
  NodeInfoDescription,
  StaticNodeMetadata,
  NvidiaContainerRuntime,
  DockerContainerRuntime,
  ContainerRuntime,
  DataSinkInvocation,
  SpDataStream,
  SpDataStreamUnion
} from "../../../core-model/gen/streampipes-model";
import {ObjectProvider} from "../../services/object-provider.service";
import {EditorService} from "../../services/editor.service";
import {PipelineService} from "../../../platform-services/apis/pipeline.service";
import {ShepherdService} from "../../../services/tour/shepherd.service";
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {Router} from "@angular/router";
import {NodeService} from "../../../platform-services/apis/node.service";

@Component({
  selector: 'save-pipeline',
  templateUrl: './save-pipeline.component.html',
  styleUrls: ['./save-pipeline.component.scss']
})
export class SavePipelineComponent implements OnInit {
  priorityForm: FormGroup;

  pipelineCategories: any;
  startPipelineAfterStorage: any;
  updateMode: any;
  submitPipelineForm: FormGroup = new FormGroup({});
  saving: boolean = false;
  saved: boolean = false;
  storageError: boolean = false;
  errorMessage: string = '';
  edgeNodes: NodeInfoDescription[];
  advancedSettings: boolean = false;
  deploymentOptions: Array<any> = new Array<any>();
  selectedRelayStrategyVal: string;
  selectedPipelineExecutionPolicy: string;
  disableNodeSelection = new FormControl(true);
  tmpPipeline: Pipeline;
  panelOpenState: boolean;
  selectedPreemption: boolean;
  selectedNodeTags: string[];

  filteredNodes = new FormControl();

  pipelineExecutionPolicies: string[] = ['default', 'locality-aware', 'custom'];
  pipelinePriorityClasses = [
    {value: 1, viewValue: 'low'},
    {value: 5, viewValue: 'medium'},
    {value: 10, viewValue: 'high'}];

  @Input()
  pipeline: Pipeline;

  @Input()
  modificationMode: string;

  @Input()
  currentModifiedPipelineId: string;

  constructor(private editorService: EditorService,
              private formBuilder: FormBuilder,
              private dialogRef: DialogRef<SavePipelineComponent>,
              private objectProvider: ObjectProvider,
              private pipelineService: PipelineService,
              private nodeService: NodeService,
              private Router: Router,
              private ShepherdService: ShepherdService) {
    this.pipelineCategories = [];
    this.updateMode = "update";
  }

  ngOnInit() {
    this.tmpPipeline = this.deepCopy(this.pipeline);

    this.priorityForm = this.formBuilder.group({
      priorityForm: [null, Validators.required]
    });

    this.getPipelineCategories();
    this.loadAndPrepareEdgeNodes();

    this.submitPipelineForm.addControl("pipelineName", new FormControl(this.tmpPipeline.name,
        [Validators.required,
          Validators.maxLength(40)]))
    this.submitPipelineForm.addControl("pipelineDescription", new FormControl(this.tmpPipeline.description,
        [Validators.maxLength(80)]))

    this.submitPipelineForm.controls["pipelineName"].valueChanges.subscribe(value => {
      this.tmpPipeline.name = value;
    });

    this.submitPipelineForm.controls["pipelineDescription"].valueChanges.subscribe(value => {
      this.tmpPipeline.description = value;
    });

    if (this.ShepherdService.isTourActive()) {
      this.ShepherdService.trigger("enter-pipeline-name");
    }

    if (this.currentModifiedPipelineId && this.updateMode === 'update') {
      this.selectedRelayStrategyVal = this.tmpPipeline.eventRelayStrategy;
      this.selectedPreemption = this.tmpPipeline.preemption;
      this.selectedNodeTags = this.tmpPipeline.nodeTags;

      const selectedPriorityClass = this.pipelinePriorityClasses.find(c => c.value == this.tmpPipeline.priorityScore);
      this.priorityForm.get('priorityForm').setValue(selectedPriorityClass);

      this.selectedPipelineExecutionPolicy = this.tmpPipeline.executionPolicy;
      if (this.selectedPipelineExecutionPolicy === "custom"){
        this.panelOpenState = true;
        this.disableNodeSelection.setValue(false);
      }

    } else {
      this.selectedRelayStrategyVal = "buffer";
      this.selectedPipelineExecutionPolicy = "locality-aware";
      this.selectedPreemption = false;
      this.applyLocalityAwarePolicy();
    }
  }

  deepCopy<T>(source: T): T {
    return Array.isArray(source)
        ? source.map(item => this.deepCopy(item))
        : source instanceof Date
            ? new Date(source.getTime())
            : source && typeof source === 'object'
                ? Object.getOwnPropertyNames(source).reduce((o, prop) => {
                  Object.defineProperty(o, prop, Object.getOwnPropertyDescriptor(source, prop));
                  o[prop] = this.deepCopy(source[prop]);
                  return o;
                }, Object.create(Object.getPrototypeOf(source)))
                : source as T;
  }

  triggerTutorial() {
    if (this.ShepherdService.isTourActive()) {
      this.ShepherdService.trigger("save-pipeline-dialog");
    }
  }

  displayErrors(data?: string) {
    this.storageError = true;
    this.errorMessage = data;
  }

  getPipelineCategories() {
    this.pipelineService.getPipelineCategories().subscribe(pipelineCategories => {
      this.pipelineCategories = pipelineCategories;
    });
  };

  applyLocalityAwarePolicy() {
    this.tmpPipeline.streams.forEach(s => {
      //let processors: DataProcessorInvocation[];
      //processors = this.pipeline.sepas.filter(p => p.connectedTo.some(entry => entry == s.dom));
      this.tmpPipeline.sepas.forEach(processor => {
        this.deploymentOptions[processor.appId] = [];
        this.deploymentOptions[processor.appId].push(this.makeDefaultNodeInfo());

        this.nodeService.getOnlineNodes().subscribe((response : NodeInfoDescription []) => {
          this.edgeNodes = response;
          this.edgeNodes.forEach(nodeInfo => {
            // only show nodes that actually have supported pipeline elements registered
            if (nodeInfo.supportedElements.length != 0 &&
                nodeInfo.supportedElements.some(appId => appId === processor.appId)) {
              this.deploymentOptions[processor.appId].push(nodeInfo);
            }
          })
        });

        processor.deploymentTargetNodeId = s.deploymentTargetNodeId;
        processor.deploymentTargetNodeHostname = s.deploymentTargetNodeHostname;
        processor.deploymentTargetNodePort = s.deploymentTargetNodePort;
      });

    //   this.tmpPipeline.actions.forEach(p => {
    //     p.deploymentTargetNodeId = s.deploymentTargetNodeId;
    //     p.deploymentTargetNodeHostname = s.deploymentTargetNodeHostname;
    //     p.deploymentTargetNodePort = s.deploymentTargetNodePort;
    //   });
    });
  }

  private applyTagBasedPolicy(filteredNodes: NodeInfoDescription[]) {
    if (filteredNodes.length > 0) {
      this.tmpPipeline.sepas.forEach(processor => {
        this.deploymentOptions[processor.appId] = [];

        filteredNodes.forEach(filteredNode => {

          if (filteredNode.supportedElements.length != 0 &&
              filteredNode.supportedElements.some(appId => appId === processor.appId)) {
            this.deploymentOptions[processor.appId].push(filteredNode);
          }
        })
      })

      this.tmpPipeline.actions.forEach(actions => {
        this.deploymentOptions[actions.appId] = [];

        filteredNodes.forEach(filteredNode => {

          if (filteredNode.supportedElements.length != 0 &&
              filteredNode.supportedElements.some(appId => appId === actions.appId)) {
            this.deploymentOptions[actions.appId].push(filteredNode);
          }
        })
      })

    } else {
      this.addAppIds(this.tmpPipeline.sepas, this.edgeNodes);
      this.addAppIds(this.tmpPipeline.actions, this.edgeNodes);
    }
  }

  private applyDefaultPolicy() {
    this.tmpPipeline.nodeTags = [];
    this.tmpPipeline.sepas.forEach(processor => {
      processor.deploymentTargetNodeId = "default";
      this.deploymentOptions[processor.appId] = []
      this.deploymentOptions[processor.appId].push(this.makeDefaultNodeInfo());
    });

    this.tmpPipeline.actions.forEach(action => {
      action.deploymentTargetNodeId = "default";
      this.deploymentOptions[action.appId] = []
      this.deploymentOptions[action.appId].push(this.makeDefaultNodeInfo());
    });
  }

  loadAndPrepareEdgeNodes() {
    this.nodeService.getOnlineNodes().subscribe((response : NodeInfoDescription []) => {
      this.edgeNodes = response;
      this.addAppIds(this.tmpPipeline.sepas, this.edgeNodes);
      this.addAppIds(this.tmpPipeline.actions, this.edgeNodes);
    });
  }

  addAppIds(pipelineElements, edgeNodes: Array<NodeInfoDescription>) {
    pipelineElements.forEach(p => {
      this.deploymentOptions[p.appId] = [];

      if (p instanceof DataSinkInvocation) {
        if (p.deploymentTargetNodeId == null) {
          p.deploymentTargetNodeId = "default";
        }
        this.deploymentOptions[p.appId].push(this.makeDefaultNodeInfo());
      }

      // if (p.deploymentTargetNodeId == null) {
      //   p.deploymentTargetNodeId = "default";
      // }
      // this.deploymentOptions[p.appId].push(this.makeDefaultNodeInfo());

      edgeNodes.forEach(nodeInfo => {
        // only show nodes that actually have supported pipeline elements registered
        if (nodeInfo.supportedElements.length != 0 &&
            nodeInfo.supportedElements.some(appId => appId === p.appId)) {
          this.deploymentOptions[p.appId].push(nodeInfo);
        }
      })
    });
  }

  makeDefaultNodeInfo() {
    let nodeInfo = {} as NodeInfoDescription;
    nodeInfo.nodeControllerId = "default";
    nodeInfo.hostname = "default";
    nodeInfo.staticNodeMetadata = {} as StaticNodeMetadata;
    nodeInfo.staticNodeMetadata.type = "default";
    nodeInfo.staticNodeMetadata.model = "Default Node";
    return nodeInfo;
  }

  modifyPipelineElementsDeployments(pipelineElements) {
    pipelineElements.forEach(p => {
      let selectedTargetNodeId = p.deploymentTargetNodeId

      // Currently relay only for data processors
      if (p instanceof DataProcessorInvocation) {
        p.eventRelayStrategy = this.selectedRelayStrategyVal;
      }

      if(selectedTargetNodeId != "default") {
        let selectedNode = this.edgeNodes
            .filter(node => node.nodeControllerId === selectedTargetNodeId)

        p.deploymentTargetNodeHostname = selectedNode
            .map(node => node.hostname)[0]

        p.deploymentTargetNodePort = selectedNode
            .map(node => node.port)[0]
      }
      else {
        p.deploymentTargetNodeHostname = null
        p.deploymentTargetNodePort = null
      }
    })
  }

  modifyPipelineElementsPreemption(pipelineElements){
    pipelineElements.forEach(p => {
      p.preemption = this.selectedPreemption;
      p.priorityScore = this.tmpPipeline.priorityScore;
    })
  }

  savePipeline(switchTab) {
    if (this.tmpPipeline.name == "") {
      //this.showToast("error", "Please enter a name for your pipeline");
      return false;
    }

    let storageRequest;

    if (this.currentModifiedPipelineId && this.updateMode === 'update') {
      this.modifyPipelineElementsDeployments(this.tmpPipeline.sepas);
      this.modifyPipelineElementsDeployments(this.tmpPipeline.actions);
      this.tmpPipeline.eventRelayStrategy = this.selectedRelayStrategyVal;
      this.tmpPipeline.executionPolicy = this.selectedPipelineExecutionPolicy;
      this.tmpPipeline.preemption = this.selectedPreemption;
      if (this.selectedPreemption) {
        this.tmpPipeline.priorityScore = this.priorityForm.get('priorityForm').value.value
      } else {
        this.tmpPipeline.priorityScore = 0;
      }
      this.modifyPipelineElementsPreemption(this.tmpPipeline.sepas);
      this.modifyPipelineElementsPreemption(this.tmpPipeline.actions);
      if (this.selectedNodeTags?.length > 0 && this.selectedPipelineExecutionPolicy === "custom") {
        this.tmpPipeline.nodeTags = this.selectedNodeTags;
      } else {
        this.tmpPipeline.nodeTags = null;
      }

      this.pipeline = this.tmpPipeline;
      storageRequest = this.pipelineService.updatePipeline(this.pipeline);
    } else {
      this.pipeline._id = undefined;
      this.modifyPipelineElementsDeployments(this.tmpPipeline.sepas);
      this.modifyPipelineElementsDeployments(this.tmpPipeline.actions);
      this.tmpPipeline.eventRelayStrategy = this.selectedRelayStrategyVal;
      this.tmpPipeline.executionPolicy = this.selectedPipelineExecutionPolicy;
      this.tmpPipeline.preemption = this.selectedPreemption;
      if (this.selectedPreemption) {
        this.tmpPipeline.priorityScore = this.priorityForm.get('priorityForm').value.value
      } else {
        this.tmpPipeline.priorityScore = 0;
      }
      this.modifyPipelineElementsPreemption(this.tmpPipeline.sepas);
      this.modifyPipelineElementsPreemption(this.tmpPipeline.actions);
      if (this.selectedNodeTags?.length > 0 && this.selectedPipelineExecutionPolicy === "custom") {
        this.tmpPipeline.nodeTags = this.selectedNodeTags;
      } else {
        this.tmpPipeline.nodeTags = null;
      }

      this.pipeline = this.tmpPipeline;
      storageRequest = this.pipelineService.storePipeline(this.pipeline);
    }

    storageRequest
        .subscribe(statusMessage => {
          if (statusMessage.success) {
            let pipelineId: string = this.currentModifiedPipelineId || statusMessage.notifications[1].description;
            this.afterStorage(statusMessage, switchTab, pipelineId);
          } else {
            this.displayErrors(statusMessage.notifications[0]);
          }
        }, data => {
          this.displayErrors();
        });
  };

  afterStorage(statusMessage: Message, switchTab, pipelineId?: string) {
    this.hide();
    this.editorService.makePipelineAssemblyEmpty(true);
    this.editorService.removePipelineFromCache().subscribe();
    if (this.ShepherdService.isTourActive()) {
      this.ShepherdService.hideCurrentStep();
    }
    if (switchTab && !this.startPipelineAfterStorage) {
      this.Router.navigate(["pipelines"]);
    }
    if (this.startPipelineAfterStorage) {
      this.Router.navigate(["pipelines"], { queryParams: {pipeline: pipelineId}});
    }
  }

  hide() {
    this.dialogRef.close();
  };

  onSelectedRelayStrategyChange(value: string) {
    this.selectedRelayStrategyVal = value;
  }

  onExecutionPolicyChange(value: any) {
    this.selectedPipelineExecutionPolicy = value;

    if (value == "custom") {
      this.panelOpenState = true;
      this.disableNodeSelection.setValue(false);
      //this.addAppIds(this.tmpPipeline.sepas, this.edgeNodes);
      //this.addAppIds(this.tmpPipeline.actions, this.edgeNodes);
      // use same policy for initial mapping
      this.applyLocalityAwarePolicy()
    } else if (value == "locality-aware") {
      this.panelOpenState = false;
      this.disableNodeSelection.setValue(true);
      this.applyLocalityAwarePolicy()
    } else if (value == "default") {
      this.panelOpenState = false;
      this.disableNodeSelection.setValue(true);
      this.applyDefaultPolicy();
    }
  }

  nodesFromSelectedTags(filteredNodes: NodeInfoDescription[]) {
    this.applyTagBasedPolicy(filteredNodes)
  }

  updateNodeTags($event: any) {
    this.selectedNodeTags = $event;
  }

  loadDefaultPreemption() {
    const selectedPriorityClass = this.pipelinePriorityClasses.find(c => c.value == 1);
    this.priorityForm.get('priorityForm').setValue(selectedPriorityClass);
  }
}