angular.module('streamPipesApp')
    .directive('oneof', function ($interval) {
        return {
            restrict : 'E',
            templateUrl : 'modules/editor/templates/oneof/oneof.tmpl.html',
            scope : {
                staticProperty : "="
            },
            controller: function($scope, $element) {

            }
        }

    });