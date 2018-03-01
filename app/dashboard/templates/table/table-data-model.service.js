import SocketConnectionDataModel from '../../socket-connection-data-model.service.js'


// export default function TableDataModel(SocketConnectionDataModel, $http) {
// export class TableDataModel extends SocketConnectionDataModel {
export class TableDataModel  {

	constructor(SocketConnectionDataModel, $http, id) {

        // Object.setPrototypeOf(TableDataModel, Sock);
        // TableDataModel.prototype = Object.create(SocketConnectionDataModel.prototype);

        var dataArray = [];
        var dataArrayLength = 5;

        // SocketConnectionDataModel.call(this, id);
    }

	newData(message) {
		//dataArray = _.sortBy(dataArray, [function(o) { return o[0]}]);
		if (dataArray.length > dataArrayLength - 1) {
			dataArray = dataArray.slice(Math.max(dataArray.length - dataArrayLength , 1));
		}

		dataArray.push(message);
		this.updateScope(dataArray);
	
	}
};

TableDataModel.$inject = ['SocketConnectionDataModel', '$http'];
