configurationRestApi.$inject = ['$rootScope', '$http', 'apiConstants', '$window'];

export default function configurationRestApi($rootScope, $http, apiConstants, $window) {

    var restApi = {};

    var getServerUrl = function() {
        return apiConstants.contextPath;
    }

    restApi.get = function () {
        return $http.get(getServerUrl() + '/api/v2/consul');
    }

    restApi.update = function (configuration) {
        return $http.post(getServerUrl() + '/api/v2/consul', configuration);
    }


    return restApi;
};