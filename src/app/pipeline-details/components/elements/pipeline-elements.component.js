import {PipelineElementsController} from "./pipeline-elements.controller";

export let PipelineElementsComponent = {
    templateUrl: 'app/pipeline-details/components/elements/pipeline-elements.tmpl.html',
    bindings: {
        pipeline: "<",
        selectedElement: "<"
    },
    controller: PipelineElementsController,
    controllerAs: 'ctrl'
};
