import SocketConnectionDataModel from '../../socket-connection-data-model.service.js'

LineDataModel.$inject = ['SocketConnectionDataModel', '$http'];

export default function LineDataModel(SocketConnectionDataModel, $http) {

	LineDataModel.prototype = Object.create(SocketConnectionDataModel.prototype);
	function LineDataModel(id) {
		SocketConnectionDataModel.call(this, id);
	}

	LineDataModel.prototype.newData = function(message) {
	this.updateScope(message);
	}

	LineDataModel.prototype.newData = function(message) {

		//dataArray.push(message);
		this.updateScope(message);
	}
	
	return LineDataModel;
};
