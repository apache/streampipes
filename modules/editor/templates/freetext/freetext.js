angular.module('streamPipesApp')
    .directive('freetext', function ($interval) {
        return {
            restrict : 'E',
            templateUrl : 'modules/editor/templates/freetext/freetext.tmpl.html',
            scope : {
                staticProperty : "="
            },
            controller: function($scope, $element) {

            }
        }

    });