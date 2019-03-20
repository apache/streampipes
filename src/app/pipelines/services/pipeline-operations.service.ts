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