import {PipelineElementRecommendationController} from "./pipeline-element-recommendation.controller";

export let PipelineElementRecommendationComponent = {
    templateUrl: 'pipeline-element-recommendation.tmpl.html',
    bindings: {
        recommendedElements: "<",
        recommendationsShown: "<",
        rawPipelineModel: "=",
        pipelineElementDomId: "<"
    },
    controller: PipelineElementRecommendationController,
    controllerAs: 'ctrl'
};
