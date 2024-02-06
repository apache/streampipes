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
    OnInit,
    Output,
    ViewChild,
} from '@angular/core';
import {
    DataExplorerDataConfig,
    DataExplorerWidgetModel,
    DataLakeMeasure,
    DatalakeRestService,
    DataViewDataExplorerService,
    SourceConfig,
} from '@streampipes/platform-services';
import { Tuple2 } from '../../../../core-model/base/Tuple2';
import { zip } from 'rxjs';
import { WidgetConfigurationService } from '../../../services/widget-configuration.service';
import { FieldSelectionPanelComponent } from './field-selection-panel/field-selection-panel.component';
import { GroupSelectionPanelComponent } from './group-selection-panel/group-selection-panel.component';

@Component({
    selector: 'sp-data-explorer-widget-data-settings',
    templateUrl: './data-explorer-widget-data-settings.component.html',
    styleUrls: ['./data-explorer-widget-data-settings.component.scss'],
})
export class DataExplorerWidgetDataSettingsComponent implements OnInit {
    @Input() dataConfig: DataExplorerDataConfig;
    @Input() dataLakeMeasure: DataLakeMeasure;
    @Input() newWidgetMode: boolean;
    @Input() widgetId: string;

    @Output() createWidgetEmitter: EventEmitter<
        Tuple2<DataLakeMeasure, DataExplorerWidgetModel>
    > = new EventEmitter<Tuple2<DataLakeMeasure, DataExplorerWidgetModel>>();
    @Output() dataLakeMeasureChange: EventEmitter<DataLakeMeasure> =
        new EventEmitter<DataLakeMeasure>();
    @Output() configureVisualizationEmitter: EventEmitter<void> =
        new EventEmitter<void>();

    @ViewChild('fieldSelectionPanel')
    fieldSelectionPanel: FieldSelectionPanelComponent;

    @ViewChild('groupSelectionPanel')
    groupSelectionPanel: GroupSelectionPanelComponent;

    availablePipelines: DataLakeMeasure[] = [];
    availableMeasurements: DataLakeMeasure[] = [];

    step = 0;

    constructor(
        private dataExplorerService: DataViewDataExplorerService,
        private datalakeRestService: DatalakeRestService,
        private widgetConfigService: WidgetConfigurationService,
    ) {}

    ngOnInit(): void {
        this.loadPipelinesAndMeasurements();
    }

    loadPipelinesAndMeasurements() {
        zip(
            this.dataExplorerService.getAllPersistedDataStreams(),
            this.datalakeRestService.getAllMeasurementSeries(),
        ).subscribe(response => {
            this.availablePipelines = response[0];
            this.availableMeasurements = response[1];

            // replace pipeline event schemas. Reason: Available measures do not contain field for timestamp
            this.availablePipelines.forEach(p => {
                const measurement = this.availableMeasurements.find(m => {
                    return m.measureName === p.measureName;
                });
                p.eventSchema = measurement.eventSchema;
            });

            if (!this.dataConfig.sourceConfigs) {
                const defaultConfigs = this.findDefaultConfig();
                this.addDataSource(
                    defaultConfigs.measureName,
                    defaultConfigs.sourceType,
                );
                if (defaultConfigs.measureName !== undefined) {
                    this.updateMeasure(
                        this.dataConfig.sourceConfigs[0],
                        defaultConfigs.measureName,
                    );
                }
            } else {
                this.checkSourceTypes();
            }
        });
    }

    checkSourceTypes() {
        this.dataConfig.sourceConfigs.forEach(sourceConfig => {
            if (
                sourceConfig.sourceType === 'pipeline' &&
                !this.existsPipelineWithMeasure(sourceConfig.measureName)
            ) {
                sourceConfig.sourceType = 'measurement';
            }
        });
    }

    existsPipelineWithMeasure(measureName: string) {
        return (
            this.availablePipelines.find(
                pipeline => pipeline.measureName === measureName,
            ) !== undefined
        );
    }

    findDefaultConfig(): {
        measureName: string;
        sourceType: 'pipeline' | 'measurement';
    } {
        if (this.availablePipelines.length > 0) {
            return {
                measureName: this.availablePipelines[0].measureName,
                sourceType: 'pipeline',
            };
        } else if (this.availableMeasurements.length > 0) {
            return {
                measureName: this.availableMeasurements[0].measureName,
                sourceType: 'measurement',
            };
        } else {
            return { measureName: undefined, sourceType: undefined };
        }
    }

    updateMeasure(sourceConfig: SourceConfig, measureName: string) {
        sourceConfig.measure = this.findMeasure(measureName);
        sourceConfig.queryConfig.fields = [];
        if (this.fieldSelectionPanel) {
            this.fieldSelectionPanel.applyDefaultFields();
        }

        sourceConfig.queryConfig.groupBy = [];
        if (this.groupSelectionPanel) {
            this.groupSelectionPanel.applyDefaultFields();
        }
        this.triggerDataRefresh();
    }

    findMeasure(measureName: string) {
        return (
            this.availablePipelines.find(
                pipeline => pipeline.measureName === measureName,
            ) ||
            this.availableMeasurements.find(m => m.measureName === measureName)
        );
    }

    setStep(index: number) {
        this.step = index;
    }

    changeDataAggregation() {
        this.fieldSelectionPanel.applyDefaultFields();
        this.triggerDataRefresh();
    }

    addDataSource(
        measureName = '',
        sourceType: 'pipeline' | 'measurement' = 'pipeline',
    ) {
        if (!this.dataConfig.sourceConfigs) {
            this.dataConfig.sourceConfigs = [];
        }
        this.dataConfig.sourceConfigs.push(
            this.makeSourceConfig(measureName, sourceType),
        );
    }

    makeSourceConfig(
        measureName = '',
        sourceType: 'pipeline' | 'measurement' = 'pipeline',
    ): SourceConfig {
        return {
            measureName,
            queryConfig: {
                selectedFilters: [],
                limit: 100,
                page: 1,
                aggregationTimeUnit: 'd',
                aggregationValue: 1,
            },
            queryType: 'raw',
            sourceType,
        };
    }

    removeSourceConfig(index: number) {
        this.dataConfig.sourceConfigs.splice(index, 1);
    }

    cloneSourceConfig(index: number) {
        const clonedConfig = this.deepCopy(
            this.dataConfig.sourceConfigs[index],
        );
        this.dataConfig.sourceConfigs.push(clonedConfig);
    }

    triggerDataRefresh() {
        this.widgetConfigService.notify({
            widgetId: this.widgetId,
            refreshData: true,
            refreshView: true,
        });
    }

    deepCopy(obj) {
        let copy;

        if (null == obj || 'object' !== typeof obj) {
            return obj;
        }

        if (obj instanceof Date) {
            copy = new Date();
            copy.setTime(obj.getTime());
            return copy;
        }

        if (obj instanceof Array) {
            copy = [];
            for (let i = 0, len = obj.length; i < len; i++) {
                copy[i] = this.deepCopy(obj[i]);
            }
            return copy;
        }

        if (obj instanceof Object) {
            copy = {};
            for (const attr in obj) {
                if (obj.hasOwnProperty(attr)) {
                    copy[attr] = this.deepCopy(obj[attr]);
                }
            }
            return copy;
        }

        throw new Error('Unable to copy.');
    }
}
