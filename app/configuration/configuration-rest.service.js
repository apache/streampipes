export class ConfigurationRestService {

    constructor($http, apiConstants) {
        this.$http = $http;
        this.apiConstants = apiConstants;
    }

    getServerUrl() {
        return this.apiConstants.contextPath;
    }

    get() {
        return this.$http.get(this.getServerUrl() + '/api/v2/consul');
    }

    update(configuration) {
        return this.$http.post(this.getServerUrl() + '/api/v2/consul', configuration)
    }

}

ConfigurationRestService.$inject = ['$http', 'apiConstants'];