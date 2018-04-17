import { AfterViewInit, Component, Input, OnChanges, SimpleChanges, ViewChild} from '@angular/core';
import {MatPaginator, MatSort, MatSortable, MatTableDataSource} from '@angular/material';
import { Log } from './model/log.model';
import { LogViewRestService } from './services/logView-rest.service';
import { LogRequest } from './model/logRequest.model';

@Component({
    selector: 'logView',
    templateUrl: './logView.component.html',
    styleUrls: ['./logView.component.css']
})
export class LogViewComponent implements AfterViewInit, OnChanges {

    static MS_PER_MINUTE = 60000;
    static DEFAULT_DATE_OFFSET = 10 * LogViewComponent.MS_PER_MINUTE;

    @Input() logSourceIDs: string[];

    startDate = new Date(Date.now() - LogViewComponent.DEFAULT_DATE_OFFSET);

    endDate = new Date(Date.now());

    logLevels = [];

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

    ngOnChanges(changes: SimpleChanges) {
        console.log(this.logSourceIDs);
        this.loadLogs();

    }

    loadLogs() {

        let logs = [];

        const logRequest = <LogRequest>{};
        this.logLevels = [];

        logRequest.dateFrom = this.startDate.getTime();
        logRequest.dateTo = this.endDate.getTime();

        for (let logSourceID of this.logSourceIDs) {

            logRequest.sourceID = logSourceID;

            this.logviewRestService.getLogs(logRequest)
                .subscribe( response => {
                    logs = logs.concat(response);

                    logs.sort((a, b) => {
                        if (a.timestamp < b.timestamp) {
                            return 1;
                        }
                        if (a.timestamp > b.timestamp) {
                            return -1;
                        }
                        return 0;
                    });

                    this.dataSourceDisplay = new MatTableDataSource<Log>(logs);
                    this.dataSource = new MatTableDataSource<Log>(logs);
                    this.dataSourceDisplay.paginator = this.paginator;

                    this.logLevels.push(Array.from(new Set(response.map(t => t.level))));
                    this.logLevels.push(Array.from(new Set(this.logLevels)));

                }, error => {
                    console.log(error);
                });
        }
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
