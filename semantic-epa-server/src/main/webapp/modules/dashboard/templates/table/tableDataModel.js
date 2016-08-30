angular.module('streamPipesApp').factory('TableDataModel', ['SocketConnectionDataModel', 
	'$http', function(SocketConnectionDataModel, $http) {

	TableDataModel.prototype = Object.create(SocketConnectionDataModel.prototype);

	var dataArray = [];
	var dataArrayLength = 5;

	function TableDataModel(id) {
		SocketConnectionDataModel.call(this, id);

	}


	TableDataModel.prototype.newData = function(message) {
		if (dataArray.length > dataArrayLength - 1) {
			dataArray = dataArray.slice(Math.max(dataArray.length - dataArrayLength , 1));
		}

		dataArray.push(message);
		this.updateScope(dataArray);
	
	}

	return TableDataModel;
}]);
