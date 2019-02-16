export class MeasurementUnits {

    measurementUnitsService: any;
    allMeasurementUnits: any;
    allMeasurementUnitTypes: any;
    $http: any;
    RestApi: any;

    constructor($http, RestApi) {
        this.measurementUnitsService = {};
        this.allMeasurementUnits = {};
        this.allMeasurementUnitTypes = {};
        this.$http = $http;
        this.RestApi = RestApi;
        this.updateUnits();
        this.updateUnitTypes();
    }

    updateUnits() {
        this.RestApi.getAllUnits()
            .then(measurementUnits => {
                this.allMeasurementUnits = measurementUnits.data;
            });
    }

    updateUnitTypes() {
        this.RestApi.getAllUnitTypes()
            .then(measurementUnits => {
                this.allMeasurementUnitTypes = measurementUnits.data;
            });
    }

    getUnits() {
        return this.allMeasurementUnits;
    }

    getUnitTypes() {
        return this.allMeasurementUnitTypes;
    }

}

//MeasurementUnits.$inject = ['$http', 'RestApi'];