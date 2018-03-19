export class PipelineStatusController {

    pipelineStatus: any;
    RestApi: any;
    pipeline: any;

    constructor(RestApi) {
        this.pipelineStatus = [];
        this.RestApi = RestApi;
        this.getPipelineStatus();
    }

    getPipelineStatus() {
        this.RestApi.getPipelineStatusById(this.pipeline._id)
            .success(data => {
                this.pipelineStatus = data;
            })
    }

}

PipelineStatusController.$inject = ['RestApi'];