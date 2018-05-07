import {PipelineDetailsController} from "./pipeline-details.controller";

export let PipelineDetailsComponent = {
    templateUrl: 'pipeline-details.tmpl.html',
    bindings: {
        pipelines: "<",
        refreshPipelines: "&",
        activeCategory: "<"
    },
    controller: PipelineDetailsController,
    controllerAs: 'ctrl'
};
