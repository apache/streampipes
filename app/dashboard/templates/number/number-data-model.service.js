// export default function NumberDataModel(SocketConnectionDataModel, $http) {

import { SocketConnectionDataModel } from '../../socket-connection-data-model.service';

// export class NumberDataModel extends SocketConnectionDataModel {
    export class NumberDataModel  {

    constructor(WidgetDataModel, $http, id) {
        // super(WidgetDataModel, $http, id);

        // Object.setPrototypeOf(SocketConnectionDataModel, WidgetDataModel);
        // NumberDataModel.prototype.constructor = this.constructor;

    }

	// NumberDataModel.prototype = Object.create(SocketConnectionDataModel.prototype);
	// function NumberDataModel(id) {
	// 	SocketConnectionDataModel.call(this, id);
	// }

	newData(message) {
		this.updateScope(message);
	}

	// return NumberDataModel;
};

NumberDataModel.$inject = ['WidgetDataModel', '$http'];
