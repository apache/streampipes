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

import { Component, inject, OnInit } from '@angular/core';
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
import { ResizeEchartsService } from '../../../services/resize-echarts.service';

@Component({
    selector: 'sp-data-explorer-echarts-widget',
    templateUrl: './echarts-widget.component.html',
    styleUrls: ['./echarts-widget.component.scss'],
    standalone: false,
})
export class SpEchartsWidgetComponent<T extends DataExplorerWidgetModel>
    extends BaseDataExplorerWidgetDirective<T>
    implements OnInit
{
    eChartsInstance: ECharts;
    option: EChartsOption;

    configReady = false;
    latestData: SpQueryResult[];

    renderSubject = new Subject<void>();
    renderSubject$: Subscription;
    resizeEcharts$: Subscription;
    renderer: SpEchartsRenderer<T>;

    resizeEchartsService = inject(ResizeEchartsService);

    widgetTypeLabel: string;

    ngOnInit(): void {
        super.ngOnInit();
        this.renderer = this.getRenderer();
        this.resizeEcharts$ =
            this.resizeEchartsService.echartsResizeSubject.subscribe(width => {
                this.currentWidth = width;
                this.refreshView();
            });
        this.renderSubject$ = this.renderSubject
            .pipe(debounceTime(300))
            .subscribe(() => {
                this.renderChartOptions(this.latestData);
            });
        this.widgetTypeLabel = this.widgetRegistryService.getChartTemplate(
            this.dataExplorerWidget.widgetType,
        ).label;
    }

    beforeDataFetched() {}

    onDataReceived(spQueryResult: SpQueryResult[]) {
        this.latestData = spQueryResult;
        this.renderChartOptions(spQueryResult);
        this.setShownComponents(false, true, false, false);
    }

    onResize(width: number, height: number) {
        this.configReady = true;
        if (this.latestData) {
            this.refreshView();
        }
    }

    onChartInit(ec: ECharts) {
        this.eChartsInstance = ec;
    }

    renderChartOptions(spQueryResult: SpQueryResult[]): void {
        if (
            this.dataExplorerWidget.visualizationConfig.configurationValid ===
                undefined ||
            this.dataExplorerWidget.visualizationConfig.configurationValid ===
                true
        ) {
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
            if (this.kioskMode) {
                ['toolbox', 'visualMap'].forEach(key => {
                    const item = this.option[key];
                    if (item) {
                        (Array.isArray(item) ? item : [item]).forEach(
                            obj => (obj.show = false),
                        );
                    }
                });
                Object.assign(this.option, {
                    grid: {
                        left: 60,
                        right: 60,
                        top: 60,
                        bottom: 60,
                    },
                });
            }
        } else {
            this.showInvalidConfiguration = true;
        }
    }

    refreshView() {
        this.renderSubject.next();
    }

    public cleanupSubscriptions(): void {
        super.cleanupSubscriptions();
        this.resizeEcharts$?.unsubscribe();
        this.renderSubject$?.unsubscribe();
    }

    getRenderer(): SpEchartsRenderer<T> {
        const widgetType = this.widgetRegistryService.getChartTemplate(
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
