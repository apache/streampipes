import {PipelineElementOptionsController} from "./pipeline-element-options.controller";

export let PipelineElementOptionsComponent = {
    templateUrl: 'app/editor/components/pipeline-element-options/pipeline-element-options.tmpl.html',
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
