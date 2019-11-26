/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import * as angular from 'angular';

export class StartAllPipelinesController {

    $mdDialog: any;
    RestApi: any;
    pipelines: any;
    activeCategory: any;
    pipelinesToModify: any;
    installationStatus: any;
    installationFinished: any;
    page: any;
    nextButton: any;
    installationRunning: any;
    action: any;
    pipeline: any;
    refreshPipelines: any;

    constructor($mdDialog, RestApi, pipelines, action, activeCategory, refreshPipelines) {
        this.$mdDialog = $mdDialog;
        this.RestApi = RestApi;
        this.pipelines = pipelines;
        this.activeCategory = activeCategory;
        this.pipelinesToModify = [];
        this.installationStatus = [];
        this.installationFinished = false;
        this.page = "preview";
        this.nextButton = "Next";
        this.installationRunning = false;
        this.action = action;
        this.refreshPipelines = refreshPipelines;
    }

    $onInit() {
        this.getPipelinesToModify();
        if (this.pipelinesToModify.length == 0) {
            this.nextButton = "Close";
            this.page = "installation";
        }
    }

    hide() {
        this.$mdDialog.hide();
    };

    cancel() {
        this.$mdDialog.cancel();
    };

    next() {
        if (this.page == "installation") {
            this.cancel();
        } else {
            this.page = "installation";
            this.initiateInstallation(this.pipelinesToModify[0], 0);
        }
    }

    getPipelinesToModify() {
        angular.forEach(this.pipelines, pipeline => {
            if (pipeline.running != this.action && this.hasCategory(pipeline)) {
                this.pipelinesToModify.push(pipeline);
            }
        });
    }

    hasCategory(pipeline) {
        var categoryPresent = false;
        if (this.activeCategory == "") return true;
        else {
            angular.forEach(this.pipeline.pipelineCategories, category => {
                if (category == this.activeCategory) {
                    categoryPresent = true;
                }
            });
            return categoryPresent;
        }
    }

    initiateInstallation(pipeline, index) {
        this.installationRunning = true;
        this.installationStatus.push({"name": pipeline.name, "id": index, "status": "waiting"});
        if (this.action) {
            this.startPipeline(pipeline, index);
        } else {
            this.stopPipeline(pipeline, index);
        }
    }

    startPipeline(pipeline, index) {
        this.RestApi.startPipeline(pipeline._id)
            .then(msg => {
                let data = msg.data;
                if (data.success) {
                    this.installationStatus[index].status = "success";
                } else {
                    this.installationStatus[index].status = "error";
                }
            }, data => {
                this.installationStatus[index].status = "error";
            })
            .then(() => {
                if (index < this.pipelinesToModify.length - 1) {
                    index++;
                    this.initiateInstallation(this.pipelinesToModify[index], index);
                } else {
                    this.refreshPipelines();
                    this.nextButton = "Close";
                    this.installationRunning = false;
                }
            });
    }
    

    stopPipeline(pipeline, index) {
        this.RestApi.stopPipeline(pipeline._id)
            .then(msg => {
                let data = msg.data;
                if (data.success) {
                    this.installationStatus[index].status = "success";
                } else {
                    this.installationStatus[index].status = "error";
                }
            }, data => {
                this.installationStatus[index].status = "error";
            })
            .then(() => {
                if (index < this.pipelinesToModify.length - 1) {
                    index++;
                    this.initiateInstallation(this.pipelinesToModify[index], index);
                } else {
                    this.refreshPipelines();
                    this.nextButton = "Close";
                    this.installationRunning = false;
                }
            });
    }
}

StartAllPipelinesController.$inject = ['$mdDialog',
    'RestApi',
    'pipelines',
    'action',
    'activeCategory',
    'refreshPipelines'];