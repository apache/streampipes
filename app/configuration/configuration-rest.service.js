configurationRestApi.$inject = ['$rootScope', '$http', 'apiConstants', '$window'];

export default function configurationRestApi($rootScope, $http, apiConstants, $window) {

    var restApi = {};

    var getServerUrl = function() {
        return apiConstants.contextPath + "/api/apps/v1/elasticsearch";
        //return "http://localhost:8080"  + apiConstants.contextPath + apiConstants.api;
    }

    restApi.get = function () {
        return new Promise(function (reject, resolve) {
            reject([
                {
                    status: true,
                    name: 'Flink',
                    configurations: [
                        {
                            key: 'host',
                            value: '127.0.0.1',
                            description: 'The Host'
                        },
                        {
                            key: 'port',
                            value: 8080,
                            description: 'The Port'
                        }
                    ]
                },
                {
                    status: false,
                    name: 'Esper',
                    configurations: [
                        {
                            key: 'host',
                            value: 'localhost',
                            description: 'The Host'
                        },
                        {
                            key: 'port',
                            value: 4857,
                            description: 'The Port'
                        }
                    ]
                },
                {
                    status: true,
                    name: 'Test',
                    configurations: [
                        {
                            key: 'host',
                            value: 'localhost',
                            description: 'The Host'
                        },
                        {
                            key: 'port',
                            value: 4857,
                            description: 'The Port'
                        },
                        {
                            key: 'test',
                            value: 'test',
                            description: 'Test'
                        }
                    ]
                },
                {
                    status: true,
                    name: 'Test',
                    configurations: [
                        {
                            key: 'host',
                            value: 'localhost',
                            description: 'The Host'
                        },
                        {
                            key: 'port',
                            value: 4857,
                            description: 'The Port'
                        },
                        {
                            key: 'test',
                            value: 'test',
                            description: 'Test'
                        }
                    ]
                }
            ]);
        });
        //return $http.get(getServerUrl() +'/config');
    }

    restApi.update = function (configuration) {
        //return $http.put(getServerUrl() +'/config');
    }


    return restApi;
};