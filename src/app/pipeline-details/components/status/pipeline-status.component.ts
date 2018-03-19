import {PipelineStatusController} from "./pipeline-status.controller";

export let PipelineStatusComponent = {
    templateUrl: 'pipeline-status.tmpl.html',
    bindings: {
        pipeline: "<",
    },
    controller: PipelineStatusController,
    controllerAs: 'ctrl'
};
