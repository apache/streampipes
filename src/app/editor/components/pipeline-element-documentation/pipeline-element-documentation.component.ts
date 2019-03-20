import {PipelineElementDocumentationController} from "./pipeline-element-documentation.controller";
declare const require: any;

export let PipelineElementDocumentationComponent = {
    template: require('./pipeline-element-documentation.tmpl.html'),
    bindings: {
        appId: "=",
        useStyling: "="
    },
    controller: PipelineElementDocumentationController,
    controllerAs: 'ctrl'
};
