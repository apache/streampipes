import {PipelineActionsController} from "./pipeline-actions.controller";

export let PipelineActionsComponent = {
    templateUrl: 'pipeline-actions.tmpl.html',
    bindings: {
        pipeline: "<"
    },
    controller: PipelineActionsController,
    controllerAs: 'ctrl'
};
