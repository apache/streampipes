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

import {Component, Inject, Input, OnInit} from "@angular/core";
import {DialogRef} from "../../../core-ui/dialog/base-dialog/dialog-ref";
import {Message, Pipeline} from "../../../core-model/gen/streampipes-model";
import {ObjectProvider} from "../../services/object-provider.service";
import {EditorService} from "../../services/editor.service";
import {PipelineService} from "../../../platform-services/apis/pipeline.service";
import {ShepherdService} from "../../../services/tour/shepherd.service";

@Component({
  selector: 'save-pipeline',
  templateUrl: './save-pipeline.component.html',
  styleUrls: ['./save-pipeline.component.scss']
})
export class SavePipelineComponent implements OnInit {

  pipelineCategories: any;
  startPipelineAfterStorage: any;
  updateMode: any;
  submitPipelineForm: any;
  TransitionService: any;

  @Input()
  pipeline: Pipeline;
  @Input()
  modificationMode: string;

  constructor(private editorService: EditorService,
              private dialogRef: DialogRef<SavePipelineComponent>,
              private objectProvider: ObjectProvider,
              private pipelineService: PipelineService,
              //TransitionService,
              @Inject("$state") private $state: any,
              private ShepherdService: ShepherdService) {
    this.pipelineCategories = [];
    this.updateMode = "update";
    //this.TransitionService = TransitionService;
    //this.ShepherdService = ShepherdService;
  }

  ngOnInit() {
    this.getPipelineCategories();
    if (this.ShepherdService.isTourActive()) {
      this.ShepherdService.trigger("enter-pipeline-name");
    }

  }

  triggerTutorial() {
    if (this.ShepherdService.isTourActive()) {
      this.ShepherdService.trigger("save-pipeline-dialog");
    }
  }

  displayErrors(data) {
    for (var i = 0, notification; notification = data.notifications[i]; i++) {
      //this.showToast("error", notification.title, notification.description);
    }
  }

  displaySuccess(data) {
    if (data.notifications.length > 0) {
      //this.showToast("success", data.notifications[0].title, data.notifications[0].description);
    }
  }

  getPipelineCategories() {
    this.pipelineService.getPipelineCategories().subscribe(pipelineCategories => {
          this.pipelineCategories = pipelineCategories;
        });
  };


  savePipelineName(switchTab) {
    if (this.pipeline.name == "") {
      //this.showToast("error", "Please enter a name for your pipeline");
      return false;
    }

    let storageRequest;

    if (this.modificationMode && this.updateMode === 'update') {
      storageRequest = this.pipelineService.updatePipeline(this.pipeline);
    } else {
      storageRequest = this.pipelineService.storePipeline(this.pipeline);
    }

    storageRequest
        .subscribe(statusMessage => {
          if (statusMessage.success) {
            this.afterStorage(statusMessage, switchTab);
          } else {
            this.displayErrors(statusMessage);
          }
        }, data => {
          //this.showToast("error", "Connection Error", "Could not fulfill request");
        });
  };

  afterStorage(data: Message, switchTab) {
    this.displaySuccess(data);
    this.hide();
    this.editorService.makePipelineAssemblyEmpty(true);
    this.editorService.removePipelineFromCache();
    if (this.ShepherdService.isTourActive()) {
      this.ShepherdService.hideCurrentStep();
    }
    if (switchTab && !this.startPipelineAfterStorage) {
      this.$state.go("streampipes.pipelines");
    }
    if (this.startPipelineAfterStorage) {
      this.$state.go("streampipes.pipelines", {pipeline: data.notifications[1].description});
    }
  }

  hide() {
    this.dialogRef.close();
    //this.$mdDialog.hide();
  };
}