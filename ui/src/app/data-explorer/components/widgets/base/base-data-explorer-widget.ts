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

import { EventEmitter, Input, OnChanges, Output, SimpleChanges, Directive } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { GridsterItem, GridsterItemComponent } from 'angular-gridster2';
import { DateRange } from '../../../../core-model/datalake/DateRange';
import { DatalakeRestService } from '../../../../core-services/datalake/datalake-rest.service';
import { IDataViewDashboardItem } from '../../../models/dataview-dashboard.model';
import { DataDownloadDialog } from '../../datadownloadDialog/dataDownload.dialog';
import {
  DataExplorerWidgetModel,
  EventProperty, EventPropertyPrimitive, EventPropertyUnion,
  EventSchema
} from "../../../../core-model/gen/streampipes-model";

@Directive()
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
    const propertyKeys: EventPropertyUnion[] = [];

    eventSchema.eventProperties.forEach(p => {
      if (!(p.domainProperties.some(dp => dp === 'http://schema.org/DateTime'))) {
        propertyKeys.push(p);
      }
    });

    return propertyKeys;
  }

  getNumericProperty(eventSchema: EventSchema) {
    const propertyKeys: EventPropertyUnion[] = [];

    eventSchema.eventProperties.forEach(p => {
      if (!(p.domainProperties.some(dp => dp === 'http://schema.org/DateTime')) && this.isNumber(p)) {
        propertyKeys.push(p);
      }
    });

    return propertyKeys;
  }

  getDimensionProperties(eventSchema: EventSchema) {
    const result: EventPropertyUnion[] = [];
    eventSchema.eventProperties.forEach(property => {
      if (property.propertyScope === 'DIMENSION_PROPERTY') {
        result.push(property);
      }
    });

    return result;
  }

  getNonNumericProperties(eventSchema: EventSchema) {
    const result: EventPropertyUnion[] = [];
    eventSchema.eventProperties.forEach(p => {
      if (!(p.domainProperties.some(dp => dp === 'http://schema.org/DateTime')) && !this.isNumber(p)) {
        result.push(p);
      }
    });

    const b = new EventPropertyPrimitive();
    b["@class"] = "org.apache.streampipes.model.schema.EventPropertyPrimitive";
    b.runtimeType = 'https://www.w3.org/2001/XMLSchema#string';
    b.runtimeName = 'sp_internal_label';
    result.push(b);

    return result;
  }

  getTimestampProperty(eventSchema: EventSchema) {
    const propertyKeys: string[] = [];

    const result = eventSchema.eventProperties.find(p =>
      this.isTimestamp(p)
    );

    return result;
  }

  isNumber(p: EventPropertyUnion): boolean {
    return (p instanceof EventPropertyPrimitive &&
      ((p as EventPropertyPrimitive).runtimeType === 'http://www.w3.org/2001/XMLSchema#number') ||
      (p as EventPropertyPrimitive).runtimeType === 'http://www.w3.org/2001/XMLSchema#float') ||
    ((p as EventPropertyPrimitive).runtimeType === 'http://www.w3.org/2001/XMLSchema#double') ||
    ((p as EventPropertyPrimitive).runtimeType === 'http://www.w3.org/2001/XMLSchema#integer')
      ? true : false;
  }

  public isTimestamp(p: EventProperty) {
    return p.domainProperties.some(dp => dp === 'http://schema.org/DateTime');
  }

  getRuntimeNames(properties: EventProperty[]): string[] {
    const result = [];
    properties.forEach(p => {
        result.push(p.runtimeName);
    });

    return result;
  }
}
