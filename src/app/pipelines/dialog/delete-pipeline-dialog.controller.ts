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
            .success(data => {
                this.refreshPipelines();
                this.hide();
            })
            .error(function (data) {
                console.log(data);
            })
    }

    stopAndDeletePipeline() {
        this.isInProgress = true;
        this.currentStatus = "Stopping pipeline...";
        this.RestApi.stopPipeline(this.pipeline._id)
            .success(data => {
               this.deletePipeline();
            })
            .error(data => {
                this.deletePipeline();
            });
    }


}

DeletePipelineDialogController.$inject = ['$mdDialog', 'RestApi', 'pipeline', 'refreshPipelines'];