import {PipelineElementsController} from "./pipeline-elements.controller";
declare const require: any;

export let PipelineElementsComponent = {
    template: require('./pipeline-elements.tmpl.html'),
    bindings: {
        pipeline: "<",
        selectedElement: "<"
    },
    controller: PipelineElementsController,
    controllerAs: 'ctrl'
};
