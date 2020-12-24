import {Component, Input, OnInit} from '@angular/core';
import {
  DataProcessorInvocation, Message,
  NodeInfoDescription,
  Pipeline,
  StaticNodeMedata
} from "../../../core-model/gen/streampipes-model";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {EditorService} from "../../services/editor.service";
import {DialogRef} from "../../../core-ui/dialog/base-dialog/dialog-ref";
import {ObjectProvider} from "../../services/object-provider.service";
import {PipelineService} from "../../../platform-services/apis/pipeline.service";

@Component({
  selector: 'migrate-pipeline-processors',
  templateUrl: './migrate-pipeline-processors.component.html',
  styleUrls: ['./migrate-pipeline-processors.component.scss']
})
export class MigratePipelineProcessorsComponent implements OnInit {

  submitPipelineForm: FormGroup = new FormGroup({});
  saving: boolean = false;
  saved: boolean = false;
  storageError: boolean = false;
  errorMessage: string = '';
  edgeNodes: NodeInfoDescription[];
  advancedSettings: boolean;
  deploymentOptions: Array<any> = new Array<any>();
  selectedRelayStrategyVal: string;
  selectedPipelineExecutionPolicy: string;
  disableNodeSelectionForProcessors = new FormControl(false);
  disableNodeSelectionForSinks = new FormControl(true);
  tmpPipeline: Pipeline;
  panelOpenState: boolean;
  pipelineExecutionPolicies: string[] = ['default', 'locality-aware', 'custom'];

  @Input()
  pipeline: Pipeline;

  constructor(private editorService: EditorService,
              private dialogRef: DialogRef<MigratePipelineProcessorsComponent>,
              private objectProvider: ObjectProvider,
              private pipelineService: PipelineService) {

    this.advancedSettings = true;
    this.panelOpenState = true;
  }

  ngOnInit() {
    this.tmpPipeline = this.deepCopy(this.pipeline);

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

    this.selectedRelayStrategyVal = "buffer";
    this.selectedPipelineExecutionPolicy = "custom";

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

  displayErrors(data?: string) {
    this.storageError = true;
    this.errorMessage = data;
  }

  loadAndPrepareEdgeNodes() {
    this.pipelineService.getAvailableEdgeNodes().subscribe(response => {
      this.edgeNodes = response;
      this.addAppIds(this.tmpPipeline.sepas, this.edgeNodes);
      this.addAppIds(this.tmpPipeline.actions, this.edgeNodes);
    });
  }

  addAppIds(pipelineElements, edgeNodes: Array<NodeInfoDescription>) {
    pipelineElements.forEach(p => {
      this.deploymentOptions[p.appId] = [];

      if (p.deploymentTargetNodeId == null) {
        p.deploymentTargetNodeId = "default";
      }
      this.deploymentOptions[p.appId].push(this.makeDefaultNodeInfo());

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
    nodeInfo.staticNodeMedata = {} as StaticNodeMedata;
    nodeInfo.staticNodeMedata.type = "default";
    nodeInfo.staticNodeMedata.model = "Default Node";
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

  migratePipelineProccesors() {
    if (this.tmpPipeline.name == "") {
      //this.showToast("error", "Please enter a name for your pipeline");
      return false;
    }

    let migrationRequest;

    this.modifyPipelineElementsDeployments(this.tmpPipeline.sepas)
    this.modifyPipelineElementsDeployments(this.tmpPipeline.actions)
    this.tmpPipeline.eventRelayStrategy = this.selectedRelayStrategyVal;
    this.pipeline = this.tmpPipeline;

    migrationRequest = this.pipelineService.migratePipeline(this.pipeline);

    migrationRequest
        .subscribe(statusMessage => {
          if (statusMessage.success) {
            this.afterMigration(statusMessage, this.pipeline._id);
          } else {
            this.displayErrors(statusMessage.notifications[0]);
          }
        }, data => {
          this.displayErrors();
        });
  };

  afterMigration(statusMessage: Message, pipelineId?: string) {
    this.hide();
    // TODO: show dialog with statusMessagge
    // this.editorService.removePipelineFromCache().subscribe();
  }

  hide() {
    this.dialogRef.close();
  };

  onSelectedRelayStrategyChange(value: string) {
    this.selectedRelayStrategyVal = value;
  }

  onExecutionPolicyChange(value: any) {
    this.selectedPipelineExecutionPolicy = value;
  }

  isExecutinoPolicyDisabled() {
    return true;
  }
}
