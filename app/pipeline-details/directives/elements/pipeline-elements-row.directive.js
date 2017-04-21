'use strict';

pipelineElementsRow.$inject = ['getElementIconText'];


export default function pipelineElementsRow(getElementIconText) {

    return {
        restrict: 'E',
        templateUrl: 'app/pipeline-details/directives/elements/pipeline-elements-row.tmpl.html',
        scope: {
            element: "=",
            pipeline: "="
        },
        controller: function ($scope) {
            $scope.elementTextIcon = function (elementName) {
                return getElementIconText(elementName);
            }

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