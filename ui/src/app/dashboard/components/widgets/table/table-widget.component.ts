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

import {Component, OnDestroy, OnInit} from "@angular/core";
import {BaseStreamPipesWidget} from "../base/base-widget";
import {RxStompService} from "@stomp/ng2-stompjs";
import {StaticPropertyExtractor} from "../../../sdk/extractor/static-property-extractor";
import {MatTableDataSource} from "@angular/material/table";
import {TableConfig} from "./table-config";
import {SemanticTypeUtilsService} from "../../../../core-services/semantic-type/semantic-type-utils.service";
import {ResizeService} from "../../../services/resize.service";

@Component({
    selector: 'table-widget',
    templateUrl: './table-widget.component.html',
    styleUrls: ['./table-widget.component.css']
})
export class TableWidgetComponent extends BaseStreamPipesWidget implements OnInit, OnDestroy {

    selectedProperties: Array<string>;

    displayedColumns: String[] = [];
    dataSource = new MatTableDataSource();
    semanticTypes: { [key: string]: string; } = {};

    constructor(rxStompService: RxStompService, resizeService: ResizeService, private semanticTypeUtils: SemanticTypeUtilsService) {
        super(rxStompService, resizeService, false);
    }

    ngOnInit(): void {
        super.ngOnInit();

        this.widgetDataConfig.schema.eventProperties.forEach((key, index) => {
            this.semanticTypes[key.runtimeName] = key.domainProperty
        });
    }

    ngOnDestroy(): void {
        super.ngOnDestroy();
    }

    extractConfig(extractor: StaticPropertyExtractor) {
        this.selectedProperties = extractor.mappingPropertyValues(TableConfig.SELECTED_PROPERTIES_KEYS);
    }

    protected onEvent(event: any) {
        this.dataSource.data.unshift(this.createTableObject(event));
        if (this.dataSource.data.length > 10) {
            this.dataSource.data.pop();
        }
        this.dataSource.data = [...this.dataSource.data];
    }

    createTableObject(event: any) {
        let object = {};
        this.selectedProperties.forEach((key, index) => {
            event[key] = this.semanticTypeUtils.getValue(event[key], this.semanticTypes[key]);
            object[key] = event[key];
        });
        return object;
    }

    protected onSizeChanged(width: number, height: number) {
    }

}