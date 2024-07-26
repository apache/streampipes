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
    Component,
    EventEmitter,
    Input,
    Output,
    ViewChild,
} from '@angular/core';
import {
    DataExplorerWidgetModel,
    DataLakeMeasure,
} from '@streampipes/platform-services';
import { Tuple2 } from '../../../../core-model/base/Tuple2';
import { DataExplorerWidgetDataSettingsComponent } from './data-settings/data-explorer-widget-data-settings.component';

@Component({
    selector: 'sp-data-explorer-designer-panel',
    templateUrl: './data-explorer-designer-panel.component.html',
    styleUrls: ['./data-explorer-designer-panel.component.scss'],
})
export class DataExplorerDesignerPanelComponent {
    @Input() currentlyConfiguredWidget: DataExplorerWidgetModel;
    @Input() dataLakeMeasure: DataLakeMeasure;
    @Input() newWidgetMode = false;

    @Output() addWidgetEmitter: EventEmitter<
        Tuple2<DataLakeMeasure, DataExplorerWidgetModel>
    > = new EventEmitter<Tuple2<DataLakeMeasure, DataExplorerWidgetModel>>();

    @Output() closeDesignerPanelEmitter = new EventEmitter();

    selectedIndex = 0;

    dataSettingsPanel: DataExplorerWidgetDataSettingsComponent;

    selectOptionsPanel(index: number) {
        this.selectedIndex = index;
    }

    createNewWidget() {
        this.newWidgetMode = false;

        // Set default name to the measure name
        if (
            this.currentlyConfiguredWidget.dataConfig.sourceConfigs.length > 0
        ) {
            this.currentlyConfiguredWidget.baseAppearanceConfig.widgetTitle =
                this.currentlyConfiguredWidget.dataConfig.sourceConfigs[0]
                    .measureName +
                ' - ' +
                this.currentlyConfiguredWidget.widgetType;
        }

        this.addWidgetEmitter.emit({
            a: this.dataLakeMeasure,
            b: this.currentlyConfiguredWidget,
        });
    }

    @ViewChild('dataSettingsPanel')
    public set content(
        dataSettingsPanel: DataExplorerWidgetDataSettingsComponent,
    ) {
        this.dataSettingsPanel = dataSettingsPanel;
    }
}
