export class MeasurementUnits {

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
            .success(measurementUnits => {
                this.allMeasurementUnits = measurementUnits;
            })
            .error(msg => {
                console.log(msg);
            });
    }

    updateUnitTypes() {
        this.RestApi.getAllUnitTypes()
            .success(measurementUnits => {
                this.allMeasurementUnitTypes = measurementUnits;
            })
            .error(msg => {
                console.log(msg);
            });
    }

    getUnits() {
        return this.allMeasurementUnits;
    }

    getUnitTypes() {
        return this.allMeasurementUnitTypes;
    }

}

MeasurementUnits.$inject = ['$http', 'RestApi'];