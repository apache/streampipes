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

import { inject, Injectable } from '@angular/core';
import { GaugeWidgetModel } from './model/gauge-widget.model';
import { EChartsOption, GaugeSeriesOption } from 'echarts';
import { FieldUpdateInfo } from '../../../models/field-update.model';
import {
    DataExplorerField,
    SpQueryResult,
} from '@streampipes/platform-services';
import {
    SpEchartsRenderer,
    WidgetEchartsAppearanceConfig,
} from '../../../models/dataview-dashboard.model';
import { WidgetSize } from '../../../models/dataset.model';
import { EchartsBasicOptionsGeneratorService } from '../../../echarts-renderer/echarts-basic-options-generator.service';
import { SpFieldUpdateService } from '../../../services/field-update.service';

@Injectable({ providedIn: 'root' })
export class SpGaugeRendererService
    implements SpEchartsRenderer<GaugeWidgetModel>
{
    protected fieldUpdateService = inject(SpFieldUpdateService);
    protected echartsBaseOptionsGenerator = inject(
        EchartsBasicOptionsGeneratorService,
    );

    makeSeriesItem(
        seriesName: string,
        fieldName: string,
        value: number,
        widgetConfig: GaugeWidgetModel,
    ): GaugeSeriesOption {
        const visConfig = widgetConfig.visualizationConfig;
        return {
            name: seriesName,
            type: 'gauge',
            progress: {
                show: true,
            },
            detail: {
                show: true,
                valueAnimation: false,
                formatter: '{value}',
            },
            min: visConfig.min,
            max: visConfig.max,
            data: [
                {
                    value: value,
                    name: fieldName,
                },
            ],
        };
    }

    getSelectedField(widgetConfig: GaugeWidgetModel): DataExplorerField {
        return widgetConfig.visualizationConfig.selectedProperty;
    }

    handleUpdatedFields(
        fieldUpdateInfo: FieldUpdateInfo,
        widgetConfig: GaugeWidgetModel,
    ): void {
        this.fieldUpdateService.updateAnyField(
            this.getSelectedField(widgetConfig),
            fieldUpdateInfo,
        );
    }

    render(
        queryResult: SpQueryResult[],
        widgetConfig: GaugeWidgetModel,
        _widgetSize: WidgetSize,
    ): EChartsOption {
        const option = this.echartsBaseOptionsGenerator.makeBaseConfig(
            widgetConfig.baseAppearanceConfig as WidgetEchartsAppearanceConfig,
            {},
        );
        const selectedField = this.getSelectedField(widgetConfig);
        const sourceIndex = selectedField.sourceIndex;
        const dataSeries = queryResult[sourceIndex].allDataSeries[0];
        const columnIndex = dataSeries.headers.indexOf(
            selectedField.fullDbName,
        );
        const data = parseFloat(dataSeries.rows[0][columnIndex].toFixed(2));
        Object.assign(option, {
            grid: {
                width: '100%',
                height: '100%',
            },
            series: this.makeSeriesItem(
                '',
                selectedField.fullDbName,
                data,
                widgetConfig,
            ),
        });

        return option;
    }
}
