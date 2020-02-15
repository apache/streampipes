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

import {Component, EventEmitter, Output, ViewChild} from '@angular/core';
import {AppTransportMonitoringRestService} from "../../services/app-transport-monitoring-rest.service";
import {TransportProcessEventModel} from "../../model/transport-process-event.model";
import {TimestampConverterService} from "../../services/timestamp-converter.service";
import {MatPaginator, MatTableDataSource} from '@angular/material';

@Component({
    selector: 'transport-selection',
    templateUrl: './transport-selection.component.html',
    styleUrls: ['./transport-selection.component.css']
})
export class TransportSelectionComponent {

    transportProcesses: TransportProcessEventModel[] = [];

    displayedColumns: string[] = ['position', 'startTime', 'endTime', 'action'];
    @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
    dataSource = new MatTableDataSource<TransportProcessEventModel>();

    @Output() selectedProcess = new EventEmitter<TransportProcessEventModel>();


    constructor(private restService: AppTransportMonitoringRestService,
                public timestampConverterService: TimestampConverterService) {

    }

    ngOnInit() {
        this.dataSource.paginator = this.paginator;
        this.fetchTransportProcesses();
    }

    fetchTransportProcesses() {
        this.restService.getTransportProcesses().subscribe(resp => {
           this.transportProcesses = this.sort(resp);
           this.dataSource.data = this.transportProcesses;
        });
    }

    selectProcess(element: TransportProcessEventModel) {
        this.selectedProcess.emit(element);
    }

    sort(tpe : TransportProcessEventModel[]):TransportProcessEventModel[] {
        tpe.sort((a,b) => {
            return b.startTime - a.startTime;
        });
        return tpe;
    }


}