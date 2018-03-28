import { AfterViewInit, Component, Input, ViewChild } from '@angular/core';
import { MatPaginator, MatTableDataSource } from '@angular/material';
import { Log } from './model/log.model';
import { LogViewRestService } from './services/logView-rest.service';
import { LogRequest } from './model/logRequest.model';
import {any} from 'codelyzer/util/function';

@Component({
    selector: 'logView',
    templateUrl: './logView.component.html',
    styleUrls: ['./logView.component.css']
})
export class LogViewComponent implements AfterViewInit {

    static MS_PER_MINUTE = 60000;
    static DEFAULT_DATE_OFFSET = 10 * LogViewComponent.MS_PER_MINUTE;

    @Input() logSourceID: string;

    startDate = new Date(Date.now() - LogViewComponent.DEFAULT_DATE_OFFSET);

    endDate = new Date(Date.now());

    logs: Log[];

    displayedColumns = ['timestamp', 'level', 'type', 'message'];
    dataSource = new MatTableDataSource<Log>(this.logs);

    @ViewChild(MatPaginator) paginator: MatPaginator;

    /**
     * Set the paginator after the view init since this component will
     * be able to query its view for the initialized paginator.
     */
    ngAfterViewInit() {
        this.dataSource.paginator = this.paginator;
        this.loadLogs();
    }


    constructor(private logviewRestService: LogViewRestService) {

    }

    loadLogs() {
        let logRequest: LogRequest = any;
        logRequest.dateFrom = this.startDate.getTime();
        logRequest.dateTo = this.endDate.getTime();
        logRequest.sourceID = this.logSourceID;

        console.log(logRequest);

        this.logviewRestService.getLogs(logRequest)
            .subscribe(response => {
                this.logs = response;
            }, error => {
                console.log(error);
            });
    }


}






