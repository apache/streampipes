angular.module('streamPipesApp')
    .directive('any', function ($interval) {
        return {
            restrict : 'E',
            templateUrl : 'modules/editor/templates/any/any.tmpl.html',
            scope : {
                staticProperty : "="
            },
            controller: function($scope, $element) {

            }
        }

    });