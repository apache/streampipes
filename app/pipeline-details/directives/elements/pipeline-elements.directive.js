'use strict';

pipelineElements.$inject = [];


export default function pipelineElements() {

    return {
        restrict: 'E',
        templateUrl: 'app/pipeline-details/directives/elements/pipeline-elements.tmpl.html',
        scope: {
            pipeline: "=",
            selectedElement: "="
        },
        controller: function ($scope) {

            $scope.getElementType = function(pipeline, element) {
                var elementType = "action";

                angular.forEach(pipeline.streams, function(el) {
                   if (element.DOM == el.DOM) {
                       elementType = "stream";
                   }
                });

                angular.forEach(pipeline.sepas, function(el) {
                    if (element.DOM == el.DOM) {
                        elementType = "sepa";
                    }
                });

                return elementType;
            }
        },
        link: function postLink(scope, element) {
        }
    }
};