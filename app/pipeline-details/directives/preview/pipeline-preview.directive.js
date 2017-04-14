import angular from 'npm/angular';
'use strict';

pipelinePreview.$inject = ['pipelinePositioningService', 'jsplumbService'];


export default function pipelinePreview(pipelinePositioningService, jsplumbService) {

    return {
        restrict: 'E',
        templateUrl: 'app/pipeline-details/directives/preview/pipeline-preview.tmpl.html',
        scope: {
            pipeline: "=",
            name: "@"
        },
        controller: function ($scope) {
            var js2 = jsPlumb.getInstance({});
            js2.setContainer("assembly-preview");
            jsplumbService.prepareJsplumb(js2);
            pipelinePositioningService.displayPipeline($scope, js2, $scope.pipeline, "#assembly-preview", true);

        },
        link: function postLink(scope, element) {
        }
    }
};