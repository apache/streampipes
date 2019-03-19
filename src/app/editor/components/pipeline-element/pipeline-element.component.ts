import {PipelineElementController} from "./pipeline-element.controller";
declare const require: any;

export let PipelineElementComponent = {
    template: require('./pipeline-element.tmpl.html'),
    bindings: {
        pipelineElement : "<",
        preview: "<",
        iconSize: "<",
        iconStandSize: "<"
    },
    controller: PipelineElementController,
    controllerAs: 'ctrl'
};
