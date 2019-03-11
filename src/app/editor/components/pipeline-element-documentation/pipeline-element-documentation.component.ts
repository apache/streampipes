import {PipelineElementDocumentationController} from "./pipeline-element-documentation.controller";

export let PipelineElementDocumentationComponent = {
    templateUrl: 'pipeline-element-documentation.tmpl.html',
    bindings: {
        appId: "=",
        useStyling: "="
    },
    controller: PipelineElementDocumentationController,
    controllerAs: 'ctrl'
};
