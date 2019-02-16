export class DomainProperties {

	$http: any;
	RestApi: any;
	availableDomainProperties: any;

	constructor($http, RestApi) {
		this.$http = $http;
		this.RestApi = RestApi;
		this.availableDomainProperties = {};
		this.RestApi.getOntologyProperties()
            .then(propertiesData => {
                this.availableDomainProperties = propertiesData.data;
            });
	}

	getDomainProperties() {
		return this.availableDomainProperties;
	}

}

//DomainProperties.$inject = ['$http', 'RestApi'];