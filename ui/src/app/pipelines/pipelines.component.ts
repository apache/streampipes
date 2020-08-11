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
import * as FileSaver from 'file-saver';
import {Component, Inject, OnInit} from "@angular/core";
import {PipelineService} from "../platform-services/apis/pipeline.service";
import {Pipeline, PipelineCategory} from '../core-model/gen/streampipes-model';
import {DialogService} from "../core-ui/dialog/base-dialog/base-dialog.service";
import {PanelType} from "../core-ui/dialog/base-dialog/base-dialog.model";
import {ImportPipelineDialogComponent} from "./dialog/import-pipeline/import-pipeline-dialog.component";
import {DialogRef} from "../core-ui/dialog/base-dialog/dialog-ref";
import {StartAllPipelinesDialogComponent} from "./dialog/start-all-pipelines/start-all-pipelines-dialog.component";
import {PipelineCategoriesDialogComponent} from "./dialog/pipeline-categories/pipeline-categories-dialog.component";
import {zip} from "rxjs";
import {ActivatedRoute} from "@angular/router";

declare const jsPlumb: any;
declare const require: any;

@Component({
    selector: 'pipelines',
    templateUrl: './pipelines.component.html',
    styleUrls: ['./pipelines.component.scss']
})
export class PipelinesComponent implements OnInit {

    pipeline: Pipeline;
    pipelines: Array<Pipeline> = [];
    systemPipelines: Array<Pipeline> = [];
    starting: boolean;
    stopping: boolean;
    pipelineCategories: Array<PipelineCategory>;
    activeCategoryId: string;
    pipelineIdToStart: string;

    pipelineToStart: Pipeline;
    systemPipelineToStart: Pipeline;

    pipelinesReady: boolean = false;

    selectedCategoryIndex: number = 0;

    constructor(private pipelineService: PipelineService,
                private DialogService: DialogService,
                private ActivatedRoute: ActivatedRoute) {
        this.pipelineCategories = [];
        this.starting = false;
        this.stopping = false;
    }

    ngOnInit() {
        this.ActivatedRoute.queryParams.subscribe(params => {
            if (params['pipeline']) {
                this.pipelineToStart = params['pipeline'];
            }
            this.getPipelineCategories();
            this.getPipelines();
        });
    }

    setSelectedTab(index) {
        if (index == 0) {
            this.activeCategoryId = undefined;
        } else {
            this.activeCategoryId = this.pipelineCategories[index - 1]._id;
        }
    }

    exportPipelines() {
        let blob = new Blob([JSON.stringify(this.pipelines)], {type: "application/json"})
        FileSaver.saveAs(blob, "pipelines.json");
    }


    getPipelines() {
        this.pipelines = [];
        zip(this.pipelineService.getOwnPipelines(), this.pipelineService.getSystemPipelines()).subscribe(allPipelines => {
            this.pipelines = allPipelines[0];
            this.systemPipelines = allPipelines[1];
            this.checkForImmediateStart(allPipelines);
            this.pipelinesReady = true;
        });
    };

    checkForImmediateStart(allPipelines: Pipeline[][]) {
        this.pipelineToStart = undefined;
        allPipelines.forEach((pipelines , index) => {
           pipelines.forEach(pipeline => {
               if (pipeline._id == this.pipelineIdToStart) {
                   if (index == 0) {
                       this.pipelineToStart = pipeline;
                   } else {
                       this.systemPipelineToStart = pipeline;
                   }
               }
           })
        });
        this.pipelineIdToStart = undefined;
    }

    getPipelineCategories() {
        this.pipelineService.getPipelineCategories()
            .subscribe(pipelineCategories => {
                this.pipelineCategories = pipelineCategories;
            });
    };

    activeClass(pipeline) {
        return 'active-pipeline';
    }

    checkCurrentSelectionStatus(status) {
        var active = true;
        angular.forEach(this.pipelines, pipeline => {
            if (!this.activeCategoryId || pipeline.pipelineCategories.some(pc => pc === this.activeCategoryId)) {
                if (pipeline.running == status) {
                    active = false;
                }
            }
        });

        return active;
    }

    openImportPipelinesDialog() {
        let dialogRef: DialogRef<ImportPipelineDialogComponent> = this.DialogService.open(ImportPipelineDialogComponent, {
            panelType: PanelType.STANDARD_PANEL,
            title: "Import Pipeline",
            width: "70vw",
            data: {
                "pipelines": this.pipelines
            }
        });
        dialogRef.afterClosed().subscribe(data => {
            if (data) {
                this.refreshPipelines();
            }
        })
    }

    startAllPipelines(action) {
        let dialogRef: DialogRef<StartAllPipelinesDialogComponent> = this.DialogService.open(StartAllPipelinesDialogComponent, {
            panelType: PanelType.STANDARD_PANEL,
            title: (action ? "Start" : "Stop") + " all pipelines",
            width: "70vw",
            data: {
                "pipelines": this.pipelines,
                "action": action,
                "activeCategoryId": this.activeCategoryId
            }
        });

        dialogRef.afterClosed().subscribe(data => {
           if (data) {
               this.refreshPipelines();
           }
        });
    }

    showPipelineCategoriesDialog() {
        let dialogRef: DialogRef<PipelineCategoriesDialogComponent> = this.DialogService.open(PipelineCategoriesDialogComponent, {
            panelType: PanelType.STANDARD_PANEL,
            title: "Pipeline Categories",
            width: "70vw",
            data: {
                "pipelines": this.pipelines,
                "systemPipelines": this.systemPipelines
            }
        });

        dialogRef.afterClosed().subscribe(data => {
            this.getPipelineCategories();
            this.refreshPipelines();
        });
    };

    refreshPipelines() {
        this.getPipelines();
    }

    showPipeline(pipeline) {
        pipeline.display = !pipeline.display;
    }
}