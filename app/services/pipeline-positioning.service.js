import _ from 'npm/lodash';

pipelinePositioningService.$inject = ['$rootScope'];

export default function pipelinePositioningService($rootScope) {

    var pipelinePositioningService = {};

    pipelinePositioningService.drawPipeline = function (jsplumb, pipeline) {
        console.log(pipeline);
    }
    
    

    return pipelinePositioningService;
}