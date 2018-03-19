import {PipelineElementsController} from "./pipeline-elements.controller";

export let PipelineElementsComponent = {
    templateUrl: 'pipeline-elements.tmpl.html',
    bindings: {
        pipeline: "<",
        selectedElement: "<"
    },
    controller: PipelineElementsController,
    controllerAs: 'ctrl'
};
