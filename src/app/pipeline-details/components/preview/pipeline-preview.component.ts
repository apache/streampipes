import {PipelinePreviewController} from "./pipeline-preview.controller";
declare const require: any;

export let PipelinePreviewComponent = {
    template: require('./pipeline-preview.tmpl.html'),
    bindings: {
        pipeline: "<",
        name: "@",
        selectedElement: "=",
        jspcanvas: "@"
    },
    controller: PipelinePreviewController,
    controllerAs: 'ctrl'
};
