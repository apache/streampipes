import {PipelineAssemblyController} from "./pipeline-assembly.controller";
declare const require: any;

export let PipelineAssemblyComponent = {
    template: require('./pipeline-assembly.tmpl.html'),
    bindings: {
        currentModifiedPipelineId: "=",
        rawPipelineModel : "=",
        allElements: "="
    },
    controller: PipelineAssemblyController,
    controllerAs: 'ctrl'
};
