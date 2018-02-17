import SocketConnectionDataModel from '../../socket-connection-data-model.service.js'

HeatmapDataModel.$inject = ['SocketConnectionDataModel', '$http'];

export default function HeatmapDataModel(SocketConnectionDataModel, $http) {

    HeatmapDataModel.prototype = Object.create(SocketConnectionDataModel.prototype);
    function HeatmapDataModel(id) {
        SocketConnectionDataModel.call(this, id);
    }

    HeatmapDataModel.prototype.newData = function(message) {
        this.updateScope(message);
    }
    
    return HeatmapDataModel;
};
