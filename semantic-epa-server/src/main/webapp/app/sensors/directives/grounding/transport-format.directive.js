transportFormat.$inject = [];

export default function transportFormat() {
	return {
		restrict : 'E',
		templateUrl : 'app/sensors/directives/grounding/transport-format.tmpl.html',
		scope : {
			grounding : "=grounding",
			disabled : "=disabled"
		},

		controller: function($scope, $element) {

			$scope.availableTransportFormats = [{"id" : "thrift", "name" : "Thrift Simple Event Format", "rdf" : ["http://www.w3.org/2000/01/rdf-schema#Resource", "http://sepa.event-processing.org/sepa#TransportFormat", "http://sepa.event-processing.org/sepa#thrift"]}, 
				{"id": "json", "name" : "Flat JSON Format", "rdf" : ["http://www.w3.org/2000/01/rdf-schema#Resource", "http://sepa.event-processing.org/sepa#TransportFormat", "http://sepa.event-processing.org/sepa#json"]},
				{"id" : "xml", "name" : "XML", "rdf" : ["http://www.w3.org/2000/01/rdf-schema#Resource", "http://sepa.event-processing.org/sepa#TransportFormat", "http://sepa.event-processing.org/sepa#xml"]}];
	$scope.selectedTransportFormat = "";

	var getFormat = function() {
		if ($scope.selectedTransportFormat == 'thrift') return $scope.availableTransportFormats[0].rdf;
		else return $scope.availableTransportFormats[1].rdf;
	}

	$scope.addTransportFormat = function(transportFormats) {
		transportFormats.push({"rdfType" : getFormat()});
	}

	$scope.removeTransportFormat = function(transportFormats) {
		transportFormats.splice(0, 1);
	}

	$scope.findFormat = function(transportFormat) {
		if (transportFormat == undefined) return "";
		else {
			if (transportFormat.rdfType.indexOf($scope.availableTransportFormats[0].rdf[2]) != -1) return $scope.availableTransportFormats[0].name;
			else return $scope.availableTransportFormats[1].name;
		}
	}
		}
	}
};
