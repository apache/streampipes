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

import { Component, OnInit, ViewChild } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { BaseDataExplorerWidgetDirective } from '../base/base-data-explorer-widget.directive';
import {
    DataExplorerField,
    DatalakeQueryParameterBuilder,
    DatalakeQueryParameters,
    EventPropertyUnion,
    SpQueryResult,
} from '@streampipes/platform-services';
import { ImageWidgetModel } from './model/image-widget.model';
import { SecurePipe } from '../../../../services/secure.pipe';

@Component({
    selector: 'sp-data-explorer-image-widget',
    templateUrl: './image-widget.component.html',
    styleUrls: ['./image-widget.component.css'],
})
export class ImageWidgetComponent
    extends BaseDataExplorerWidgetDirective<ImageWidgetModel>
    implements OnInit
{
    @ViewChild(MatSort, { static: true }) sort: MatSort;

    imageBaseUrl: string;
    imagePaths = [];

    availableColumns: EventPropertyUnion[];
    selectedColumn: EventPropertyUnion;

    canvasHeight;
    canvasWidth;
    imagePreviewHeight;

    constructor(private securePipe: SecurePipe) {
        super();
    }

    ngOnInit(): void {
        super.ngOnInit();
        this.onResize(
            this.gridsterItemComponent.width,
            this.gridsterItemComponent.height - 40,
        );
        this.imageBaseUrl = this.dataLakeRestService.dataLakeUrl + '/images/';
    }

    refreshView() {}

    buildQuery(): DatalakeQueryParameters {
        return DatalakeQueryParameterBuilder.create(
            this.timeSettings.startTime,
            this.timeSettings.endTime,
        ).build();
    }

    onResize(width: number, height: number) {
        this.canvasHeight = height - 50;
        this.canvasWidth = width - 20;
        this.imagePreviewHeight = width / 14;
    }

    beforeDataFetched() {
        this.setShownComponents(false, false, true, false);
    }

    onDataReceived(spQueryResult: SpQueryResult[]) {
        const selectedField =
            this.dataExplorerWidget.visualizationConfig.selectedField;
        if (spQueryResult.length > 0) {
            const qr = spQueryResult[selectedField.sourceIndex];
            const columnIndex = qr.headers.indexOf(selectedField.runtimeName);
            this.imagePaths = qr.allDataSeries[0].rows
                .map(row => row[columnIndex])
                .map(imageId => this.imageBaseUrl + imageId)
                .map(imageRoute => this.securePipe.transform(imageRoute));
        }
        this.setShownComponents(false, true, false, false);
    }

    handleUpdatedFields(
        addedFields: DataExplorerField[],
        removedFields: DataExplorerField[],
    ) {}
}
