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
import { DataExplorerField } from '@streampipes/platform-services';
import {
    TimeSeriesChartVisConfig,
    TimeSeriesGroupColorMapping,
} from '../components/charts/time-series-chart/model/time-series-chart-widget.model';
import { TagValue } from '../models/dataset.model';
import { ColorMappingService } from './color-mapping.service';

/** Where a grouped series sits among the series of its chart. */
export interface SeriesPosition {
    /** Index of the group (tag value) within the field's groups. */
    groupIndex: number;
    /** Number of groups of the field. */
    groupCount: number;
    /** Index of the field among the chart's plotted fields. */
    fieldIndex: number;
}

@Injectable({ providedIn: 'root' })
export class ChartColorizationService {
    private colorMappingService = inject(ColorMappingService);

    /**
     * The colour of one series. Fields without grouping keep their configured
     * colour. Grouped fields use the custom mapping when configured, otherwise
     * the ordered chart palette: the n-th group gets the n-th palette colour,
     * and further fields continue after the groups of the previous field, so
     * every series of a chart is distinct and the same group keeps its colour
     * across charts of the same dataset.
     */
    makeColor(
        visualizationConfig: TimeSeriesChartVisConfig,
        field: DataExplorerField,
        tag?: TagValue,
        position?: SeriesPosition,
    ): string {
        const fieldKey = field.fullDbName + field.sourceIndex;
        const baseColor = visualizationConfig.chosenColor[fieldKey];

        if (!tag || tag.values.length === 0) {
            return baseColor;
        }

        const groupKey = this.makeGroupKey(tag);
        const fieldMappings =
            visualizationConfig.groupedColorMappings?.[fieldKey] ?? [];

        if (
            visualizationConfig.groupedColorMode?.[fieldKey] ===
            'custom_mapping'
        ) {
            return (
                this.findMapping(fieldMappings, tag, groupKey)?.color ??
                this.paletteColor(groupKey, position)
            );
        }

        return this.paletteColor(groupKey, position);
    }

    findLabel(
        visualizationConfig: TimeSeriesChartVisConfig,
        field: DataExplorerField,
        tag?: TagValue,
    ): string | undefined {
        if (!tag || tag.values.length === 0) {
            return undefined;
        }

        const fieldKey = field.fullDbName + field.sourceIndex;
        const fieldMappings =
            visualizationConfig.groupedColorMappings?.[fieldKey] ?? [];
        const mapping = this.findMapping(
            fieldMappings,
            tag,
            this.makeGroupKey(tag),
        );

        return mapping?.label?.trim() ? mapping.label : undefined;
    }

    private paletteColor(
        groupKey: string,
        position: SeriesPosition | undefined,
    ): string {
        if (!position) {
            return this.colorMappingService.getDefaultColor(groupKey);
        }
        return this.colorMappingService.getPaletteColor(
            position.fieldIndex * position.groupCount + position.groupIndex,
        );
    }

    private findMapping(
        mappings: TimeSeriesGroupColorMapping[],
        tag: TagValue,
        groupKey: string,
    ): TimeSeriesGroupColorMapping | undefined {
        const candidateKeys = this.makeCandidateKeys(tag, groupKey);
        return mappings.find(mapping => candidateKeys.includes(mapping.value));
    }

    private makeGroupKey(tag: TagValue): string {
        return tag.tagKeys
            .map((key, index) => `${key}=${tag.values[index]}`)
            .join(', ');
    }

    private makeCandidateKeys(tag: TagValue, groupKey: string): string[] {
        const valueStrings = tag.values.map(value => String(value));
        const keyValuePairs = tag.tagKeys.map(
            (key, index) => `${key}=${valueStrings[index]}`,
        );

        return [
            groupKey,
            valueStrings.join(', '),
            ...keyValuePairs,
            ...valueStrings,
        ];
    }
}
