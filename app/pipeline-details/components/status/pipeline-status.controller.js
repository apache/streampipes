export class PipelineStatusController {

    constructor(restApi) {
        this.pipelineStatus = [];
        this.restApi = restApi;
        this.getPipelineStatus();
    }

    getPipelineStatus() {
        this.restApi.getPipelineStatusById(this.pipeline._id)
            .success(data => {
                this.pipelineStatus = data;
            })
    }

}

PipelineStatusController.$inject = ['restApi'];