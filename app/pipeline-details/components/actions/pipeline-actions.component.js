import {PipelineActionsController} from "./pipeline-actions.controller";

export let PipelineActionsComponent = {
    templateUrl: 'app/pipeline-details/components/actions/pipeline-actions.tmpl.html',
    bindings: {
        pipeline: "<"
    },
    controller: PipelineActionsController,
    controllerAs: 'ctrl'
};
