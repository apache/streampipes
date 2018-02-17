import SocketConnectionDataModel from '../../socket-connection-data-model.service.js'

TrafficlightDataModel.$inject = ['SocketConnectionDataModel', '$http'];

export default function TrafficlightDataModel(SocketConnectionDataModel, $http) {

    TrafficlightDataModel.prototype = Object.create(SocketConnectionDataModel.prototype);
    function TrafficlightDataModel(id) {
        SocketConnectionDataModel.call(this, id);
    }

    TrafficlightDataModel.prototype.newData = function(message) {
        this.updateScope(message);
    }

    return TrafficlightDataModel;
};
