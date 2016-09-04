angular.module('streamPipesApp')
    .directive('oneof', function ($interval) {
        return {
            restrict : 'E',
            templateUrl : 'modules/editor/templates/oneof/oneof.tmpl.html',
            scope : {
                staticProperty : "="
            },
            link: function($scope) {

                $scope.staticProperty.properties.currentSelection;

                var loadSavedProperty = function() {
                    angular.forEach($scope.staticProperty.properties.options, function(option) {
                        if (option.selected) {
                            $scope.staticProperty.properties.currentSelection = option;
                        }
                    });
                }

                $scope.toggleOption = function(option, options) {
                    console.log(option);
                    angular.forEach(options, function(o) {
                        if (o.elementId == option.elementId) {
                            o.selected = true;
                        } else {
                            o.selected = false;
                        }
                    });
                }

                $scope.exists = function(option, options) {
                    angular.forEach(options, function(o) {
                        if (o.elementId == option.elementId) {
                            if (o.selected) return true;
                            return false;
                        }
                    });
                }

                
                loadSavedProperty();
            }
        }

    });