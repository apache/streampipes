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

import {Directive, Input, OnChanges} from "@angular/core";
import {
  DataExplorerWidgetModel, DataLakeMeasure,
  EventProperty,
  EventPropertyUnion,
  EventSchema
} from "../../../../core-model/gen/streampipes-model";
import {WidgetConfigurationService} from "../../../services/widget-configuration.service";

@Directive()
export abstract class BaseWidgetConfig<T extends DataExplorerWidgetModel>  {

  @Input() currentlyConfiguredWidget: T;
  @Input() dataLakeMeasure: DataLakeMeasure;

  constructor(protected widgetConfigurationService: WidgetConfigurationService) {

  }

  triggerDataRefresh() {
    this.widgetConfigurationService.notify({widgetId: this.currentlyConfiguredWidget._id, refreshData: true, refreshView: false});
  }

  triggerViewRefresh() {
    this.widgetConfigurationService.notify({widgetId: this.currentlyConfiguredWidget._id, refreshData: false, refreshView: true});
  }

  getTimestampProperty(eventSchema: EventSchema) {
    const propertyKeys: string[] = [];

    const result = eventSchema.eventProperties.find(p =>
        this.isTimestamp(p)
    );

    return result;
  }

  public isTimestamp(p: EventProperty) {
    return p.domainProperties.some(dp => dp === 'http://schema.org/DateTime');
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

  getRuntimeNames(properties: EventPropertyUnion[]): string[] {
    const result = [];
    properties.forEach(p => {
      result.push(p.runtimeName);
    });

    return result;
  }
}
