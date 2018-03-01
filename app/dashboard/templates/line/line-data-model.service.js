import { SocketConnectionDataModel } from '../../socket-connection-data-model.service.js'


// export default function LineDataModel(SocketConnectionDataModel, $http) {
export class LineDataModel extends SocketConnectionDataModel {
// export class LineDataModel {

	constructor(WidgetDataModel, $http, id) {
        super(WidgetDataModel, $http, id);
    }

	// LineDataModel.prototype = Object.create(SocketConnectionDataModel.prototype);
	// function LineDataModel(id) {
	// 	SocketConnectionDataModel.call(this, id);
	// }

	// LineDataModel.prototype.newData = function(message) {
	// this.updateScope(message);
	// }

	newData(message) {
		this.updateScope(message);
	}
	
	// return LineDataModel;
};

LineDataModel.$inject = ['WidgetDataModel', '$http'];
