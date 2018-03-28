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

/*
    logs: Log[] = [
        {timestamp: '12.12.12', level: 'INFO', type: 'USERLOG', message: 'Test'},
        {timestamp: '12.12.12', level: 'INFO', type: 'USERLOG', message: 'Test'},
        {timestamp: '12.12.12', level: 'INFO', type: 'USERLOG', message: 'Test'},
        {timestamp: '12.12.12', level: 'INFO', type: 'USERLOG', message: 'Test'},
        {timestamp: '12.12.12', level: 'INFO', type: 'USERLOG', message: 'T asdfasd fs dfa sfas sa dfas fas d sdfa est'},
        {timestamp: '12.12.12', level: 'INFO', type: 'USERLOG', message: 'Tesasd fas fasd fasd fas dfast'},
        {timestamp: '12.12.12', level: 'INFO123213', type: 'USERLOG', message: 'Test'},
        {timestamp: '12.12.12', level: 'INFO', type: 'USERLOG', message: 'Test33333 asdfasdfasdf asdf as fs'},
        {timestamp: '12.12.12', level: 'INFO', type: 'USERLOG', message: 'Test'},
        {timestamp: '12.12.12', level: 'ERROR', type: 'USERLOG', message: 'Test'},
        {timestamp: '12.12.12', level: 'INFO', type: 'USERLOG', message: 'Test'},
        {timestamp: '12.12.12', level: 'ERROR', type: 'USERLOG', message: 'Teddasdfas das dfas da sdfst'},
        {timestamp: '12.12.12', level: 'INFO', type: 'USERLOG', message: 'Test'},
        {timestamp: '12.12.12', level: 'INFO', type: 'USERLOG', message: 'Test'},
        {timestamp: '12.12.12', level: 'INFO', type: 'USERLOG', message: 'T asdfas dfasd fsad as dfas dfasfest'},
        {timestamp: '12.12.12', level: 'INFO', type: 'USERLOG', message: 'Testdasdsadsadasdasdasdasdasdasdasdasdasdqwww w eqwe wq asdsd afsadf sdf sasa dsd fsd  wes fsa df'},
        {timestamp: '12.12.12', level: 'INFO', type: 'USERLOG', message: 'Test'},
        {timestamp: '12.12.12', level: 'INFO', type: 'USERLOG', message: 'Test'},
        {timestamp: '12.12.12', level: 'INFO', type: 'USERLOG', message: 'Test'}
    ];
    */

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






