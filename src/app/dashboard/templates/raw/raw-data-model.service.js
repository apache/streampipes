import SocketConnectionDataModel from '../../socket-connection-data-model.service.js'

RawDataModel.$inject = ['SocketConnectionDataModel', '$http'];

export default function RawDataModel(SocketConnectionDataModel, $http) {
    var dataArray = [];
    var dataArrayLength = 5;

    RawDataModel.prototype = Object.create(SocketConnectionDataModel.prototype);
    function RawDataModel(id) {
        SocketConnectionDataModel.call(this, id);
    }

    RawDataModel.prototype.newData = function(message) {
        if (dataArray.length > dataArrayLength - 1) {
            dataArray = dataArray.slice(Math.max(dataArray.length - dataArrayLength , 1));
        }

        dataArray.push(message);
        this.updateScope(dataArray);
    }

    return RawDataModel;
};
