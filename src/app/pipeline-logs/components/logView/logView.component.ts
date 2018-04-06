import { AfterViewInit, Component, Input, ViewChild } from '@angular/core';
import { MatPaginator, MatTableDataSource } from '@angular/material';
import { Log } from './model/log.model';
import { LogViewRestService } from './services/logView-rest.service';
import { LogRequest } from './model/logRequest.model';

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

    logLevels;

    selectedLogLevel = 'ALL';

    displayedColumns = ['timestamp', 'level', 'type', 'message'];
    dataSource;
    dataSourceDisplay;
    filter = '';

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
                this.dataSourceDisplay = new MatTableDataSource<Log>(response);
                this.dataSource = new MatTableDataSource<Log>(response);
                this.dataSourceDisplay.paginator = this.paginator;

                this.logLevels = Array.from(new Set(response.map(t => t.level)));
            }, error => {
                console.log(error);
            });
    }

    applyFilter(filterValue: string) {
        filterValue = filterValue.trim();
        filterValue = filterValue.toLowerCase();
        this.dataSourceDisplay.filter = filterValue;
    }

    logLevelSelection() {
        let filterValue;

        if (this.selectedLogLevel === 'ALL') {
            filterValue = ''.trim();
            filterValue = filterValue.toLowerCase();
            this.dataSource.filter = filterValue;
        } else {
            filterValue = this.selectedLogLevel.trim();
            filterValue = filterValue.toLowerCase();
            this.dataSource.filter = filterValue;
        }



        this.dataSourceDisplay = this.dataSource;
        this.dataSourceDisplay.paginator = this.paginator;
        console.log(this.filter);


    }


}






