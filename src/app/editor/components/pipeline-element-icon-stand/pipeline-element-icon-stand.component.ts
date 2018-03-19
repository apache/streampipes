import {PipelineElementIconStandController} from "./pipeline-element-icon-stand.controller";

export let PipelineElementIconStandComponent = {
    templateUrl: 'pipeline-element-icon-stand.tmpl.html',
    bindings: {
       currentElements: "=",
       activeType: "="
    },
    controller: PipelineElementIconStandController,
    controllerAs: 'ctrl'
};
