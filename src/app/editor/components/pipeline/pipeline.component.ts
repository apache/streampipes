import {PipelineController} from "./pipeline.controller";

export let PipelineComponent = {
    templateUrl: 'pipeline.tmpl.html',
    bindings: {
        staticProperty : "=",
        rawPipelineModel: "=",
        allElements: "=",
        preview: "<",
        canvasId: "@"
    },
    controller: PipelineController,
    controllerAs: 'ctrl'
};
