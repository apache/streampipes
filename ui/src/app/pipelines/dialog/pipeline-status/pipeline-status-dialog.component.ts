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

import { DialogRef } from '@streampipes/shared-ui';
import {
    PipelineOperationStatus,
    PipelineService,
} from '@streampipes/platform-services';
import { Component, Input, OnInit } from '@angular/core';
import { PipelineAction } from '../../model/pipeline-model';
import { ShepherdService } from '../../../services/tour/shepherd.service';

@Component({
    selector: 'sp-pipeline-status-dialog',
    templateUrl: './pipeline-status-dialog.component.html',
    styleUrls: ['./pipeline-status-dialog.component.scss'],
})
export class PipelineStatusDialogComponent implements OnInit {
    operationInProgress = true;
    forceStopActive = false;
    pipelineOperationStatus: PipelineOperationStatus;

    @Input()
    pipelineId: string;

    @Input()
    action: PipelineAction;

    constructor(
        private dialogRef: DialogRef<PipelineStatusDialogComponent>,
        private pipelineService: PipelineService,
        private shepherdService: ShepherdService,
    ) {}

    ngOnInit(): void {
        if (this.action === PipelineAction.Start) {
            this.startPipeline();
        } else {
            this.stopPipeline();
        }
    }

    close() {
        this.dialogRef.close();
    }

    startPipeline() {
        this.pipelineService.startPipeline(this.pipelineId).subscribe(
            msg => {
                this.pipelineOperationStatus = msg;
                this.operationInProgress = false;
                if (this.shepherdService.isTourActive()) {
                    this.shepherdService.trigger('pipeline-started');
                }
            },
            error => {
                this.operationInProgress = false;
                this.pipelineOperationStatus = {
                    title: 'Network Error',
                    success: false,
                    pipelineId: undefined,
                    pipelineName: undefined,
                    elementStatus: [],
                };
            },
        );
    }

    stopPipeline() {
        this.pipelineService.stopPipeline(this.pipelineId).subscribe(
            msg => {
                this.pipelineOperationStatus = msg;
                this.operationInProgress = false;
            },
            error => {
                this.operationInProgress = false;
                this.pipelineOperationStatus = {
                    title: 'Network Error',
                    success: false,
                    pipelineId: undefined,
                    pipelineName: undefined,
                    elementStatus: [],
                };
            },
        );
    }

    forceStopPipeline() {
        this.operationInProgress = true;
        this.forceStopActive = true;
        this.pipelineService.stopPipeline(this.pipelineId, true).subscribe(
            msg => {
                this.pipelineOperationStatus = msg;
                this.operationInProgress = false;
            },
            error => {
                this.operationInProgress = false;
                this.pipelineOperationStatus = {
                    title: 'Network Error',
                    success: false,
                    pipelineId: undefined,
                    pipelineName: undefined,
                    elementStatus: [],
                };
            },
        );
    }
}
