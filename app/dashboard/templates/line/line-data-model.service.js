import SocketConnectionDataModel from '../../socket-connection-data-model.service.js'

LineDataModel.$inject = ['WidgetDataModel', '$http'];

export default function LineDataModel(WidgetDataModel, $http) {

	//LineDataModel.prototype = Object.create(SocketConnectionDataModel.prototype);
	//function LineDataModel(id) {
	//SocketConnectionDataModel.call(this, id);
	//}

	//LineDataModel.prototype.newData = function(message) {
	//this.updateScope(message);
	//}
	function LineDataModel() {
	}

	LineDataModel.prototype = Object.create(WidgetDataModel.prototype);


	LineDataModel.prototype.updateScope = function(data) {
		this.widgetScope.widgetData = [
			{
				label: "Series 1",
				values: [ {x: 0, y: 100}, {x: 20, y: 1000} ]
			}
		];

		this.widgetScope.$apply(function () {
		});

	}
	return LineDataModel;
};
