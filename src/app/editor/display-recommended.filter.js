displayRecommendedFilter.$inject = [];

export default function displayRecommendedFilter() {
    return function (properties, propertyScope, showRecommended) {
        var result = [];
        if (!showRecommended) {
            return properties;
        } else {
            angular.forEach(properties, function (property) {
                if (property.properties.propertyScope == propertyScope) {
                    result.push(property);
                }
            })
            return result;
        }
    }
}