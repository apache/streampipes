import * as angular from 'angular';
import {PipelineStatusDialogController} from "../dialog/pipeline-status-dialog.controller";


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
            .success(data => {
                this.showDialog(data);
                refreshPipelines();
                toggleRunningOperation('starting');
                if (this.ShepherdService.isTourActive()) {
                    this.ShepherdService.trigger("pipeline-started");
                }
            })
            .error(data => {
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
            .success(data => {
                toggleRunningOperation('stopping');
                this.showDialog(data);
                refreshPipelines();
            })
            .error(data => {
                toggleRunningOperation('stopping');
                this.showDialog({
                    notifications: [{
                        title: "Network Error",
                        description: "Please check your Network."
                    }]
                });

            });
    };

    deletePipeline(ev, pipelineId, refreshPipelines) {
        var confirm = this.$mdDialog.confirm()
            .title('Delete pipeline?')
            .textContent('The pipeline will be removed. ')
            .targetEvent(ev)
            .ok('Delete')
            .cancel('Cancel');
        this.$mdDialog.show(confirm).then(() => {
            this.RestApi.deleteOwnPipeline(pipelineId)
                .success(data => {
                    refreshPipelines();
                })
                .error(function (data) {
                    console.log(data);
                })
        });
    };

    showDialog(data) {
        this.$mdDialog.show({
            controller: PipelineStatusDialogController,
            controllerAs: 'ctrl',
            templateUrl: '../dialog/pipeline-status-dialog.tmpl.html',
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