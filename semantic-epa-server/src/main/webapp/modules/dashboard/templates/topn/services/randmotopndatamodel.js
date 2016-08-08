'use strict';

angular.module('streamPipesApp').factory('RandomTopNDataModel', function (WidgetDataModel, $http) {
    function RandomTopNDataModel() {
    }

    var client;
    RandomTopNDataModel.prototype = Object.create(WidgetDataModel.prototype);

    RandomTopNDataModel.prototype.init = function () {

        var couchDbServer = 'http://127.0.0.1:5984';

        var couchDbVizualisationId = '5cfcd7327fd04a549130dba7187e487e';
        
        //TODO find better solution
        var login = 'admin';
        var passcode = 'admin';
        var self = this;

        $http.get(couchDbServer + '/visualization/' + couchDbVizualisationId)
            .success(function(data) {

                //TODO should we write this into the couchdb?
                var brokerUrl = 'ws://ipe-koi04.fzi.de:61614';
                var inputTopic = '/topic/' + data['pipelineId'];

                client = Stomp.client(brokerUrl);

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

    RandomTopNDataModel.prototype.destroy = function () {
        WidgetDataModel.prototype.destroy.call(this);
        
        client.disconnect(function() {
           console.log("Disconnected websocket connection");
        })
    };

    return RandomTopNDataModel;
});