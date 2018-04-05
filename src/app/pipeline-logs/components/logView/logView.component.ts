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

    error: string;

    displayedColumns = ['timestamp', 'level', 'type', 'message'];
    dataSource;

    @ViewChild(MatPaginator) paginator: MatPaginator;

    ngAfterViewInit() {
        this.loadLogs();
    }


    constructor(private logviewRestService: LogViewRestService) {
    }

    loadLogs() {

        const logRequest = <LogRequest>{};
        logRequest.dateFrom = this.startDate.getTime();
        logRequest.dateTo = this.endDate.getTime();
        logRequest.sourceID = this.logSourceID;


        this.logviewRestService.getLogs(logRequest)
            .subscribe( response => {
                this.dataSource = new MatTableDataSource<Log>(response);
                this.dataSource.paginator = this.paginator;
            }, error => {
                console.log(error);
                this.error = 'ERROR12';
            });
    }


}






