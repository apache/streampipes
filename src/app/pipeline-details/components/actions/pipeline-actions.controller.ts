
export class PipelineActionsController {

    PipelineOperationsService: any;
    VersionService: any;
    starting: any;
    stopping: any;
    pipeline: any;
    $state: any;
    loadPipeline: any;

    constructor(PipelineOperationsService, $state, VersionService) {
        this.PipelineOperationsService = PipelineOperationsService;
        this.starting = false;
        this.stopping = false;
        this.$state = $state;
    }

    $onInit() {
        this.toggleRunningOperation = this.toggleRunningOperation.bind(this);
        this.reload = this.reload.bind(this);
        this.switchToPipelineView = this.switchToPipelineView.bind(this);
    }

    toggleRunningOperation(currentOperation) {
        if (currentOperation === 'starting') {
            this.starting = !(this.starting);
        } else {
            this.stopping = !(this.stopping);
        }
    }

    reload() {
        this.loadPipeline();
    }

    switchToPipelineView() {
        this.$state.go("streampipes.pipelines");
    }

}

PipelineActionsController.$inject = ['PipelineOperationsService', '$state', 'VersionService'];
