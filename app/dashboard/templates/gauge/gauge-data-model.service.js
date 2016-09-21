import SocketConnectionDataModel from '../../socket-connection-data-model.service.js'

GaugeDataModel.$inject = ['SocketConnectionDataModel', '$http'];

export default function GaugeDataModel(SocketConnectionDataModel, $http) {

	GaugeDataModel.prototype = Object.create(SocketConnectionDataModel.prototype);
	function GaugeDataModel(id) {
		SocketConnectionDataModel.call(this, id);
	}

	GaugeDataModel.prototype.newData = function(message) {
	this.updateScope(message);
	}

	GaugeDataModel.prototype.newData = function(message) {

		//dataArray.push(message);
		this.updateScope(message);
	}
	
	return GaugeDataModel;
};
