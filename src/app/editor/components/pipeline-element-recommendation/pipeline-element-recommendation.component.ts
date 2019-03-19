import {PipelineElementRecommendationController} from "./pipeline-element-recommendation.controller";
declare const require: any;

export let PipelineElementRecommendationComponent = {
    template: require('./pipeline-element-recommendation.tmpl.html'),
    bindings: {
        recommendedElements: "<",
        recommendationsShown: "<",
        rawPipelineModel: "=",
        pipelineElementDomId: "<"
    },
    controller: PipelineElementRecommendationController,
    controllerAs: 'ctrl'
};
