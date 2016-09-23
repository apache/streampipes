import SocketConnectionDataModel from '../../socket-connection-data-model.service.js'

TableDataModel.$inject = ['SocketConnectionDataModel', '$http'];

export default function TableDataModel(SocketConnectionDataModel, $http) {
	TableDataModel.prototype = Object.create(SocketConnectionDataModel.prototype);

	var dataArray = [];
	var dataArrayLength = 5;

	function TableDataModel(id) {
		SocketConnectionDataModel.call(this, id);
	}

	TableDataModel.prototype.newData = function(message) {
		//dataArray = _.sortBy(dataArray, [function(o) { return o[0]}]);
		if (dataArray.length > dataArrayLength - 1) {
			dataArray = dataArray.slice(Math.max(dataArray.length - dataArrayLength , 1));
		}

		dataArray.push(message);
		this.updateScope(dataArray);
	
	}

	return TableDataModel;
};
