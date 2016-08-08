'use strict';

angular.module('streamPipesApp').factory('Widgets', function (WidgetDataModel, $http) {

		var widgets = new Array();
    var client;


		var createNewWidget = function(widget) {
			widgets[widget.id] = widget;
		}

		var getWidgetById = function(id) {
			return widgets[id];	
		}

		var getWidgetDashboardDefinition = function(id) {
			var widget = getWidgetById(id);
			

			return {
				name: 'wt-top-n',
				title: widget.id,
				dataAttrName: 'data',
				dataModelType: SocketConnectionDataModel,
				dataModelArgs: widget.id,
				style: {
					width: '30%'
				}
			}
		}

		//TODO put SocketConnectionDataModel in an extra class
    function SocketConnectionDataModel(id) {
			var id = id;
			this.id = id;
    }

    SocketConnectionDataModel.prototype = Object.create(WidgetDataModel.prototype);
    SocketConnectionDataModel.prototype.init = function () {

			//console.log(this.id);

        var couchDbServer = 'http://127.0.0.1:5984';

				//var couchDbVizualisationId = '5cfcd7327fd04a549130dba7187e487e';
        //var couchDbVizualisationId = id;
        
        //TODO find better solution
        var login = 'admin';
        var passcode = 'admin';
        var self = this;

        $http.get(couchDbServer + '/visualization/' + this.id)
            .success(function(data) {

                //TODO should we write this into the couchdb?
                var brokerUrl = 'ws://ipe-koi04.fzi.de:61614';
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

		return {
			add: createNewWidget,
			get: getWidgetById,	
			getWidgetDefinition: getWidgetDashboardDefinition
		};
});
