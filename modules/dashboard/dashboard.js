
angular.module('streamPipesApp')
.controller('DashCtrl', function($rootScope, $scope, $http) {
	//$http.get('http://127.0.0.1:5984/visualization/c4df3ae8948d44b0a334cbb0cc9683af')
	//.success(function (data) {
	//$scope.items = data.rows;
	//});
	
	var login = 'admin';
	var passcode = 'admin';
	var brokerUrl = 'ws://ipe-koi04.fzi.de:61614';
	var inputTopic = '/topic/efb75264-5fd4-4469-a539-06c6b56ef2bf';


	var client = Stomp.client(brokerUrl);

	// Uncomment these lines to get all the wesocket messages to the console
	//  client.debug = function (str) {
	//  	console.log(str);
	//  };
	
	$scope.ils = [];
	
	// the client is notified when it is connected to the server.
	var onconnect = function (frame) {

		client.subscribe(inputTopic, function (message) {
			$scope.ils.push(JSON.parse(message.body));
			$scope.$apply();

		});
	};

	client.connect(login, passcode, onconnect);

	// var error_callbck = function(error) {
	// 	client.connect(login, passcode, onconnect);
	// };


});
