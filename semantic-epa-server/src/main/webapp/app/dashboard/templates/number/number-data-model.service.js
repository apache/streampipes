import SocketConnectionDataModel from '../../socket-connection-data-model.service.js'

NumberDataModel.$inject = ['SocketConnectionDataModel', '$http'];

export default function NumberDataModel(SocketConnectionDataModel, $http) {

	NumberDataModel.prototype = Object.create(SocketConnectionDataModel.prototype);
	function NumberDataModel(id) {
		SocketConnectionDataModel.call(this, id);
	}

	NumberDataModel.prototype.newData = function(message) {
		this.updateScope(message);
	}

	return NumberDataModel;
};
