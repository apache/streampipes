import {PipelineElementController} from "./pipeline-element.controller";

export let PipelineElementComponent = {
    templateUrl: 'pipeline-element.tmpl.html',
    bindings: {
        pipelineElement : "<"
    },
    controller: PipelineElementController,
    controllerAs: 'ctrl'
};
