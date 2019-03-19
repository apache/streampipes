import {PipelineElementOptionsController} from "./pipeline-element-options.controller";
declare const require: any;

export let PipelineElementOptionsComponent = {
    template: require('./pipeline-element-options.tmpl.html'),
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
