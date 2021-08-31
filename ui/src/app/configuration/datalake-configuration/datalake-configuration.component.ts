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
import { DatalakeRestService } from '../../platform-services/apis/datalake-rest.service';
import { MatTableDataSource } from '@angular/material/table';
import { DataViewDataExplorerService } from '../../platform-services/apis/data-view-data-explorer.service';
import { DataLakeConfigurationEntry } from './datalake-configuration-entry';
import { DatalakeQueryParameters } from '../../core-services/datalake/DatalakeQueryParameters';
import { DatalakeQueryParameterBuilder } from '../../core-services/datalake/DatalakeQueryParameterBuilder';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { DialogRef } from '../../core-ui/dialog/base-dialog/dialog-ref';
import { PanelType } from '../../core-ui/dialog/base-dialog/base-dialog.model';
import { DialogService } from '../../core-ui/dialog/base-dialog/base-dialog.service';
import { DeleteDatalakeIndexComponent } from '../dialog/delete-datalake-index/delete-datalake-index-dialog.component';

@Component({
  selector: 'sp-datalake-configuration',
  templateUrl: './datalake-configuration.component.html',
  styleUrls: ['./datalake-configuration.component.css']
})
export class DatalakeConfigurationComponent implements OnInit {

  @ViewChild(MatPaginator) paginator: MatPaginator;
  pageSize = 1;
  @ViewChild(MatSort) sort: MatSort;

  dataSource: MatTableDataSource<DataLakeConfigurationEntry>;
  availableMeasurements: DataLakeConfigurationEntry[] = [];

  displayedColumns: string[] = ['name', 'pipeline', 'events', 'truncate', 'remove'];

  constructor(
    // protected dataLakeRestService: DatalakeRestService,
    private datalakeRestService: DatalakeRestService,
    private dataViewDataExplorerService: DataViewDataExplorerService,
    private dialogService: DialogService) {
  }

  ngOnInit(): void {
    this.loadAvailableMeasurements();
  }

  loadAvailableMeasurements() {
    this.availableMeasurements = [];
    // get all available measurements that are stored in the data lake
    this.datalakeRestService.getAllMeasurementSeries().subscribe(allMeasurements => {
      // get all measurements that are still used in pipelines
      this.dataViewDataExplorerService.getAllPersistedDataStreams().subscribe(inUseMeasurements => {
        allMeasurements.forEach(measurement => {
          const entry = new DataLakeConfigurationEntry();
          entry.name = measurement.measureName;

          inUseMeasurements.forEach(inUseMeasurement => {
            if (inUseMeasurement.measureName === measurement.measureName) {
              entry.pipelines.push(inUseMeasurement.pipelineName);
              entry.remove = false;
            }
          });

          // get the amount of events from the database
          this.datalakeRestService.getData(
            measurement.measureName,
            this.buildQ(measurement.eventSchema.eventProperties[0].label.toLocaleLowerCase())).subscribe(res => {
            entry.events = res.rows[0][1];
          });

          this.availableMeasurements.push(entry);
        });

        this.dataSource = new MatTableDataSource(this.availableMeasurements);
        setTimeout(() => {
          this.dataSource.paginator = this.paginator;
          this.dataSource.sort = this.sort;
        });
      });
      // this.availableMeasurements = response;
    });
  }

  cleanDatalakeIndex(measurementIndex: string) {
    const dialogRef: DialogRef<DeleteDatalakeIndexComponent> = this.dialogService.open(DeleteDatalakeIndexComponent, {
      panelType: PanelType.STANDARD_PANEL,
      title: 'Truncate data',
      width: '70vw',
      data: {
        'measurementIndex': measurementIndex
      }
    });

    dialogRef.afterClosed().subscribe(data => {
      if (data) {
        this.loadAvailableMeasurements();
      }
    });
  }

  deleteDatalakeIndex(measurmentIndex: string) {
    // add user confirmation
    this.datalakeRestService.dropSingleMeasurementSeries(measurmentIndex);
  }

  private buildQ(column: string): DatalakeQueryParameters {
    return DatalakeQueryParameterBuilder.create(0, new Date().getTime())
      .withColumnFilter([column])
      .withAggregationFunction('COUNT')
      .build();
  }

}
