'use strict';

pipelineActions.$inject = [];


export default function pipelineActions() {

    return {
        restrict: 'E',
        templateUrl: 'app/pipeline-details/directives/actions/pipeline-actions.tmpl.html',
        scope: {
            pipeline: "=",
        },
        controller: function ($scope) {

            console.log("actions");
            console.log($scope.pipeline);

            $scope.startPipeline = function() {

            }

            $scope.stopPipeline = function() {

            }

            $scope.modifyPipeline = function() {

            }

            $scope.deletePipeline = function() {

            }
        },
        link: function postLink(scope, element) {
        }
    }
};