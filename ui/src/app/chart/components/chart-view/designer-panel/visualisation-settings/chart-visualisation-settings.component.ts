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
    Input,
    OnChanges,
    OnInit,
    SimpleChanges,
    inject,
} from '@angular/core';
import { DataExplorerWidgetModel } from '@streampipes/platform-services';
import { ChartTypeService } from '../../../../../chart-shared/services/chart-type.service';
import {
    MatOption,
    MatSelect,
    MatSelectChange,
    MatSelectTrigger,
} from '@angular/material/select';
import { IWidget } from '../../../../../chart-shared/models/dataview-dashboard.model';
import { ChartRegistry } from '../../../../../chart-shared/registry/chart-registry.service';
import { FlexDirective, LayoutDirective } from '@ngbracket/ngx-layout/flex';
import { SplitSectionComponent } from '@streampipes/shared-ui';
import { MatFormField } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { NgComponentOutlet } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-chart-visualisation-settings',
    templateUrl: './chart-visualisation-settings.component.html',
    styleUrls: ['./chart-visualisation-settings.component.scss'],
    imports: [
        FlexDirective,
        LayoutDirective,
        SplitSectionComponent,
        MatFormField,
        MatSelect,
        MatSelectTrigger,
        MatOption,
        MatIcon,
        NgComponentOutlet,
        TranslatePipe,
    ],
})
export class ChartVisualisationSettingsComponent implements OnInit, OnChanges {
    private widgetTypeService = inject(ChartTypeService);
    private widgetRegistryService = inject(ChartRegistry);

    @Input() currentlyConfiguredWidget: DataExplorerWidgetModel;

    availableWidgets: IWidget<any>[];
    activeWidgetType: IWidget<any>;

    ngOnInit(): void {
        this.availableWidgets =
            this.widgetRegistryService.getAvailableChartTemplates();
        this.selectWidget();
    }

    ngOnChanges(changes: SimpleChanges) {
        if (changes.currentlyConfiguredWidget) {
            this.selectWidget();
        }
    }

    selectWidget(): void {
        this.activeWidgetType = this.widgetRegistryService.getChartTemplate(
            this.currentlyConfiguredWidget.widgetType,
        );
    }

    triggerWidgetTypeChange(ev: MatSelectChange) {
        this.widgetTypeService.notify({
            widgetId: this.currentlyConfiguredWidget.elementId,
            newWidgetTypeId: ev.value,
        });
        this.selectWidget();
    }
}
