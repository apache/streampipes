import {PipelineActionsController} from "./pipeline-actions.controller";

export let PipelineActionsComponent = {
    templateUrl: 'pipeline-actions.tmpl.html',
    bindings: {
        pipeline: "=",
        loadPipeline: "&"
    },
    controller: PipelineActionsController,
    controllerAs: 'ctrl'
};
