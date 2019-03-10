import {PipelineElementDocumentationController} from "./pipeline-element-documentation.controller";

export let PipelineElementDocumentationComponent = {
    templateUrl: 'pipeline-element-documentation.tmpl.html',
    bindings: {
        appId: "="
    },
    controller: PipelineElementDocumentationController,
    controllerAs: 'ctrl'
};
