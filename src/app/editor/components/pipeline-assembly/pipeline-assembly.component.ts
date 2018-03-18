import {PipelineAssemblyController} from "./pipeline-assembly.controller";

export let PipelineAssemblyComponent = {
    templateUrl: 'pipeline-assembly.tmpl.html',
    bindings: {
        currentModifiedPipelineId: "=",
        rawPipelineModel : "=",
        allElements: "="
    },
    controller: PipelineAssemblyController,
    controllerAs: 'ctrl'
};
