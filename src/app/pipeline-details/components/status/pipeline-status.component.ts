import {PipelineStatusController} from "./pipeline-status.controller";
declare const require: any;

export let PipelineStatusComponent = {
    template: require('./pipeline-status.tmpl.html'),
    bindings: {
        pipeline: "<",
    },
    controller: PipelineStatusController,
    controllerAs: 'ctrl'
};
