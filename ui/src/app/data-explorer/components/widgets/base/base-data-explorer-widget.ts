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

import {
  Directive,
  EventEmitter,
  Input,
  OnChanges, OnDestroy,
  OnInit,
  Output,
  SimpleChanges
} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {GridsterItem, GridsterItemComponent} from 'angular-gridster2';
import {DateRange} from '../../../../core-model/datalake/DateRange';
import {DatalakeRestService} from '../../../../core-services/datalake/datalake-rest.service';
import {IDataViewDashboardItem} from '../../../models/dataview-dashboard.model';
import {
  DashboardWidgetModel,
  DataExplorerWidgetModel,
  DataLakeMeasure,
  EventProperty,
  EventPropertyPrimitive,
  EventPropertyUnion,
  EventSchema
} from "../../../../core-model/gen/streampipes-model";
import {WidgetConfigurationService} from "../../../services/widget-configuration.service";

@Directive()
export abstract class BaseDataExplorerWidget<T extends DataExplorerWidgetModel> implements OnInit, OnChanges, OnDestroy {

  @Output()
  removeWidgetCallback: EventEmitter<boolean> = new EventEmitter();

  @Input() gridsterItem: GridsterItem;
  @Input() gridsterItemComponent: GridsterItemComponent;
  @Input() editMode: boolean;

  @Input()
  viewDateRange: DateRange;


  @Input() dataViewDashboardItem: IDataViewDashboardItem;
  @Input() dataExplorerWidget: T;
  @Input() dataLakeMeasure: DataLakeMeasure;

  public selectedProperties: string[];

  public showNoDataInDateRange: boolean;
  public showData: boolean;
  public showIsLoadingData: boolean;

  constructor(protected dataLakeRestService: DatalakeRestService,
              protected dialog: MatDialog,
              protected widgetConfigurationService: WidgetConfigurationService) {

  }

  ngOnInit(): void {
    this.widgetConfigurationService.configurationChangedSubject.subscribe(refreshMessage => {
      if (refreshMessage.widgetId === this.dataExplorerWidget._id) {
        if (refreshMessage.refreshData) {
          this.refreshData();
        }

        if (refreshMessage.refreshView) {
          this.refreshView();
        }
      }
    })
  }

  ngOnDestroy(): void {
    this.widgetConfigurationService.configurationChangedSubject.unsubscribe();
  }

  public removeWidget() {
    this.removeWidgetCallback.emit(true);
  }

  public setShownComponents(showNoDataInDateRange: boolean,
                            showData: boolean,
                            showIsLoadingData: boolean) {

    this.showNoDataInDateRange = showNoDataInDateRange;
    this.showData = showData;
    this.showIsLoadingData = showIsLoadingData;
  }

  ngOnChanges(changes: SimpleChanges) {
    this.viewDateRange = changes.viewDateRange.currentValue;
    this.updateData();
  }

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
    const b = new EventPropertyPrimitive();
    b["@class"] = "org.apache.streampipes.model.schema.EventPropertyPrimitive";
    b.runtimeType = 'https://www.w3.org/2001/XMLSchema#string';
    b.runtimeName = '';

    result.push(b);

    eventSchema.eventProperties.forEach(p => {
      if (!(p.domainProperties.some(dp => dp === 'http://schema.org/DateTime')) && !this.isNumber(p)) {
        result.push(p);
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

  isNumber(p: EventPropertyUnion): boolean {
    if (p instanceof EventPropertyPrimitive) {
      const runtimeType = (p as EventPropertyPrimitive).runtimeType;

      return runtimeType === 'http://schema.org/Number' ||
      runtimeType === 'http://www.w3.org/2001/XMLSchema#float' ||
      runtimeType === 'http://www.w3.org/2001/XMLSchema#double' ||
      runtimeType === 'http://www.w3.org/2001/XMLSchema#integer' ||
      runtimeType === 'https://schema.org/Number' ||
      runtimeType === 'https://www.w3.org/2001/XMLSchema#float' ||
      runtimeType === 'https://www.w3.org/2001/XMLSchema#double' ||
      runtimeType === 'https://www.w3.org/2001/XMLSchema#integer'
        ? true : false;
    } else {
      return  false;
    }
  }

  public isTimestamp(p: EventProperty) {
    return p.domainProperties.some(dp => dp === 'http://schema.org/DateTime');
  }

  getRuntimeNames(properties: EventPropertyUnion[]): string[] {
    const result = [];
    properties.forEach(p => {
        result.push(p.runtimeName);
    });

    return result;
  }

  public updateData() {
    this.refreshData();
    this.refreshView();
  }

  public abstract refreshData();

  public abstract refreshView();


}
