import {PipelineDetailsController} from "./pipeline-details.controller";

export let PipelineDetailsComponent = {
    templateUrl: 'pipeline-details.tmpl.html',
    bindings: {
        pipeline: "<",
        refreshPipelines: "&"
    },
    controller: PipelineDetailsController,
    controllerAs: 'ctrl'
};
