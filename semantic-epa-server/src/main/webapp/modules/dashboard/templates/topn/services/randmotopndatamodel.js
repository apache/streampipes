'use strict';

angular.module('streamPipesApp').factory('RandomTopNDataModel', function (WidgetDataModel, $http) {
    function RandomTopNDataModel() {
    }

    RandomTopNDataModel.prototype = Object.create(WidgetDataModel.prototype);

    RandomTopNDataModel.prototype.init = function () {

        var couchDbServer = 'http://127.0.0.1:5984';

        var couchDbVizualisationId = 'c5491ef6630a4af694bddcdd5deb010d';
        
        //TODO find better solution
        var login = 'admin';
        var passcode = 'admin';
        var self = this;

        $http.get(couchDbServer + '/visualization/' + 'c5491ef6630a4af694bddcdd5deb010d')
            .success(function(data) {

                //TODO should we write this into the couchdb?
                var brokerUrl = 'ws://ipe-koi04.fzi.de:61614';
                var inputTopic = '/topic/' + data['pipelineId'];

                var client = Stomp.client(brokerUrl);

                // Uncomment these lines to get all the wesocket messages to the console
                //  client.debug = function (str) {
                //  	console.log(str);
                //  };

                var dataArray = [];

                // the client is notified when it is connected to the server.
                var onConnect = function (frame) {

                    client.subscribe(inputTopic, function (message) {
                        dataArray.push(JSON.parse(message.body));
                        self.updateScope(dataArray);
                    });
                };

                client.connect(login, passcode, onConnect);
            });




        // this.intervalPromise = $interval(function () {
        //     var topTen = _.map(_.range(0, 10), function (index) {
        //         return {
        //             name: 'item' + index,
        //             value: Math.floor(Math.random() * 100)
        //         };
        //     });
        //     this.updateScope(topTen);
        // }.bind(this), 500);
    };

    RandomTopNDataModel.prototype.destroy = function () {
        WidgetDataModel.prototype.destroy.call(this);
        $interval.cancel(this.intervalPromise);
    };

    return RandomTopNDataModel;
});