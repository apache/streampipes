supportedGrounding.$inject = [];

export default function supportedGrounding() {
	return {
		restrict: 'E',
		templateUrl: 'app/sensors/directives/grounding/supported-grounding.tmpl.html',
		scope : {
			grounding : "=grounding",
			disabled : "=disabled"
		},
		link: function($scope, element, attrs) {


			var transportFormatTypesThrift = ["http://sepa.event-processing.org/sepa#TransportFormat", "http://www.w3.org/2000/01/rdf-schema#Resource", "http://sepa.event-processing.org/sepa#thrift"];
			var transportFormatTypesJson = ["http://sepa.event-processing.org/sepa#TransportFormat", "http://www.w3.org/2000/01/rdf-schema#Resource", "http://sepa.event-processing.org/sepa#json"];

			var thriftFormat = "http://sepa.event-processing.org/sepa#thrift";
			var jsonFormat = "http://sepa.event-processing.org/sepa#json";

			$scope.kafkaClass = "org.streampipes.model.impl.KafkaTransportProtocol";
			$scope.jmsClass = "org.streampipes.model.impl.JmsTransportProtocol";

			$scope.kafkaSelected = function(transportProtocols)
			{
				return brokerSelected(transportProtocols, $scope.kafkaClass);
			}

			$scope.jmsSelected = function(transportProtocols)
			{
				return brokerSelected(transportProtocols, $scope.jmsClass);
			}

			$scope.jsonSelected = function(transportFormats)
			{
				return formatSelected(transportFormats, jsonFormat);
			}

			$scope.thriftSelected = function(transportFormats)
			{
				return formatSelected(transportFormats, thriftFormat);
			}

			var brokerSelected = function(transportProtocols, protocolClass) {
				var selected = false;
				angular.forEach(transportProtocols, function(protocol) {
					if (protocol.type == protocolClass) selected = true;
				});
				return selected;
			}

			var formatSelected = function(transportFormats, formatClass) {
				var selected = false;
				angular.forEach(transportFormats, function(format) {
					angular.forEach(format.rdfType, function(type) {
						if (type == formatClass) selected = true;
					})
				});
				return selected;
			}

			$scope.toggleKafka = function(transportProtocols)
			{
				if ($scope.kafkaSelected(transportProtocols)) {
					removeBroker(transportProtocols, $scope.kafkaClass);
				} 
				else {
					checkGrounding();
					if (transportProtocols == undefined) $scope.grounding.transportProtocols = [];
					$scope.grounding.transportProtocols.push({"type" : $scope.kafkaClass, "properties" : {"kafkaPort" : 0, "zookeeperPort" : 0}})
				}
			}

			$scope.toggleJms = function(transportProtocols)
			{
				if ($scope.jmsSelected(transportProtocols)) {
					removeBroker(transportProtocols, $scope.jmsClass);
				} 
				else {
					checkGrounding();
					if (transportProtocols == undefined) $scope.grounding.transportProtocols = [];
					$scope.grounding.transportProtocols.push({"type" : $scope.jmsClass, "properties" : {"port" : 0}})
				}
			}

			var removeBroker = function(transportProtocols, protocolClass) {
				angular.forEach(transportProtocols, function(protocol) {
					if (protocol.type == protocolClass) transportProtocols.splice(transportProtocols.indexOf(protocol), 1);
				});
			}

			$scope.toggleJson = function(transportFormats)
			{
				if ($scope.jsonSelected(transportFormats)) {
					removeFormat(transportFormats, jsonFormat);
				}
				else {
					checkGrounding();
					if (transportFormats == undefined) $scope.grounding.transportFormats = [];
					$scope.grounding.transportFormats.push({"rdfType" : transportFormatTypesJson});
				}
			}

			$scope.toggleThrift = function(transportFormats)
			{
				if ($scope.thriftSelected(transportFormats)) {
					removeFormat(transportFormats, thriftFormat);
				}
				else {
					checkGrounding();
					if (transportFormats == undefined) $scope.grounding.transportFormats = [];
					$scope.grounding.transportFormats.push({"rdfType" : transportFormatTypesThrift});
				}
			}

			var checkGrounding = function() {
				if ($scope.grounding == undefined) $scope.grounding = {};
			}



			var removeFormat = function(transportFormats, formatClass) {
				angular.forEach(transportFormats, function(format) {
					angular.forEach(format.rdfType, function(type) {
						if (type == formatClass) transportFormats.splice(transportFormats.indexOf(format), 1);
					})
				});
			}
		}
	}

};
