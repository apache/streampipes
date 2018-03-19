import {PipelineElementsRowController} from "./pipeline-elements-row.controller";

export let PipelineElementsRowComponent = {
    templateUrl: 'pipeline-elements-row.tmpl.html',
    bindings: {
        element: "<",
        pipeline: "<"
    },
    controller: PipelineElementsRowController,
    controllerAs: 'ctrl'
};
