angular.module('streamPipesApp')
    .directive('freetext', function ($interval) {
        return {
            restrict: 'E',
            templateUrl: 'modules/editor/templates/freetext/freetext.tmpl.html',
            scope: {
                staticProperty: "="
            },
            link: function ($scope) {


                $scope.staticProperty.validator = function () {
                    return $scope.staticProperty.properties.value && $scope.staticProperty.properties.value != "";
                }
            }
        }

    });