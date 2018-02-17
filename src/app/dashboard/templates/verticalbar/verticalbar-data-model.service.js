import SocketConnectionDataModel from '../../socket-connection-data-model.service.js'

VerticalbarDataModel.$inject = ['SocketConnectionDataModel'];

export default function VerticalbarDataModel(SocketConnectionDataModel) {

    VerticalbarDataModel.prototype = Object.create(SocketConnectionDataModel.prototype);
    function VerticalbarDataModel(id) {
        SocketConnectionDataModel.call(this, id);
    }

    VerticalbarDataModel.prototype.newData = function(message) {
        this.updateScope(message);
    }

    return VerticalbarDataModel;
};
