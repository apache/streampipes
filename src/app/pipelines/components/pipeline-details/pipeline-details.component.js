import {PipelineDetailsController} from "./pipeline-details.controller";

export let PipelineDetailsComponent = {
    templateUrl: 'app/pipelines/components/pipeline-details/pipeline-details.tmpl.html',
    bindings: {
        pipeline: "<",
        refreshPipelines: "&"
    },
    controller: PipelineDetailsController,
    controllerAs: 'ctrl'
};
