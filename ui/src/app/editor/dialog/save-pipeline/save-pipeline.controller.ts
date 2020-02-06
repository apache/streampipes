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

import {RestApi} from "../../../services/rest-api.service";

export class SavePipelineController {

    RestApi: RestApi;
    $mdToast: any;
    $state: any;
    $mdDialog: any;
    pipelineCategories: any;
    pipeline: any;
    ObjectProvider: any;
    startPipelineAfterStorage: any;
    modificationMode: any;
    updateMode: any;
    submitPipelineForm: any;
    TransitionService: any;
    ShepherdService: any;

    constructor($mdDialog,
                $state,
                RestApi,
                $mdToast,
                ObjectProvider,
                pipeline,
                modificationMode,
                TransitionService,
                ShepherdService) {
        this.RestApi = RestApi;
        this.$mdToast = $mdToast;
        this.$state = $state;
        this.$mdDialog = $mdDialog;
        this.pipelineCategories = [];
        this.pipeline = pipeline;
        this.ObjectProvider = ObjectProvider;
        this.modificationMode = modificationMode;
        this.updateMode = "update";
        this.TransitionService = TransitionService;
        this.ShepherdService = ShepherdService;
    }

    $onInit() {
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
            this.showToast("error", notification.description, notification.title);
        }
    }

    displaySuccess(data) {
        if (data.notifications.length > 0) {
            this.showToast("success", data.notifications[0].description, data.notifications[0].title);
        }
    }

    getPipelineCategories() {
        this.RestApi.getPipelineCategories()
            .then(pipelineCategories => {
                this.pipelineCategories = pipelineCategories.data;
            });

    };


    savePipelineName(switchTab) {
        if (this.pipeline.name == "") {
            this.showToast("error", "Please enter a name for your pipeline");
            return false;
        }

        let storageRequest;

        if (this.modificationMode && this.updateMode === 'update') {
            storageRequest = this.RestApi.updatePipeline(this.pipeline);
        } else {
            storageRequest = this.RestApi.storePipeline(this.pipeline);
        }

        storageRequest
            .then(msg => {
                let data = msg.data;
                if (data.success) {
                    this.afterStorage(data, switchTab);
                } else {
                    this.displayErrors(data);
                }
            }, data => {
                this.showToast("error", "Could not fulfill request", "Connection Error");
            });
    };

    afterStorage(data, switchTab) {
        this.displaySuccess(data);
        this.hide();
        this.TransitionService.makePipelineAssemblyEmpty(true);
        this.RestApi.removePipelineFromCache();
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
        this.$mdDialog.hide();
    };

    showToast(type, title, description?) {
        this.$mdToast.show(
            this.$mdToast.simple()
                .textContent(title)
                .position("top right")
                .hideDelay(3000)
        );
    }
}

SavePipelineController.$inject = ['$mdDialog', '$state', 'RestApi', '$mdToast', 'ObjectProvider', 'pipeline', 'modificationMode', 'TransitionService', 'ShepherdService'];
