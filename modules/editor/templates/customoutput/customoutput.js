angular.module('streamPipesApp')
    .directive('customOutput', function ($interval) {
        return {
            restrict : 'E',
            templateUrl : 'modules/editor/templates/customoutput/customoutput.tmpl.html',
            scope : {
                outputStrategy : "="
            },
            controller: function($scope, $element) {
                $scope.toggle = function(property, outputStrategy) {
                    if ($scope.exists(property, outputStrategy)) {
                        remove(property, outputStrategy);
                    } else {
                        add(property, outputStrategy);
                    }
                }

                $scope.exists = function(property, outputStrategy) {
                    if (!outputStrategy.properties.eventProperties) return false;
                    return outputStrategy.properties.eventProperties.indexOf(property) > -1;
                }

                var add = function(property, outputStrategy) {
                    if (!outputStrategy.properties.eventProperties) {
                        outputStrategy.properties.eventProperties = [];
                    }
                    outputStrategy.properties.eventProperties.push(property);
                }

                var remove = function(property, outputStrategy) {
                    var index = outputStrategy.properties.eventProperties.indexOf(property);
                    outputStrategy.properties.eventProperties.splice(index, 1);
                }

                $scope.outputStrategy.validator = function() {
                    return $scope.outputStrategy.properties.eventProperties &&
                        $scope.outputStrategy.properties.eventProperties.length > 0;
                }
            }
        }

    });