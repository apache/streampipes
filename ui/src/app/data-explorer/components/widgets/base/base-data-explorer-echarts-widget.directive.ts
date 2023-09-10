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

import { Directive, OnInit } from '@angular/core';
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

@Directive()
export abstract class BaseDataExplorerEchartsWidgetDirective<
        T extends DataExplorerWidgetModel,
    >
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

    ngOnInit(): void {
        super.ngOnInit();
        this.initOptions();
        this.renderSubjectSubscription = this.renderSubject
            .pipe(debounceTime(300))
            .subscribe(() => {
                this.renderChartOptions(this.latestData);
            });
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
        this.initOptions();
    }

    applySize(width: number, height: number) {
        if (this.eChartsInstance) {
            this.eChartsInstance.resize({ width, height });
        }
    }

    renderChartOptions(spQueryResult: SpQueryResult[]): void {
        this.option = {
            ...this.getRenderer().render(
                spQueryResult,
                this.dataExplorerWidget,
                { width: this.currentWidth, height: this.currentHeight },
            ),
        };
    }

    triggerFieldUpdate(
        selected: DataExplorerField,
        addedFields: DataExplorerField[],
        removedFields: DataExplorerField[],
    ): DataExplorerField {
        return this.updateSingleField(
            selected,
            this.fieldProvider.numericFields,
            addedFields,
            removedFields,
            field => field.fieldCharacteristics.numeric,
        );
    }

    refreshView() {
        this.renderSubject.next();
    }

    abstract getRenderer(): SpEchartsRenderer<T>;

    initOptions() {}

    public cleanupSubscriptions(): void {
        super.cleanupSubscriptions();
        this.renderSubjectSubscription.unsubscribe();
    }
}
