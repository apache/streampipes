import {PipelineAssemblyController} from "./pipeline-assembly.controller";

export let PipelineAssemblyComponent = {
    templateUrl: 'app/editor/components/pipeline-assembly/pipeline-assembly.tmpl.html',
    bindings: {
        pipelineModel : "=",
        allElements: "="
    },
    controller: PipelineAssemblyController,
    controllerAs: 'ctrl'
};
