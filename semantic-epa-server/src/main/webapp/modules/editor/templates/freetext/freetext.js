angular.module('streamPipesApp')
    .directive('freetext', function ($interval) {
        return {
            restrict : 'E',
            templateUrl : 'modules/editor/templates/freetext/freetext.tmpl.html',
            scope : {
                staticProperty : "="
            },
            controller: function($scope, $element) {


                $scope.staticProperty.validator = function() {
                    return $scope.staticProperty.properties.value && $scope.staticProperty.properties.value != "";
                }
            }
        }

    });