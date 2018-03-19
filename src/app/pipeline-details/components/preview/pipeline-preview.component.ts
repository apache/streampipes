import {PipelinePreviewController} from "./pipeline-preview.controller";

export let PipelinePreviewComponent = {
    templateUrl: 'pipeline-preview.tmpl.html',
    bindings: {
        pipeline: "<",
        name: "@",
        selectedElement: "=",
        jspcanvas: "@"
    },
    controller: PipelinePreviewController,
    controllerAs: 'ctrl'
};
