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

import { Component, Input, OnInit } from '@angular/core';
import { DialogRef } from '@streampipes/shared-ui';
import { Pipeline, PipelineService } from '@streampipes/platform-services';

@Component({
    selector: 'sp-start-all-pipelines-dialog',
    templateUrl: './start-all-pipelines-dialog.component.html',
    styleUrls: ['./start-all-pipelines-dialog.component.scss'],
})
export class StartAllPipelinesDialogComponent implements OnInit {
    @Input()
    pipelines: Pipeline[];

    @Input()
    activeCategory: string;

    pipelinesToModify: Pipeline[];
    installationStatus: any;
    installationFinished: boolean;
    page: string;
    nextButton: string;
    installationRunning: boolean;

    @Input()
    action: boolean;

    constructor(
        private dialogRef: DialogRef<StartAllPipelinesDialogComponent>,
        private pipelineService: PipelineService,
    ) {
        this.pipelinesToModify = [];
        this.installationStatus = [];
        this.installationFinished = false;
        this.page = 'preview';
        this.nextButton = 'Next';
        this.installationRunning = false;
    }

    ngOnInit() {
        this.getPipelinesToModify();
        if (this.pipelinesToModify.length === 0) {
            this.nextButton = 'Close';
            this.page = 'installation';
        }
    }

    close(refreshPipelines: boolean) {
        this.dialogRef.close(refreshPipelines);
    }

    next() {
        if (this.page === 'installation') {
            this.close(true);
        } else {
            this.page = 'installation';
            this.initiateInstallation(this.pipelinesToModify[0], 0);
        }
    }

    getPipelinesToModify() {
        this.pipelines.forEach(pipeline => {
            if (
                pipeline.running !== this.action &&
                this.hasCategory(pipeline)
            ) {
                this.pipelinesToModify.push(pipeline);
            }
        });
    }

    hasCategory(pipeline: Pipeline) {
        let categoryPresent = false;
        if (!this.activeCategory) {
            return true;
        } else {
            pipeline.pipelineCategories.forEach(category => {
                if (category === this.activeCategory) {
                    categoryPresent = true;
                }
            });
            return categoryPresent;
        }
    }

    initiateInstallation(pipeline, index) {
        this.installationRunning = true;
        this.installationStatus.push({
            name: pipeline.name,
            id: index,
            status: 'waiting',
        });
        if (this.action) {
            this.startPipeline(pipeline, index);
        } else {
            this.stopPipeline(pipeline, index);
        }
    }

    startPipeline(pipeline, index) {
        this.pipelineService
            .startPipeline(pipeline._id)
            .subscribe(
                data => {
                    this.installationStatus[index].status = data.success
                        ? 'success'
                        : 'error';
                },
                data => {
                    this.installationStatus[index].status = 'error';
                },
            )
            .add(() => {
                if (index < this.pipelinesToModify.length - 1) {
                    index++;
                    this.initiateInstallation(
                        this.pipelinesToModify[index],
                        index,
                    );
                } else {
                    this.nextButton = 'Close';
                    this.installationRunning = false;
                }
            });
    }

    stopPipeline(pipeline, index) {
        this.pipelineService
            .stopPipeline(pipeline._id)
            .subscribe(
                data => {
                    this.installationStatus[index].status = data.success
                        ? 'success'
                        : 'error';
                },
                data => {
                    this.installationStatus[index].status = 'error';
                },
            )
            .add(() => {
                if (index < this.pipelinesToModify.length - 1) {
                    index++;
                    this.initiateInstallation(
                        this.pipelinesToModify[index],
                        index,
                    );
                } else {
                    this.nextButton = 'Close';
                    this.installationRunning = false;
                }
            });
    }
}
