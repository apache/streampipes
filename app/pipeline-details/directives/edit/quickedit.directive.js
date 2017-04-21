'use strict';

quickEdit.$inject = [];


export default function quickEdit() {

    return {
        restrict: 'E',
        templateUrl: 'app/pipeline-details/directives/edit/quickedit.tmpl.html',
        scope: {
            pipeline: "=",
            selectedElement: "="
        },
        controller: function ($scope) {

            $scope.updatePipeline = function() {
                console.log($scope.pipeline);
            }
            
        },
        link: function postLink(scope, element, attrs) {

        }
    }
};