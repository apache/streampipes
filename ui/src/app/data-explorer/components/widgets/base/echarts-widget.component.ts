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

import { Component, OnInit } from '@angular/core';
import {
    DataExplorerField,
    DataExplorerWidgetModel,
    SpQueryResult,
} from '@streampipes/platform-services';
import { SpEchartsRenderer } from '../../../models/dataview-dashboard.model';
import { BaseDataExplorerWidgetDirective } from './base-data-explorer-widget.directive';
import { ECharts } from 'echarts/core';
import { EChartsOption } from 'echarts';
import { Subject, Subscription } from 'rxjs';
import { debounceTime } from 'rxjs/operators';

@Component({
    selector: 'sp-data-explorer-echarts-widget',
    templateUrl: './echarts-widget.component.html',
    styleUrls: ['./echarts-widget.component.scss'],
})
export class SpEchartsWidgetComponent<T extends DataExplorerWidgetModel>
    extends BaseDataExplorerWidgetDirective<T>
    implements OnInit
{
    eChartsInstance: ECharts;
    currentWidth: number;
    currentHeight: number;

    option: EChartsOption;

    configReady = false;
    latestData: SpQueryResult[];

    renderSubject = new Subject<void>();
    renderSubjectSubscription: Subscription;
    renderer: SpEchartsRenderer<T>;

    widgetTypeLabel: string;

    ngOnInit(): void {
        super.ngOnInit();
        this.renderer = this.getRenderer();
        this.renderSubjectSubscription = this.renderSubject
            .pipe(debounceTime(300))
            .subscribe(() => {
                this.renderChartOptions(this.latestData);
            });
        this.widgetTypeLabel = this.widgetRegistryService.getWidgetTemplate(
            this.dataExplorerWidget.widgetType,
        ).label;
    }

    beforeDataFetched() {}

    onDataReceived(spQueryResult: SpQueryResult[]) {
        this.renderChartOptions(spQueryResult);
        this.latestData = spQueryResult;
        this.setShownComponents(false, true, false, false);
    }

    onResize(width: number, height: number) {
        this.currentWidth = width;
        this.currentHeight = height;
        this.configReady = true;
        this.applySize(width, height);
        if (this.latestData) {
            this.renderSubject.next();
        }
    }

    onChartInit(ec: ECharts) {
        this.eChartsInstance = ec;
        this.applySize(this.currentWidth, this.currentHeight);
    }

    applySize(width: number, height: number) {
        if (this.eChartsInstance) {
            this.eChartsInstance.resize({ width, height });
        }
    }

    renderChartOptions(spQueryResult: SpQueryResult[]): void {
        if (this.dataExplorerWidget.visualizationConfig.configurationValid) {
            this.showInvalidConfiguration = false;
            this.option = {
                ...this.renderer.render(
                    spQueryResult,
                    this.dataExplorerWidget,
                    {
                        width: this.currentWidth,
                        height: this.currentHeight,
                    },
                ),
            };
        } else {
            this.showInvalidConfiguration = true;
        }
    }

    refreshView() {
        this.renderSubject.next();
    }

    public cleanupSubscriptions(): void {
        super.cleanupSubscriptions();
        this.renderSubjectSubscription.unsubscribe();
    }

    getRenderer(): SpEchartsRenderer<T> {
        const widgetType = this.widgetRegistryService.getWidgetTemplate(
            this.dataExplorerWidget.widgetType,
        );
        return widgetType.chartRenderer;
    }

    handleUpdatedFields(
        addedFields: DataExplorerField[],
        removedFields: DataExplorerField[],
    ) {
        this.renderer.handleUpdatedFields(
            {
                addedFields,
                removedFields,
                fieldProvider: this.fieldProvider,
            },
            this.dataExplorerWidget,
        );
    }
}
