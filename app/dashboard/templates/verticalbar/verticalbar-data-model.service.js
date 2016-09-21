import SocketConnectionDataModel from '../../socket-connection-data-model.service.js'

VerticalbarDataModel.$inject = ['SocketConnectionDataModel', '$http'];

export default function VerticalbarDataModel(SocketConnectionDataModel, $http) {

    VerticalbarDataModel.prototype = Object.create(SocketConnectionDataModel.prototype);
    function VerticalbarDataModel(id) {
        SocketConnectionDataModel.call(this, id);
    }

    VerticalbarDataModel.prototype.newData = function(message) {
        this.updateScope(message);
    }

    return VerticalbarDataModel;
};
