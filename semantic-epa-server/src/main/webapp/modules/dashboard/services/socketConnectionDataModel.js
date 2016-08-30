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

				// the client is notified when it is connected to the server.
				var onConnect = function (frame) {

					client.subscribe(inputTopic, function (message) {
						self.newData(JSON.parse(message.body));
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


	SocketConnectionDataModel.prototype.updateScope = function(data) {
		this.widgetScope.widgetData = data;
		this.widgetScope.$apply(function () {
		});
	}

	SocketConnectionDataModel.prototype.newData = function(message) {
		// to be overridden by subclasses
	}

	return SocketConnectionDataModel;
}]);
