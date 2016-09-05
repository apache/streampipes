angular.module('streamPipesApp')
    .directive('mappingPropertyUnary', function ($interval) {
        return {
            restrict : 'E',
            templateUrl : 'modules/editor/templates/mappingunary/mappingunary.tmpl.html',
            scope : {
                staticProperty : "="
            },
            link: function (scope) {
                

            }
        }

    });