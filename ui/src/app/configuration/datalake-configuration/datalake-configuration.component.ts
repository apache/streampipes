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

import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { DataLakeConfigurationEntry } from './datalake-configuration-entry';
import {
    DatalakeQueryParameterBuilder,
    DatalakeQueryParameters,
    DatalakeRestService,
    DataViewDataExplorerService,
    EventSchema,
    FieldConfig,
    SpQueryResult,
} from '@streampipes/platform-services';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import {
    DialogRef,
    DialogService,
    PanelType,
    SpBreadcrumbService,
} from '@streampipes/shared-ui';
import { DeleteDatalakeIndexComponent } from '../dialog/delete-datalake-index/delete-datalake-index-dialog.component';
import { SpConfigurationTabs } from '../configuration-tabs';
import { SpConfigurationRoutes } from '../configuration.routes';
import { DataDownloadDialogComponent } from '../../core-ui/data-download-dialog/data-download-dialog.component';

@Component({
    selector: 'sp-datalake-configuration',
    templateUrl: './datalake-configuration.component.html',
    styleUrls: ['./datalake-configuration.component.css'],
})
export class DatalakeConfigurationComponent implements OnInit {
    tabs = SpConfigurationTabs.getTabs();

    @ViewChild(MatPaginator) paginator: MatPaginator;
    pageSize = 1;
    @ViewChild(MatSort) sort: MatSort;

    dataSource: MatTableDataSource<DataLakeConfigurationEntry>;
    availableMeasurements: DataLakeConfigurationEntry[] = [];

    displayedColumns: string[] = [
        'name',
        'pipeline',
        'events',
        'download',
        'truncate',
        'remove',
    ];

    constructor(
        // protected dataLakeRestService: DatalakeRestService,
        private datalakeRestService: DatalakeRestService,
        private dataViewDataExplorerService: DataViewDataExplorerService,
        private dialogService: DialogService,
        private breadcrumbService: SpBreadcrumbService,
    ) {}

    ngOnInit(): void {
        this.breadcrumbService.updateBreadcrumb([
            SpConfigurationRoutes.BASE,
            { label: SpConfigurationTabs.getTabs()[1].itemTitle },
        ]);
        this.loadAvailableMeasurements();
    }

    loadAvailableMeasurements() {
        this.availableMeasurements = [];
        // get all available measurements that are stored in the data lake
        this.datalakeRestService
            .getAllMeasurementSeries()
            .subscribe(allMeasurements => {
                // get all measurements that are still used in pipelines
                this.dataViewDataExplorerService
                    .getAllPersistedDataStreams()
                    .subscribe(inUseMeasurements => {
                        allMeasurements.forEach(measurement => {
                            const entry = new DataLakeConfigurationEntry();
                            entry.name = measurement.measureName;

                            inUseMeasurements.forEach(inUseMeasurement => {
                                if (
                                    inUseMeasurement.measureName ===
                                    measurement.measureName
                                ) {
                                    entry.pipelines.push(
                                        inUseMeasurement.pipelineName,
                                    );
                                    if (inUseMeasurement.pipelineIsRunning) {
                                        entry.remove = false;
                                    }
                                }
                            });

                            // get the amount of events from the database
                            const propertyName =
                                this.getFirstNoneDimensionProperty(
                                    measurement.eventSchema,
                                );
                            const field: FieldConfig = {
                                runtimeName: propertyName,
                                aggregations: ['COUNT'],
                                selected: true,
                                numeric: false,
                            };
                            this.datalakeRestService
                                .getData(
                                    measurement.measureName,
                                    this.buildQ(field),
                                )
                                .subscribe((res: SpQueryResult) => {
                                    // read the count value from the result
                                    entry.events =
                                        res.allDataSeries[0].rows[0][1];
                                });

                            this.availableMeasurements.push(entry);
                        });

                        this.dataSource = new MatTableDataSource(
                            this.availableMeasurements,
                        );
                        setTimeout(() => {
                            this.dataSource.paginator = this.paginator;
                            this.dataSource.sort = this.sort;
                        });
                    });
                // this.availableMeasurements = response;
            });
    }

    private getFirstNoneDimensionProperty(eventSchema: EventSchema): string {
        const propertyName = eventSchema.eventProperties.find(
            ep => ep.propertyScope !== 'DIMENSION_PROPERTY',
        );
        if (!propertyName) {
            return '*';
        } else {
            return propertyName.runtimeName;
        }
    }

    cleanDatalakeIndex(measurementIndex: string) {
        const dialogRef: DialogRef<DeleteDatalakeIndexComponent> =
            this.dialogService.open(DeleteDatalakeIndexComponent, {
                panelType: PanelType.STANDARD_PANEL,
                title: 'Truncate data',
                width: '70vw',
                data: {
                    measurementIndex: measurementIndex,
                    deleteDialog: false,
                },
            });

        dialogRef.afterClosed().subscribe(data => {
            if (data) {
                this.loadAvailableMeasurements();
            }
        });
    }

    deleteDatalakeIndex(measurementIndex: string) {
        const dialogRef: DialogRef<DeleteDatalakeIndexComponent> =
            this.dialogService.open(DeleteDatalakeIndexComponent, {
                panelType: PanelType.STANDARD_PANEL,
                title: 'Delete data',
                width: '70vw',
                data: {
                    measurementIndex: measurementIndex,
                    deleteDialog: true,
                },
            });

        dialogRef.afterClosed().subscribe(data => {
            if (data) {
                this.loadAvailableMeasurements();
            }
        });
    }

    openDownloadDialog(measurementName: string) {
        this.dialogService.open(DataDownloadDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: 'Download data',
            width: '50vw',
            data: {
                dataDownloadDialogModel: {
                    measureName: measurementName,
                },
            },
        });
    }

    private buildQ(column: FieldConfig): DatalakeQueryParameters {
        return DatalakeQueryParameterBuilder.create(0, new Date().getTime())
            .withColumnFilter([column], true)
            .build();
    }
}
