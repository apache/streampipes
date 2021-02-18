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

import {ShepherdService} from "../../services/tour/shepherd.service";
import {EventEmitter, Inject, Injectable} from "@angular/core";
import {PipelineService} from "../../platform-services/apis/pipeline.service";
import {PanelType} from "../../core-ui/dialog/base-dialog/base-dialog.model";
import {DialogService} from "../../core-ui/dialog/base-dialog/base-dialog.service";
import {PipelineStatusDialogComponent} from "../dialog/pipeline-status/pipeline-status-dialog.component";
import {Pipeline, PipelineOperationStatus} from "../../core-model/gen/streampipes-model";
import {DeletePipelineDialogComponent} from "../dialog/delete-pipeline/delete-pipeline-dialog.component";
import {DialogRef} from "../../core-ui/dialog/base-dialog/dialog-ref";
import {Router} from "@angular/router";
import {MigratePipelineProcessorsComponent} from "../../editor/dialog/migrate-pipeline-processors/migrate-pipeline-processors.component";

declare const require: any;

@Injectable()
export class PipelineOperationsService {

  starting: any;
  stopping: any;

  constructor(
      private ShepherdService: ShepherdService,
      private PipelineService: PipelineService,
      private DialogService: DialogService,
      private Router: Router) {
  }

  startPipeline(pipelineId: string,
                refreshPipelinesEmitter: EventEmitter<boolean>,
                toggleRunningOperation?,) {
    if (toggleRunningOperation) {
      toggleRunningOperation('starting');
    }
    this.PipelineService.startPipeline(pipelineId).subscribe(msg => {
      refreshPipelinesEmitter.emit(true);
      if (toggleRunningOperation) {
        toggleRunningOperation('starting');
      }
      if (this.ShepherdService.isTourActive()) {
        this.ShepherdService.trigger("pipeline-started");
      }
      this.showDialog(msg);
    }, error => {
      this.showDialog({title: "Network Error", success: false, pipelineId: undefined, pipelineName: undefined, elementStatus: []})
    });
  };

  stopPipeline(pipelineId: string, refreshPipelinesEmitter: EventEmitter<boolean>, toggleRunningOperation?) {
    toggleRunningOperation('stopping');
    this.PipelineService.stopPipeline(pipelineId).subscribe(msg => {
      refreshPipelinesEmitter.emit(true);
      toggleRunningOperation('stopping');
      this.showDialog(msg);
    }, error => {
      this.showDialog({title: "Network Error", success: false, pipelineId: undefined, pipelineName: undefined, elementStatus: []})
    });
  };

  showDeleteDialog(pipeline: Pipeline, refreshPipelinesEmitter: EventEmitter<boolean>, switchToPipelineView?: any) {
    let dialogRef: DialogRef<DeletePipelineDialogComponent> = this.DialogService.open(DeletePipelineDialogComponent, {
      panelType: PanelType.STANDARD_PANEL,
      title: "Delete Pipeline",
      width: "70vw",
      data: {
        "pipeline": pipeline,
      }
    });

    dialogRef.afterClosed().subscribe(data => {
      if (data) {
        if (!switchToPipelineView) {
          refreshPipelinesEmitter.emit(true);
        } else {
          switchToPipelineView();
        }
      }
    })
  };

  showDialog(data: PipelineOperationStatus) {
    this.DialogService.open(PipelineStatusDialogComponent, {
      panelType: PanelType.STANDARD_PANEL,
      title: "Pipeline Status",
      width: "70vw",
      data: {
        "pipelineOperationStatus": data
      }
    });
  };

  showPipelineInEditor(id) {
    this.Router.navigate(["editor"], { queryParams: { pipeline: id }});
  }

  showPipelineDetails(id) {
    this.Router.navigate(["pipeline-details"], { queryParams: { pipeline: id }});
  }

  modifyPipeline(pipeline) {
    this.showPipelineInEditor(pipeline);
  }

  showLogs(id) {
    //this.$state.go("streampipes.pipelinelogs", {pipeline: id});
  }

  migratePipelineProcessors(pipelineId: string) {
    this.PipelineService.getPipelineById(pipelineId).subscribe(pipeline => {
      this.DialogService.open(MigratePipelineProcessorsComponent,{
        panelType: PanelType.SLIDE_IN_PANEL,
        title: "Live-Migrate pipeline processors",
        data: {
          "pipeline": pipeline
        }
      });
    })
  }
}
