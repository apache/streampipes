import angular from 'angular';

export class DeploymentService {

    $http: any;
    $rootScope: any;
    RestApi: any;

    constructor($http, $rootScope, RestApi) {
        this.$http = $http;
        this.$rootScope = $rootScope;
        this.RestApi = RestApi;
    }

    updateElement(deploymentConfig, model) {
        return this.$http({
            method: 'POST',
            headers: {'Accept': 'application/json', 'Content-Type': undefined},
            url: '/semantic-epa-backend/api/v2/users/' + this.$rootScope.email + '/deploy/update',
            data: this.getFormData(deploymentConfig, model)
        });
    }

    generateImplementation(deploymentConfig, model) {
        return this.$http({
            method: 'POST',
            responseType: 'arraybuffer',
            headers: {'Accept': 'application/zip', 'Content-Type': undefined},
            url: '/semantic-epa-backend/api/v2/users/' + this.$rootScope.email + '/deploy/implementation',
            data: this.getFormData(deploymentConfig, model)
        });
    }

    generateDescriptionJava(deploymentConfig, model) {
        return this.$http({
            method: 'POST',
            headers: {'Accept': 'text/plain', 'Content-Type': undefined},
            url: '/semantic-epa-backend/api/v2/users/' + this.$rootScope.email + '/deploy/description/java',
            data: this.getFormData(deploymentConfig, model)
        });
    }

    generateDescriptionJsonld(deploymentConfig, model) {
        return this.$http({
            method: 'POST',
            headers: {'Accept': 'application/json', 'Content-Type': undefined},
            url: '/semantic-epa-backend/api/v2/users/' + this.$rootScope.email + '/deploy/description/jsonld',
            data: this.getFormData(deploymentConfig, model)
        });
    }

    getFormData(deploymentConfig, model) {
        var formData = new FormData();
        formData.append("config", angular.toJson(deploymentConfig));
        formData.append("model", angular.toJson(model));
        return formData;
    }

}

//DeploymentService.$inject = ['$http', '$rootScope', 'RestApi'];