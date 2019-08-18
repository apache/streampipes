import {PipelineController} from "./pipeline.controller";
declare const require: any;

export let PipelineComponent = {
    template: require('./pipeline.tmpl.html'),
    bindings: {
        staticProperty : "=",
        rawPipelineModel: "=",
        allElements: "=",
        preview: "<",
        canvasId: "@",
        pipelineValid: "="
    },
    controller: PipelineController,
    controllerAs: 'ctrl'
};
