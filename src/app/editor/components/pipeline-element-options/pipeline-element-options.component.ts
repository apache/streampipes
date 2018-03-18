import {PipelineElementOptionsController} from "./pipeline-element-options.controller";

export let PipelineElementOptionsComponent = {
    templateUrl: 'pipeline-element-options.tmpl.html',
    bindings: {
        pipelineElementId: "@",
        internalId: "@",
        pipelineElement: "<",
        allElements: "=",
        deleteFunction: "=",
        rawPipelineModel: "=",
        currentMouseOverElement: "="
    },
    controller: PipelineElementOptionsController,
    controllerAs: 'ctrl'
};
