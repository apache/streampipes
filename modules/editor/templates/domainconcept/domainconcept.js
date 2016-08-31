angular.module('streamPipesApp')
    .directive('domainConceptInput', function(restApi) {
    return {
        restrict : 'E',
        templateUrl : 'modules/editor/templates/domainconcept/domainconcept.tmpl.html',
        scope : {
            staticProperty : "=",
            autoCompleteStaticProperty : "="
        },
        controller: function($scope, $element)  {

            $scope.querySearch = querySearch;
            $scope.selectedItemChange = selectedItemChange;
            $scope.searchTextChange   = searchTextChange;

            $scope.availableDomainProperties = {};

            $scope.loadDomainConcepts = function(item) {
                var query = {};
                query.requiredClass = item.input.properties.requiredClass;
                query.requiredProperties = [];
                angular.forEach(item.input.properties.supportedProperties, function(p) {
                    var propertyObj = {};
                    propertyObj.propertyId = p.propertyId;
                    query.requiredProperties.push(propertyObj);
                });

                restApi.getDomainKnowledgeItems(query)
                    .success(function(queryResponse){
                        if (!$scope.availableDomainProperties[item.elementId])
                        {
                            $scope.availableDomainProperties[item.elementId] = {};
                        }
                        angular.forEach(queryResponse.requiredProperties, function(resp) {
                            angular.forEach(resp.queryResponse, function(instanceResult) {
                                if (!$scope.availableDomainProperties[item.elementId][resp.propertyId])
                                    $scope.availableDomainProperties[item.elementId][resp.propertyId] = [];
                                var instanceData = {label : instanceResult.label, description : instanceResult.description, propertyValue : instanceResult.propertyValue};
                                $scope.availableDomainProperties[item.elementId][resp.propertyId].push(instanceData);
                            });
                        });
                    })
                    .error(function(msg){
                        console.log(msg);
                    });
            }

            function querySearch (query, staticPropertyId) {
                var result = [];
                var i = 0;
                angular.forEach($scope.availableDomainProperties[staticPropertyId], function(values) {
                    if (values.length > 0 && i == 0)
                    {
                        var position = 0;
                        angular.forEach(values, function(value) {
                            if (query == undefined || value.label.substring(0, query.length) === query) result.push({label : value.label, description: value.description, position : position});
                            position++;
                        })
                        i++;
                    }
                });
                return result;
            }

            function searchTextChange(text) {

            }

            function selectedItemChange(item, staticPropertyId, supportedProperties) {
                angular.forEach(supportedProperties, function(supportedProperty) {
                    supportedProperty.value = $scope.availableDomainProperties[staticPropertyId][supportedProperty.propertyId][item.position].propertyValue;
                });
            }

            $scope.loadDomainConcepts($scope.autoCompleteStaticProperty);

        }
    }
});