angular.module('streamPipesApp').factory('TableDataModel', ['SocketConnectionDataModel', '$http', function(SocketConnectionDataModel, $http) {
	function TableDataModel(id) {
		SocketConnectionDataModel.call(this, id);
	}

	var dataArray = [];
	var dataArrayLength = 5;


	SocketConnectionDataModel.prototype.newData = function(message) {
		// to be overridden by subclasses
		dataArray.push(JSON.parse(message.body));
		if (dataArray.length > dataArrayLength) {
			dataArray = dataArray.slice(Math.max(dataArray.length - dataArrayLength, 1));
		}
		this.updateScope(dataArray);
	}

	return SocketConnectionDataModel;
}]);
