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

import * as angular from 'angular';

export class MeasurementUnitController {

    query: any;
    selectedItem: any;
    items: any;
    property: any;
    MeasurementUnitsService: any;

    constructor(MeasurementUnitsService) {
        this.MeasurementUnitsService = MeasurementUnitsService;
        this.query = {};
        this.selectedItem = "";
    }

    $onInit() {
        this.items = this.MeasurementUnitsService.getUnits();

        if (this.property != undefined && this.property != "") {
            angular.forEach(this.items, function (item) {
                if (item.resource == this.property) this.selectedItem = item;
            });
        }
    }

    querySearch(query) {
        var results = [];

        angular.forEach(this.items, function (item) {
            if (query == undefined || item.label.substring(0, query.length) === query) results.push(item);
        })

        return results;
    }

    searchTextChange(text) {

    }

    selectedItemChange(item) {
        if (item != undefined) this.property = item.resource;
    }

}

MeasurementUnitController.$inject = ['MeasurementUnitsService'];
