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

import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { StaticPropertyExtractor } from '../../../sdk/extractor/static-property-extractor';
import { BaseDataExplorerWidget } from '../base/base-data-explorer-widget';
import { TableConfig } from './table-config';

@Component({
    selector: 'sp-data-explorer-table-widget',
    templateUrl: './table-widget.component.html',
    styleUrls: ['./table-widget.component.css']
})
export class TableWidgetComponent extends BaseDataExplorerWidget implements OnInit, OnDestroy {

    item: any;

    selectedProperty: string;

    constructor() {
        super();
    }

    ngOnInit(): void {
        super.ngOnInit();
    }

    ngOnDestroy(): void {
        super.ngOnDestroy();
    }

    extractConfig(extractor: StaticPropertyExtractor) {
        this.selectedProperty = extractor.mappingPropertyValue(TableConfig.NUMBER_MAPPING_KEY);
    }

    isNumber(item: any): boolean {
        return false;
    }

    protected onEvent(event: any) {
        this.item = event[this.selectedProperty];
    }

}
