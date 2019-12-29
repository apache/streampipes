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
import {PipelineStatusDialogController} from "../dialog/pipeline-status-dialog.controller";
import {DeletePipelineDialogController} from "../dialog/delete-pipeline-dialog.controller";
declare const require: any;

export class PipelineOperationsService {

    $mdDialog: any;
    RestApi: any;
    $state: any;
    starting: any;
    stopping: any;
    ShepherdService: any;

    constructor($mdDialog, RestApi, $state, ShepherdService) {
        this.$mdDialog = $mdDialog;
        this.RestApi = RestApi;
        this.$state = $state;
        this.ShepherdService = ShepherdService;
    }

    startPipeline(pipelineId, toggleRunningOperation, refreshPipelines) {
        toggleRunningOperation('starting');
        this.RestApi.startPipeline(pipelineId)
            .then(msg => {
                let data = msg.data;
                this.showDialog(data);
                refreshPipelines();
                toggleRunningOperation('starting');
                if (this.ShepherdService.isTourActive()) {
                    this.ShepherdService.trigger("pipeline-started");
                }
            }, error => {
                toggleRunningOperation('starting');
                this.showDialog({
                    notifications: [{
                        title: "Network Error",
                        description: "Please check your Network."
                    }]
                });
            });
    };

    stopPipeline(pipelineId, toggleRunningOperation, refreshPipelines) {
        toggleRunningOperation('stopping');
        this.RestApi.stopPipeline(pipelineId)
            .then(msg => {
                let data = msg.data;
                toggleRunningOperation('stopping');
                this.showDialog(data);
                refreshPipelines();
            }, error => {
                toggleRunningOperation('stopping');
                this.showDialog({
                    notifications: [{
                        title: "Network Error",
                        description: "Please check your Network."
                    }]
                });

            });
    };

    showDeleteDialog(pipeline, refreshPipelines) {
        this.$mdDialog.show({
            controller: DeletePipelineDialogController,
            controllerAs: 'ctrl',
            template: require('../dialog/delete-pipeline-dialog.tmpl.html'),
            parent: angular.element(document.body),
            clickOutsideToClose: false,
            locals: {
                pipeline: pipeline,
                refreshPipelines: refreshPipelines
            },
            bindToController: true
        })
    };

    showDialog(data) {
        this.$mdDialog.show({
            controller: PipelineStatusDialogController,
            controllerAs: 'ctrl',
            template: require('../dialog/pipeline-status-dialog.tmpl.html'),
            parent: angular.element(document.body),
            clickOutsideToClose: false,
            locals: {
                data: data
            },
            bindToController: true
        })
    };

    showPipelineInEditor(id) {
        this.$state.go("streampipes.editor", {pipeline: id});
    }

    showPipelineDetails(id) {
        this.$state.go("streampipes.pipelineDetails", {pipeline: id});
    }

    modifyPipeline(pipeline) {
        this.showPipelineInEditor(pipeline);
    }

    showLogs(id) {
        this.$state.go("streampipes.pipelinelogs", {pipeline: id});
    }
}

PipelineOperationsService.$inject = ['$mdDialog', 'RestApi', '$state', 'ShepherdService'];