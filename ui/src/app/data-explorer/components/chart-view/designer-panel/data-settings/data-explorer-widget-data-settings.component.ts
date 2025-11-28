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
    SourceConfig,
} from '@streampipes/platform-services';
import { Tuple2 } from '../../../../../core-model/base/Tuple2';
import { Router } from '@angular/router';
import { ChartConfigurationService } from '../../../../../data-explorer-shared/services/chart-configuration.service';
import { FieldSelectionPanelComponent } from './field-selection-panel/field-selection-panel.component';
import { GroupSelectionPanelComponent } from './group-selection-panel/group-selection-panel.component';
import { TableVisConfig } from '../../../../../data-explorer-shared/components/charts/table/model/table-widget.model';
import { ChartFieldProviderService } from '../../../../../data-explorer-shared/services/chart-field-provider.service';
import { FieldProvider } from '../../../../../data-explorer-shared/models/dataview-dashboard.model';
import { ChartTypeService } from '../../../../../data-explorer-shared/services/chart-type.service';

@Component({
    selector: 'sp-data-explorer-widget-data-settings',
    templateUrl: './data-explorer-widget-data-settings.component.html',
    styleUrls: ['./data-explorer-widget-data-settings.component.scss'],
    standalone: false,
})
export class DataExplorerWidgetDataSettingsComponent implements OnInit {
    @Input() dataConfig: DataExplorerDataConfig;
    @Input() dataLakeMeasure: DataLakeMeasure;
    @Input() newWidgetMode: boolean;
    @Input() widgetId: string;
    @Input() currentlyConfiguredWidget: DataExplorerWidgetModel;

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

    availableMeasurements: DataLakeMeasure[] = [];

    step = 0;

    expandFieldsDataSource = true;
    expandFieldsQuery = true;

    constructor(
        private datalakeRestService: DatalakeRestService,
        private widgetConfigService: ChartConfigurationService,
        private fieldProviderService: ChartFieldProviderService,
        private widgetTypeService: ChartTypeService,
        private router: Router,
    ) {}

    ngOnInit(): void {
        this.loadPipelinesAndMeasurements();
    }

    loadPipelinesAndMeasurements() {
        this.datalakeRestService
            .getAllMeasurementSeries()
            .subscribe(response => {
                this.availableMeasurements = response;
                this.availableMeasurements.sort((a, b) =>
                    a.measureName.localeCompare(b.measureName),
                );

                if (!this.dataConfig.sourceConfigs) {
                    const defaultConfigs = this.findDefaultConfig();
                    this.addDataSource(defaultConfigs.measureName);
                    if (defaultConfigs.measureName !== undefined) {
                        this.updateMeasure(
                            this.dataConfig.sourceConfigs[0],
                            defaultConfigs.measureName,
                        );
                    }
                }
            });
    }

    findDefaultConfig(): {
        measureName: string;
    } {
        if (this.availableMeasurements.length > 0) {
            return {
                measureName: this.availableMeasurements[0].measureName,
            };
        } else {
            return { measureName: undefined };
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
        return this.availableMeasurements.find(
            m => m.measureName === measureName,
        );
    }

    setStep(index: number) {
        this.step = index;
    }

    changeDataAggregation() {
        this.fieldSelectionPanel.applyDefaultFields();
        this.triggerDataRefresh();
    }

    addDataSource(measureName = '') {
        if (!this.dataConfig.sourceConfigs) {
            this.dataConfig.sourceConfigs = [];
        }
        this.dataConfig.sourceConfigs.push(this.makeSourceConfig(measureName));
    }

    makeSourceConfig(measureName = ''): SourceConfig {
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
        };
    }

    makeVisualizationConfig(fields: FieldProvider): TableVisConfig {
        return {
            configurationValid: true,
            searchValue: '',
            selectedColumns: fields.allFields,
        };
    }

    createDefaultWidget(): void {
        if (this.checkIfDefaultTableShouldBeShown()) {
            const fields = this.fieldProviderService.generateFieldLists(
                this.dataConfig.sourceConfigs,
            );
            this.currentlyConfiguredWidget.visualizationConfig =
                this.makeVisualizationConfig(fields);
            this.currentlyConfiguredWidget.widgetType = 'table';
            this.widgetTypeService.notify({
                widgetId: this.currentlyConfiguredWidget.elementId,
                newWidgetTypeId: this.currentlyConfiguredWidget.widgetType,
            });

            this.createWidgetEmitter.emit({
                a: this.dataLakeMeasure,
                b: this.currentlyConfiguredWidget,
            });
        }
    }

    /**
     * This method checks if there is at least one data source and that no widget type is already configured.
     */
    checkIfDefaultTableShouldBeShown(): boolean {
        return (
            this.dataConfig.sourceConfigs.length === 1 &&
            !this.currentlyConfiguredWidget.widgetType
        );
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
            refreshData: true,
            refreshView: true,
        });
    }

    toggleExpandFieldsDataSource() {
        this.expandFieldsDataSource = !this.expandFieldsDataSource;
    }

    toggleExpandFieldsQuery() {
        this.expandFieldsQuery = !this.expandFieldsQuery;
    }

    navigateToConnect(): void {
        this.router.navigate(['connect']);
    }

    navigateToPipelines(): void {
        this.router.navigate(['pipelines']);
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
