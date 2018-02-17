import SocketConnectionDataModel from '../../socket-connection-data-model.service.js'

MapDataModel.$inject = ['SocketConnectionDataModel', '$http'];

export default function MapDataModel(SocketConnectionDataModel, $http) {

    MapDataModel.prototype = Object.create(SocketConnectionDataModel.prototype);
    function MapDataModel(id) {
        SocketConnectionDataModel.call(this, id);
    }

    MapDataModel.prototype.newData = function(message) {
        this.updateScope(message);
    }

    return MapDataModel;
};
