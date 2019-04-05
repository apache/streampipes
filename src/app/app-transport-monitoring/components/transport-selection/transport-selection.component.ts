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
    @ViewChild(MatPaginator, {}) paginator: MatPaginator;
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