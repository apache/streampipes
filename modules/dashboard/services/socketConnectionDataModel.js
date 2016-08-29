angular.module('streamPipesApp').factory('SocketConnectionDataModel', ['WidgetDataModel', '$http', function(WidgetDataModel, $http) {
	 function SocketConnectionDataModel(id) {
		 var id = id;
		 this.id = id;
	 }

	 SocketConnectionDataModel.prototype = Object.create(WidgetDataModel.prototype);
	 SocketConnectionDataModel.prototype.init = function () {

		 //TODO make dynamic
		 var couchDbServer = 'http://127.0.0.1:5984';

		 //TODO find better solution
		 var login = 'admin';
		 var passcode = 'admin';
		 var self = this;


		 $http.get(couchDbServer + '/visualization/' + this.id)
			 .success(function(data) {

			   var brokerUrl = 'ws://' + data['broker'];
         var inputTopic = '/topic/' + data['pipelineId'];

				 client = Stomp.client(brokerUrl + inputTopic);

				 // Uncomment these lines to get all the wesocket messages to the console
				 //  client.debug = function (str) {
				 //  	console.log(str);
				 //  };

				 var dataArray = [];
				 var dataArrayLength = 5;

				 // the client is notified when it is connected to the server.
				 var onConnect = function (frame) {

					 client.subscribe(inputTopic, function (message) {
						 dataArray.push(JSON.parse(message.body));
						 if (dataArray.length > dataArrayLength) {
							 dataArray = dataArray.slice(Math.max(dataArray.length - dataArrayLength, 1));
						 }
						 self.updateScope(dataArray);
					 });
				 };

				 client.connect(login, passcode, onConnect);
			 });
	 };

	 SocketConnectionDataModel.prototype.destroy = function () {
		 WidgetDataModel.prototype.destroy.call(this);

		 client.disconnect(function() {
			 console.log("Disconnected websocket connection");
		 })
	 };

	 return SocketConnectionDataModel;
 }]);
