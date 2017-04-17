'use strict';

pipelineElementsRow.$inject = ['getElementIconText'];


export default function pipelineElementsRow(getElementIconText) {

    return {
        restrict: 'E',
        templateUrl: 'app/pipeline-details/directives/elements/pipeline-elements-row.tmpl.html',
        scope: {
            element: "=",
            elementType: "@",
        },
        controller: function ($scope) {
            $scope.elementTextIcon = function (elementName) {
                return getElementIconText(elementName);
            }

        },
        link: function postLink(scope, element) {
        }
    }
};