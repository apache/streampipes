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
import { GridsterItem, GridsterItemComponent } from 'angular-gridster2';
import { DataExplorerWidgetModel } from '../../../../core-model/datalake/DataExplorerWidgetModel';
import { DateRange } from '../../../../core-model/datalake/DateRange';
import { IDataViewDashboardItem } from '../../../models/dataview-dashboard.model';
import { EventSchema } from '../../../../connect/schema-editor/model/EventSchema';
import { EventPropertyPrimitive } from '../../../../connect/schema-editor/model/EventPropertyPrimitive';
import { EventPropertyComponent } from '../../../../connect/schema-editor/event-property/event-property.component';
import { EventProperty } from '../../../../connect/schema-editor/model/EventProperty';

export abstract class BaseDataExplorerWidget implements OnChanges {

  protected constructor() {
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

  public showNoDataInDateRange: boolean;
  public showData: boolean;
  public showIsLoadingData: boolean;


  public removeWidget() {
    this.removeWidgetCallback.emit(true);
  }

  public setShownComponents(showNoDataInDateRange: boolean,
                            showData: boolean, showIsLoadingData: boolean) {

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
    const propertyKeys: string[] = [];

    eventSchema.eventProperties.forEach(p => {
      if (p.domainProperty !== 'http://schema.org/DateTime') {
        propertyKeys.push(p.getRuntimeName());
      }
    });

    return propertyKeys;
  }

  getNumericPropertyKeys(eventSchema: EventSchema) {
    const propertyKeys: string[] = [];

    eventSchema.eventProperties.forEach(p => {
      if (p.domainProperty !== 'http://schema.org/DateTime' && this.isNumber(p)) {
        propertyKeys.push(p.getRuntimeName());
      }
    });

    return propertyKeys;
  }

  getTimestampPropertyKey(eventSchema: EventSchema) {
    const propertyKeys: string[] = [];

    const result = eventSchema.eventProperties.find(p =>
      p.domainProperty === 'http://schema.org/DateTime'
    );

    return result.getRuntimeName();
  }

  isNumber(p: EventProperty): boolean {
    return (p instanceof EventPropertyPrimitive &&
      ((p as EventPropertyPrimitive).runtimeType === 'http://www.w3.org/2001/XMLSchema#number') ||
      (p as EventPropertyPrimitive).runtimeType === 'http://www.w3.org/2001/XMLSchema#float') ||
      ((p as EventPropertyPrimitive).runtimeType === 'http://www.w3.org/2001/XMLSchema#double') ||
    ((p as EventPropertyPrimitive).runtimeType === 'http://www.w3.org/2001/XMLSchema#integer')
      ? true : false;
}

  // TODO add get a specific property
  getPropertyKeysOfStaticProperty(eventSchema) {

  }

}
