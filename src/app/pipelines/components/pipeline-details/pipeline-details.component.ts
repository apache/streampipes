import {PipelineDetailsController} from "./pipeline-details.controller";
declare const require: any;

export let PipelineDetailsComponent = {
    template: require('./pipeline-details.tmpl.html'),
    bindings: {
        pipelines: "<",
        refreshPipelines: "&",
        activeCategory: "<"
    },
    controller: PipelineDetailsController,
    controllerAs: 'ctrl'
};
