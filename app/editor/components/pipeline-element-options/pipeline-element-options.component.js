import {PipelineElementOptionsController} from "./pipeline-element-options.controller";

export let PipelineElementOptionsComponent = {
    templateUrl: 'app/editor/components/pipeline-element-options/pipeline-element-options.tmpl.html',
    bindings: {
        pipelineElementId: "@",
        internalId: "@",
        allElements: "=",
        createFunction: "=",
        createPartialPipelineFunction: "=",
        showCustomizeDialogFunction: "=",
        showCustomizeStreamDialogFunction: "=",
        deleteFunction: "="
    },
    controller: PipelineElementOptionsController,
    controllerAs: 'ctrl'
};
