export class PipelineStatusController {

    pipelineStatus: any;
    RestApi: any;
    pipeline: any;

    constructor(RestApi) {
        this.pipelineStatus = [];
        this.RestApi = RestApi;
    }

    $onInit() {
        this.getPipelineStatus();
    }

    getPipelineStatus() {
        this.RestApi.getPipelineStatusById(this.pipeline._id)
            .then(msg => {
                this.pipelineStatus = msg.data;
            });
    }

}

PipelineStatusController.$inject = ['RestApi'];