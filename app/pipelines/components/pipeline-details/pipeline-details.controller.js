import {PipelineStatusDialogController} from '../../dialog/pipeline-status-dialog.controller';

export class PipelineDetailsController {

    constructor(restApi, $mdDialog, $rootScope) {
        this.restApi = restApi;
        this.$mdDialog = $mdDialog;
        this.$rootScope = $rootScope;

        if (this.pipeline.immediateStart) {
            if (!this.pipeline.running) {
                this.startPipeline(this.pipeline._id);
            }
        }
    }

    startPipeline(pipelineId) {
        this.starting = true;
        this.restApi.startPipeline(pipelineId)
            .success(data => {
                this.showDialog(data);
                this.refreshPipelines();
                this.starting = false;

            })
            .error(data => {
                this.starting = false;
                this.showDialog({
                    notifications: [{
                        title: "Network Error",
                        description: "Please check your Network."
                    }]
                });

            });
    };

    stopPipeline(pipelineId) {
        this.stopping = true;
        this.restApi.stopPipeline(pipelineId)
            .success(data => {
                this.stopping = false;
                this.showDialog(data);
                this.refreshPipelines();
            })
            .error(data => {
                console.log(data);
                this.stopping = false;
                this.showDialog({
                    notifications: [{
                        title: "Network Error",
                        description: "Please check your Network."
                    }]
                });

            });
    };

    deletePipeline(ev, pipelineId) {
        var confirm = this.$mdDialog.confirm()
            .title('Delete pipeline?')
            .textContent('The pipeline will be removed. ')
            .targetEvent(ev)
            .ok('Delete')
            .cancel('Cancel');
        this.$mdDialog.show(confirm).then(() => {
            this.restApi.deleteOwnPipeline(pipelineId)
                .success(data => {
                    this.refreshPipelines();
                })
                .error(function (data) {
                    console.log(data);
                })
        }, function () {

        });
    };

    showDialog(data) {
        this.$mdDialog.show({
            controller: PipelineStatusDialogController,
            controllerAs: 'ctrl',
            templateUrl: 'app/pipelines/dialog/pipeline-status-dialog.tmpl.html',
            parent: angular.element(document.body),
            clickOutsideToClose: false,
            locals: {
                data: data
            },
            bindToController: true
        })
    };

    showPipelineInEditor(id) {
        this.$rootScope.go("streampipes.editor", {pipeline: id});
    }

    showPipelineDetails(id) {
        this.$rootScope.go("streampipes.pipelineDetails", {pipeline: id});
    }

    modifyPipeline(pipeline) {
        this.showPipelineInEditor(pipeline);
    }

}

PipelineDetailsController.$inject = ['restApi', '$mdDialog', '$rootScope'];