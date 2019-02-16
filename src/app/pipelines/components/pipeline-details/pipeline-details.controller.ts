import * as angular from "angular";

export class PipelineDetailsController {

    pipelines: any;
    starting: any;
    stopping: any;
    refreshPipelines: any;
    PipelineOperationsService: any;
    VersionService: any;
    activeCategory: any;
    dtOptions = {paging: false, searching: false,   "order": [], "columns": [
            { "orderable": false },
            null,
            null,
            { "orderable": false },
        ]};

    constructor(PipelineOperationsService, VersionService) {
        this.PipelineOperationsService = PipelineOperationsService;
        this.VersionService = VersionService;
        this.starting = false;
        this.stopping = false;
    }

    $onInit() {
        this.toggleRunningOperation = this.toggleRunningOperation.bind(this);

        angular.forEach(this.pipelines, pipeline => {
            if (pipeline.immediateStart) {
                if (!pipeline.running) {
                    this.PipelineOperationsService.startPipeline(pipeline._id, this.toggleRunningOperation, this.refreshPipelines);
                }
            }
        });
    }

    toggleRunningOperation(currentOperation) {
        if (currentOperation === 'starting') {
            this.starting = !(this.starting);
        } else {
            this.stopping = !(this.stopping);
        }
    }

}

PipelineDetailsController.$inject = ['PipelineOperationsService', 'VersionService'];