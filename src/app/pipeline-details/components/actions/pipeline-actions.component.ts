import {PipelineActionsController} from "./pipeline-actions.controller";
declare const require: any;

export let PipelineActionsComponent = {
    template: require('./pipeline-actions.tmpl.html'),
    bindings: {
        pipeline: "=",
        loadPipeline: "&"
    },
    controller: PipelineActionsController,
    controllerAs: 'ctrl'
};
