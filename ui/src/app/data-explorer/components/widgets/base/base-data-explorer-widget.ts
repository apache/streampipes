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

import { EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { GridsterItem, GridsterItemComponent } from 'angular-gridster2';
import { EventProperty } from '../../../../connect/schema-editor/model/EventProperty';
import { EventPropertyPrimitive } from '../../../../connect/schema-editor/model/EventPropertyPrimitive';
import { EventSchema } from '../../../../connect/schema-editor/model/EventSchema';
import { DataExplorerWidgetModel } from '../../../../core-model/datalake/DataExplorerWidgetModel';
import { DateRange } from '../../../../core-model/datalake/DateRange';
import { DatalakeRestService } from '../../../../core-services/datalake/datalake-rest.service';
import { IDataViewDashboardItem } from '../../../models/dataview-dashboard.model';
import { DataDownloadDialog } from '../../datadownloadDialog/dataDownload.dialog';

export abstract class BaseDataExplorerWidget implements OnChanges {

  protected constructor(protected dataLakeRestService: DatalakeRestService, protected dialog: MatDialog) {
  }

  @Output()
  removeWidgetCallback: EventEmitter<boolean> = new EventEmitter();

  @Input() gridsterItem: GridsterItem;
  @Input() gridsterItemComponent: GridsterItemComponent;
  @Input() editMode: boolean;

  @Input()
  viewDateRange: DateRange;


  @Input() dataViewDashboardItem: IDataViewDashboardItem;
  @Input() dataExplorerWidget: DataExplorerWidgetModel;

  public selectedProperties: string[];

  public showNoDataInDateRange: boolean;
  public showData: boolean;
  public showIsLoadingData: boolean;

  public removeWidget() {
    this.removeWidgetCallback.emit(true);
  }

  public setShownComponents(showNoDataInDateRange: boolean,
                            showData: boolean,
                            showIsLoadingData: boolean,
                            ) {

    this.showNoDataInDateRange = showNoDataInDateRange;
    this.showData = showData;
    this.showIsLoadingData = showIsLoadingData;
  }

  ngOnChanges(changes: SimpleChanges) {
    this.viewDateRange = changes.viewDateRange.currentValue;
    this.updateData();
  }

  public abstract updateData();

  getValuePropertyKeys(eventSchema: EventSchema) {
    const propertyKeys: EventProperty[] = [];

    eventSchema.eventProperties.forEach(p => {
      if (p.domainProperty !== 'http://schema.org/DateTime') {
        propertyKeys.push(p);
      }
    });

    return propertyKeys;
  }

  getNumericProperty(eventSchema: EventSchema) {
    const propertyKeys: EventProperty[] = [];

    eventSchema.eventProperties.forEach(p => {
      if (p.domainProperty !== 'http://schema.org/DateTime' && this.isNumber(p)) {
        propertyKeys.push(p);
      }
    });

    return propertyKeys;
  }

  getDimenstionProperties(eventSchema: EventSchema) {
    const result: EventProperty[] = [];
    eventSchema.eventProperties.forEach(property => {
      if (property.propertyScope === 'DIMENSION_PROPERTY') {
        result.push(property);
      }
    });

    return result;
  }


  getTimestampProperty(eventSchema: EventSchema) {
    const propertyKeys: string[] = [];

    const result = eventSchema.eventProperties.find(p =>
      this.isTimestamp(p)
    );

    return result;
  }

  isNumber(p: EventProperty): boolean {
    return (p instanceof EventPropertyPrimitive &&
      ((p as EventPropertyPrimitive).runtimeType === 'http://www.w3.org/2001/XMLSchema#number') ||
      (p as EventPropertyPrimitive).runtimeType === 'http://www.w3.org/2001/XMLSchema#float') ||
    ((p as EventPropertyPrimitive).runtimeType === 'http://www.w3.org/2001/XMLSchema#double') ||
    ((p as EventPropertyPrimitive).runtimeType === 'http://www.w3.org/2001/XMLSchema#integer')
      ? true : false;
  }

  public isTimestamp(p: EventProperty) {
    return p.domainProperty === 'http://schema.org/DateTime';
  }

  getRuntimeNames(properties: EventProperty[]): string[] {
    const result = [];
    properties.forEach(p => {
        result.push(p.runtimeName);
    });

    return result;
  }

  downloadDataAsFile() {
    const dialogRef = this.dialog.open(DataDownloadDialog, {
      width: '600px',
      data: { index: this.dataExplorerWidget.dataLakeMeasure.measureName, date: this.viewDateRange },
      panelClass: 'custom-dialog-container'

    });
  }

}
