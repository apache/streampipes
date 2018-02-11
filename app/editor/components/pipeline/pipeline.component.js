import {PipelineController} from "./pipeline.controller";

export let PipelineComponent = {
    templateUrl: 'app/editor/components/pipeline/pipeline.tmpl.html',
    bindings: {
        staticProperty : "=",
        pipelineModel: "=",
        allElements: "="
    },
    controller: PipelineController,
    controllerAs: 'ctrl'
};
