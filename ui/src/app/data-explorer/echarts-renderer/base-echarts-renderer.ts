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
    FieldProvider,
    SpEchartsRenderer,
} from '../models/dataview-dashboard.model';
import {
    DataExplorerField,
    DataExplorerWidgetModel,
    SpQueryResult,
} from '@streampipes/platform-services';
import { EChartsOption } from 'echarts';
import { GeneratedDataset, WidgetSize } from '../models/dataset.model';
import { DataTransformOption } from 'echarts/types/src/data/helper/transform';
import { inject } from '@angular/core';
import { SpFieldUpdateService } from '../services/field-update.service';
import { DataExplorerFieldProviderService } from '../services/data-explorer-field-provider-service';
import { EchartsAxisGeneratorService } from './echarts-axis-generator.service';
import { EchartsBasicOptionsGeneratorService } from './echarts-basic-options-generator.service';
import { EchartsDatasetGeneratorService } from './echarts-dataset-generator.service';
import { EchartsGridGeneratorService } from './echarts-grid-generator.service';
import { EchartsUtilsService } from './echarts-utils.service';

export abstract class SpBaseEchartsRenderer<T extends DataExplorerWidgetModel>
    implements SpEchartsRenderer<T>
{
    protected fieldUpdateService = inject(SpFieldUpdateService);
    protected fieldProvider = inject(DataExplorerFieldProviderService);

    protected basicOptionsGeneratorService = inject(
        EchartsBasicOptionsGeneratorService,
    );
    protected axisGeneratorService = inject(EchartsAxisGeneratorService);
    protected datasetGeneratorService = inject(
        EchartsDatasetGeneratorService<T>,
    );
    protected gridGeneratorService = inject(EchartsGridGeneratorService);
    protected echartsUtilsService = inject(EchartsUtilsService);

    render(
        spQueryResult: SpQueryResult[],
        widgetConfig: T,
        widgetSize: WidgetSize,
    ): EChartsOption {
        const options = this.basicOptionsGeneratorService.makeBaseConfig();
        const datasets = this.datasetGeneratorService.toDataset(
            spQueryResult,
            widgetConfig,
            (widgetConfig, index) =>
                this.initialTransforms(widgetConfig, index),
        );
        this.applyOptions(datasets, options, widgetConfig, widgetSize);
        return options;
    }

    abstract applyOptions(
        datasets: GeneratedDataset,
        options: EChartsOption,
        widgetConfig: T,
        widgetSize: WidgetSize,
    ): void;

    initialTransforms(
        widgetConfig: T,
        sourceIndex: number,
    ): DataTransformOption[] {
        return [];
    }

    abstract performFieldUpdate(
        widgetConfig: T,
        fieldProvider: FieldProvider,
        addedFields: DataExplorerField[],
        removedFields: DataExplorerField[],
    ): void;
}
