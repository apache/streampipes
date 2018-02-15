import {PipelineElementController} from "./pipeline-element.controller";

export let PipelineElementComponent = {
    templateUrl: 'app/editor/components/pipeline-element/pipeline-element.tmpl.html',
    bindings: {
        pipelineElement : "<"
    },
    controller: PipelineElementController,
    controllerAs: 'ctrl'
};
