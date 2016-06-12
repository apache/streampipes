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
    			
    			$scope.availableTransportProtocols = [{"id" : "kafka", "name" : "Apache Kafka", "type" : "de.fzi.cep.sepa.model.impl.KafkaTransportProtocol"}, 
    			                                      {"id": "jms", "name" : "JMS", "type" : "de.fzi.cep.sepa.model.impl.JmsTransportProtocol"},
    			                                      {"id": "mqtt", "name" : "MQTT", "type" : "de.fzi.cep.sepa.model.impl.MqttTransportProtocol"}];
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
    }).directive('supportedGrounding', function() {
    	return {
	        restrict: 'E',
	        templateUrl: 'modules/sensors/directives/supported-grounding.tmpl.html',
	        scope : {
	        	grounding : "=grounding",
    			disabled : "=disabled"
	        },
	        link: function($scope, element, attrs) {

	        	
	        	var transportFormatTypesThrift = ["http://sepa.event-processing.org/sepa#TransportFormat", "http://www.w3.org/2000/01/rdf-schema#Resource", "http://sepa.event-processing.org/sepa#thrift"];
	        	var transportFormatTypesJson = ["http://sepa.event-processing.org/sepa#TransportFormat", "http://www.w3.org/2000/01/rdf-schema#Resource", "http://sepa.event-processing.org/sepa#json"];
	        	
	        	var thriftFormat = "http://sepa.event-processing.org/sepa#thrift";
	        	var jsonFormat = "http://sepa.event-processing.org/sepa#json";
	        	
	        	$scope.kafkaClass = "de.fzi.cep.sepa.model.impl.KafkaTransportProtocol";
	        	$scope.jmsClass = "de.fzi.cep.sepa.model.impl.JmsTransportProtocol";
	        	   	
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
    	
    });