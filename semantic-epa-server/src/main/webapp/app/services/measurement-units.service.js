measurementUnits.$inject = ['$http', 'restApi'];

export default function measurementUnits($http, restApi) {
	var measurementUnitsService = {};

	var allMeasurementUnits = {};
	var allMeasurementUnitTypes = {};

	var updateUnits = function() {
		restApi.getAllUnits()
			.success(function(measurementUnits){
				allMeasurementUnits = measurementUnits;
			})
			.error(function(msg){
				console.log(msg);
			});
	};


	var updateUnitTypes = function() {
		restApi.getAllUnitTypes()
			.success(function(measurementUnits){
				allMeasurementUnitTypes = measurementUnits;
			})
			.error(function(msg){
				console.log(msg);
			});
	};

	updateUnits();
	updateUnitTypes();

	measurementUnitsService.getUnits = function() {
		return allMeasurementUnits;
	}

	measurementUnitsService.getUnitTypes = function() {
		return allMeasurementUnitTypes;
	}

	measurementUnitsService.updateUnits = function() {
		updateUnits();
	}

	measurementUnitsService.updateUnitTypes = function() {
		updateUnitTypes;
	}

	return measurementUnitsService;

};
