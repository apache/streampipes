import SocketConnectionDataModel from '../../socket-connection-data-model.service.js'

TrafficLightDataModel.$inject = ['SocketConnectionDataModel', '$http'];

export default function TrafficLightDataModel(SocketConnectionDataModel, $http) {

    TrafficLightDataModel.prototype = Object.create(SocketConnectionDataModel.prototype);
    function TrafficLightDataModel(id) {
        SocketConnectionDataModel.call(this, id);
    }

    TrafficLightDataModel.prototype.newData = function(message) {
        this.updateScope(message);
    }

    return TrafficLightDataModel;
};