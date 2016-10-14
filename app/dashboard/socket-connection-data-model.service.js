SocketConnectionDataModel.$inject = ['WidgetDataModel', '$http'];

export default function SocketConnectionDataModel(WidgetDataModel, $http) {
	function SocketConnectionDataModel(visualisationId) {
		var visualisationId = visualisationId;
		this.visualisationId = visualisationId;
		this.client = {};
	}

	SocketConnectionDataModel.prototype = Object.create(WidgetDataModel.prototype);
	SocketConnectionDataModel.prototype.init = function () {
		
		//TODO find better solution
		var login = 'admin';
		var passcode = 'admin';
		var self = this;

		$http.get('/dashboard/_all_docs?include_docs=true')
			.success(function(data) {

				var element = _.find(data.rows, function(elem) {
					return elem.doc.visualisation._id == self.visualisationId;
				});

				var brokerUrl = 'ws://' + element.doc.visualisation['broker'];
				var inputTopic = '/topic/' + element.doc.visualisation['pipelineId'];

				self.client = Stomp.client(brokerUrl + inputTopic);

				// Uncomment these lines to get all the wesocket messages to the console
				//  client.debug = function (str) {
				//  	console.log(str);
				//  };

				// the client is notified when it is connected to the server.
				var onConnect = function (frame) {

					self.client.subscribe(inputTopic, function (message) {
						self.newData(JSON.parse(message.body));
					});
				};

				self.client.connect(login, passcode, onConnect);
			});
	};

	SocketConnectionDataModel.prototype.destroy = function () {
		WidgetDataModel.prototype.destroy.call(this);

		self.client.disconnect(function() {
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
};
