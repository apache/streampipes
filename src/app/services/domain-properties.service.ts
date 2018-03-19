export class DomainProperties {

	$http: any;
	RestApi: any;
	availableDomainProperties: any;

	constructor($http, RestApi) {
		this.$http = $http;
		this.RestApi = RestApi;
		this.availableDomainProperties = {};
		this.RestApi.getOntologyProperties()
            .success(propertiesData => {
                this.availableDomainProperties = propertiesData;
            })
            .error(msg => {
                console.log(msg);
            });
	}

	getDomainProperties() {
		return this.availableDomainProperties;
	}

}

//DomainProperties.$inject = ['$http', 'RestApi'];