angular.module('streamPipesApp').factory('NumberDataModel', ['SocketConnectionDataModel', 
	'$http', function(SocketConnectionDataModel, $http) {

	NumberDataModel.prototype = Object.create(SocketConnectionDataModel.prototype);
	function NumberDataModel(id) {
		SocketConnectionDataModel.call(this, id);
	}

	NumberDataModel.prototype.newData = function(message) {
		this.updateScope(message);
	}

	return NumberDataModel;
}]);
