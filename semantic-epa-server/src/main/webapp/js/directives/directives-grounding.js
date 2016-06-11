angular
    .module('streamPipesApp')
     .directive('transportFormat', function() {
    	return {
    		restrict : 'E',
    		templateUrl : 'modules/sensors/directives/transport-format.tmpl.html',
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
    }) 
    .directive('transportProtocol', function() {
    	return {
    		restrict : 'E',
    		templateUrl : 'modules/sensors/directives/transport-protocol.tmpl.html',
    		scope : {
    			grounding : "=grounding",
    			disabled : "=disabled"
    		},
    		
    		controller: function($scope, $element) {
    			
    			$scope.availableTransportProtocols = [{"id" : "kafka", "name" : "Apache Kafka", "type" : "de.fzi.cep.sepa.model.impl.KafkaTransportProtocol"}, {"id": "jms", "name" : "JMS", "type" : "de.fzi.cep.sepa.model.impl.JmsTransportProtocol"}];
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
    });