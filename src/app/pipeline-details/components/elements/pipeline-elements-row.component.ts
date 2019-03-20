import {PipelineElementsRowController} from "./pipeline-elements-row.controller";
declare const require: any;

export let PipelineElementsRowComponent = {
    template: require('./pipeline-elements-row.tmpl.html'),
    bindings: {
        element: "<",
        pipeline: "<"
    },
    controller: PipelineElementsRowController,
    controllerAs: 'ctrl'
};
