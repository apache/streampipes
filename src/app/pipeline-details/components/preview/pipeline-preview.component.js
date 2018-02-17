import {PipelinePreviewController} from "./pipeline-preview.controller";

export let PipelinePreviewComponent = {
    templateUrl: 'app/pipeline-details/components/preview/pipeline-preview.tmpl.html',
    bindings: {
        pipeline: "<",
        name: "@",
        updateSelected: "&",
        jspcanvas: "@"
    },
    controller: PipelinePreviewController,
    controllerAs: 'ctrl'
};
