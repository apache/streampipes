import {PipelinePreviewController} from "./pipeline-preview.controller";

export let PipelinePreviewComponent = {
    templateUrl: 'app/pipeline-details/components/preview/pipeline-preview.tmpl.html',
    bindings: {
        pipeline: "<",
        name: "@",
        selectedElement: "=",
        jspcanvas: "@"
    },
    controller: PipelinePreviewController,
    controllerAs: 'ctrl'
};
