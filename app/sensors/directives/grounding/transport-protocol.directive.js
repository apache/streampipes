transportProtocol.$inject = [];

export default function transportProtocol() {
	return {
		restrict : 'E',
		templateUrl : 'app/sensors/directives/grounding/transport-protocol.tmpl.html',
		scope : {
			grounding : "=grounding",
			disabled : "=disabled"
		},

		controller: function($scope, $element) {

			$scope.availableTransportProtocols = [{"id" : "kafka", "name" : "Apache Kafka", "type" : "org.streampipes.model.impl.KafkaTransportProtocol"},
				{"id": "jms", "name" : "JMS", "type" : "org.streampipes.model.impl.JmsTransportProtocol"},
				{"id": "mqtt", "name" : "MQTT", "type" : "org.streampipes.model.impl.MqttTransportProtocol"}];
			$scope.selectedTransportProtocol = "";

			$scope.addTransportProtocol = function(transportProtocols) {
				if ($scope.selectedTransportProtocol == $scope.availableTransportProtocols[0].id) $scope.addKafkaProtocol(transportProtocols);
				else $scope.addJmsProtocol(transportProtocols);
			};

			$scope.addKafkaProtocol = function(transportProtocols) {
				transportProtocols.push({"type" : $scope.availableTransportProtocols[0].type, "properties" : {"zookeeperHost" : "", "zookeeperPort" : 2181, "brokerHostname" : "", "kafkaPort" : 9092, "topicName" : ""}});
			}

			$scope.addJmsProtocol = function(transportProtocols) {
				transportProtocols.push({"type" : $scope.availableTransportProtocols[1].type, "properties" : {"brokerHostname" : "", "port" : 61616, "topicName" : ""}});
			}

			$scope.removeTransportProtocol = function(transportProtocols) {
				transportProtocols.splice(0, 1);
			}

			$scope.findProtocol = function(transportProtocol) {
				if (transportProtocol == undefined) return "";
				else {
					if (transportProtocol.type == $scope.availableTransportProtocols[0].type) return $scope.availableTransportProtocols[0].name;
					else return $scope.availableTransportProtocols[1].name;
				}
			} 
		}
	}
};
