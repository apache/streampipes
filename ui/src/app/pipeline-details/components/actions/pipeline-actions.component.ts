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

import {Component, EventEmitter, Inject, Input, OnInit, Output} from "@angular/core";
import {PipelineOperationsService} from "../../../pipelines/services/pipeline-operations.service";
import {Pipeline} from "../../../core-model/gen/streampipes-model";
import {Router} from "@angular/router";

@Component({
    selector: 'pipeline-actions',
    templateUrl: './pipeline-actions.component.html',
})
export class PipelineActionsComponent implements OnInit {

    starting: boolean = false;
    stopping: boolean = false;

    @Input()
    pipeline: Pipeline;

    @Output()
    reloadPipelineEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();

    constructor(public pipelineOperationsService: PipelineOperationsService,
                private Router: Router) {
    }

    ngOnInit() {
        this.toggleRunningOperation = this.toggleRunningOperation.bind(this);
        this.switchToPipelineView = this.switchToPipelineView.bind(this);
    }

    toggleRunningOperation(currentOperation) {
        if (currentOperation === 'starting') {
            this.starting = !(this.starting);
        } else {
            this.stopping = !(this.stopping);
        }
    }

    switchToPipelineView() {
        this.Router.navigate(["pipelines"]);
    }

    startPipeline() {
        this.pipelineOperationsService.startPipeline(this.pipeline._id, this.toggleRunningOperation, this.reloadPipelineEmitter)
    }

    stopPipeline() {
        this.pipelineOperationsService.stopPipeline(this.pipeline._id, this.toggleRunningOperation, this.reloadPipelineEmitter)
    }

}
