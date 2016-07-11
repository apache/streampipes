angular.module('streamPipesApp').
    factory('activeMqConnector', ['$http', function ($http) {
    var couchDbServer = 'http://127.0.0.1:5984';

    var couchDbVizualisationId = 'c5491ef6630a4af694bddcdd5deb010d';
	var visualisationId = 'c5491ef6630a4af694bddcdd5deb010d';
    
    return function (visualisationId, dataArray) {
			//TODO find better solution
			var login = 'admin';
			var passcode = 'admin';
			
			$http.get(couchDbServer + '/visualization/' + visualisationId)
				.success(function(data) {

					//TODO should we write this into the couchdb?
					var brokerUrl = 'ws://ipe-koi04.fzi.de:61614';
					var inputTopic = '/topic/' + data['pipelineId'];

					var client = Stomp.client(brokerUrl);

					// Uncomment these lines to get all the wesocket messages to the console
					 client.debug = function (str) {
					 	console.log(str);
					 };

					$scope.ils = [];

					// the client is notified when it is connected to the server.
					var onConnect = function (frame) {

						client.subscribe(inputTopic, function (message) {
							dataArray.push(JSON.parse(message.body));
						});
					};

					client.connect(login, passcode, onConnect);
				});
		};
}]);