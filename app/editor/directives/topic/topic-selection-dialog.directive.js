topicSelectionDialog.$inject = [];

export default function topicSelectionDialog() {

    return {
        restrict: 'E',
        templateUrl: 'app/editor/directives/topic/topic-selection-dialog.tmpl.html',
        scope: {
            streamDescription: "=",
            titleField: "=",
            topicMappings: "="
        },
        link: function (scope) {
           var getEventProperties = function() {
                return scope.streamDescription.eventSchema.eventProperties;
            }

            var getEventProperty = function(runtimeName) {
                var eventProperty = {};
                angular.forEach(getEventProperties(), function(ep) {
                    if (ep.properties.runtimeName == runtimeName) {
                        eventProperty = ep;
                    }
                })
                return eventProperty;
            }

            var getTopicDefinition = function () {
                return scope.streamDescription.eventGrounding
                    .transportProtocols[0]
                    .properties.topicDefinition
                    .properties;
            }

            var getWildcardTopic = function() {
                return getTopicDefinition()
                    .wildcardTopicName;
            }

            scope.getLabel = function(runtimeName) {
                var ep = getEventProperty(runtimeName);
                return ep.properties.label + " (" +ep.properties.description +")";
            }

            scope.getPossibleValues = function(runtimeName) {
                var ep = getEventProperty(runtimeName);
                return ep.properties.valueSpecification.properties.runtimeValues;
            }

            angular.forEach(scope.topicMappings, function(topicMapping) {
                if (!topicMapping.selectedMapping) {
                    topicMapping.selectedMapping = "*";
                }
            })

            scope.eventProperties = getEventProperties();
        }
    }

};
