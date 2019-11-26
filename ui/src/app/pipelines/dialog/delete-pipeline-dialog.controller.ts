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

export class DeletePipelineDialogController {

    $mdDialog: any;
    PipelineOperationsService: any;

    pipeline: any;
    refreshPipelines: any;
    RestApi: any;

    isInProgress: any = false;
    currentStatus: any;

    constructor($mdDialog, RestApi, pipeline, refreshPipelines) {
        this.$mdDialog = $mdDialog;
        this.RestApi = RestApi;
        this.pipeline = pipeline;
        this.refreshPipelines = refreshPipelines;
    }

    hide() {
        this.$mdDialog.hide();
    };

    cancel() {
        this.$mdDialog.cancel();
    };

    deletePipeline() {
        this.isInProgress = true;
        this.currentStatus = "Deleting pipeline...";
        this.RestApi.deleteOwnPipeline(this.pipeline._id)
            .then(data => {
                this.refreshPipelines();
                this.hide();
            });
    }

    stopAndDeletePipeline() {
        this.isInProgress = true;
        this.currentStatus = "Stopping pipeline...";
        this.RestApi.stopPipeline(this.pipeline._id)
            .then(data => {
               this.deletePipeline();
            }, data => {
                this.deletePipeline();
            });
    }


}

DeletePipelineDialogController.$inject = ['$mdDialog', 'RestApi', 'pipeline', 'refreshPipelines'];