import {PipelineElementIconStandController} from "./pipeline-element-icon-stand.controller";
declare const require: any;

export let PipelineElementIconStandComponent = {
    template: require('./pipeline-element-icon-stand.tmpl.html'),
    bindings: {
       currentElements: "=",
       activeType: "="
    },
    controller: PipelineElementIconStandController,
    controllerAs: 'ctrl'
};
