/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import { SocketConnectionDataModel } from '../../socket-connection-data-model.service'

export class TableDataModel extends SocketConnectionDataModel {

	dataArray: any;
	dataArrayLength: any;

    constructor($http, id) {
        super($http, id);
        this.dataArray = [];
        this.dataArrayLength = 5;
    }

	newData(message) {
		if (this.dataArray.length > this.dataArrayLength - 1) {
			this.dataArray = this.dataArray.slice(Math.max(this.dataArray.length - this.dataArrayLength , 1));
		}

		this.dataArray.push(message);
		this.updateScope(this.dataArray);
	
	}
};

TableDataModel.$inject = ['$http'];
