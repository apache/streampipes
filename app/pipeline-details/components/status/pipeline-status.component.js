import {PipelineStatusController} from "./pipeline-status.controller";

export let PipelineStatusComponent = {
    templateUrl: 'app/pipeline-details/components/status/pipeline-status.tmpl.html',
    bindings: {
        pipeline: "<",
    },
    controller: PipelineStatusController,
    controllerAs: 'ctrl'
};
