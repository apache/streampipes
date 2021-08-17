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

import { Directive, Input, OnChanges, SimpleChanges } from "@angular/core";
import {
  DataExplorerWidgetModel,
  DataLakeMeasure,
  EventProperty,
  EventPropertyPrimitive,
  EventPropertyUnion,
  EventSchema
} from "../../../../core-model/gen/streampipes-model";
import { WidgetConfigurationService } from "../../../services/widget-configuration.service";

@Directive()
export abstract class BaseWidgetConfig<T extends DataExplorerWidgetModel> implements OnChanges {

  @Input() currentlyConfiguredWidget: T;

  @Input()
  dataLakeMeasure: DataLakeMeasure;

  constructor(protected widgetConfigurationService: WidgetConfigurationService) {

  }

  ngOnChanges(changes: SimpleChanges) {
   if (changes.dataLakeMeasure && changes.dataLakeMeasure.currentValue.measureName !== this.dataLakeMeasure.measureName) {
     this.updateWidgetConfigOptions();
   }
  }

  triggerDataRefresh() {
    this.widgetConfigurationService.notify({widgetId: this.currentlyConfiguredWidget._id, refreshData: true, refreshView: false});
  }

  triggerViewRefresh() {
    this.widgetConfigurationService.notify({widgetId: this.currentlyConfiguredWidget._id, refreshData: false, refreshView: true});
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

  getDimensionProperties(eventSchema: EventSchema) {
    const result: EventPropertyUnion[] = [];
    eventSchema.eventProperties.forEach(property => {
      if (property.propertyScope === 'DIMENSION_PROPERTY') {
        result.push(property);
      }
    });

    return result;
  }

  getNonNumericProperties(eventSchema: EventSchema): EventPropertyUnion[] {
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

  getRuntimeNames(properties: EventPropertyUnion[]): string[] {
    const result = [];
    properties.forEach(p => {
      result.push(p.runtimeName);
    });

    return result;
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
          runtimeType === 'https://www.w3.org/2001/XMLSchema#integer';
    } else {
      return  false;
    }
  }

  public isTimestamp(p: EventProperty) {
    return p.domainProperties.some(dp => dp === 'http://schema.org/DateTime');
  }

  protected abstract updateWidgetConfigOptions();


}
