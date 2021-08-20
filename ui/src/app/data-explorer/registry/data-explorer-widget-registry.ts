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

import { IWidget } from '../models/dataview-dashboard.model';

export class DataExplorerWidgetRegistry {

    // private static availableWidgets: WidgetConfig[] = [
    //     new TableConfig(),
    // ];
    //
    // static getAvailableWidgetTemplates(): DashboardWidgetSettings[] {
    //     const widgetTemplates = new Array<DashboardWidgetSettings>();
    //     this.availableWidgets.forEach(widget => widgetTemplates.push(widget.getConfig()));
    //     return widgetTemplates;
    // }

    private static availableWidgets: IWidget[] = [
        {id: 'table', label: 'Table'},
        {id: 'line-chart', label: 'Line Chart'},
        {id: 'image', label: 'Image'}
    ];

    static getAvailableWidgetTemplates(): IWidget[] {
        const widgetTemplates = new Array<IWidget>();
        this.availableWidgets.forEach(widget => widgetTemplates.push(widget));
        return widgetTemplates;
    }
}
