import {PipelineElementRecommendationController} from "./pipeline-element-recommendation.controller";

export let PipelineElementRecommendationComponent = {
    templateUrl: 'app/editor/components/pipeline-element-recommendation/pipeline-element-recommendation.tmpl.html',
    bindings: {
        recommendedElements: "<",
        recommendationsShown: "<"
    },
    controller: PipelineElementRecommendationController,
    controllerAs: 'ctrl'
};
