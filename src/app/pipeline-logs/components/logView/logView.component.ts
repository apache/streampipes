import { Component, OnInit } from '@angular/core';

@Component({
    selector: 'logView',
    templateUrl: './logView.component.html',
    styleUrls: ['./logView.component.css']
})
export class LogViewComponent  {

    startDate = new Date();
    endDate = new Date();

    public logs = [{"timestamp": "12.12.12", "level": "INFO", "type":"USERLOG", "message": "Test"},
        {"timestamp": "12.12.12", "level": "INFO", "type":"USERLOG", "message": "Test"},
        {"timestamp": "12.12.12", "level": "INFO", "type":"USERLOG", "message": "Test"},
        {"timestamp": "12.12.12", "level": "INFO", "type":"USERLOG", "message": "Test"},
        {"timestamp": "12.12.12", "level": "INFO", "type":"USERLOG", "message": "T asdfasd fs dfa sfas sa dfas fas d sdfa est"},
        {"timestamp": "12.12.12", "level": "INFO", "type":"USERLOG", "message": "Tesasd fas fasd fasd fas dfast"},
        {"timestamp": "12.12.12", "level": "INFO123213", "type":"USERLOG", "message": "Test"},
        {"timestamp": "12.12.12", "level": "INFO", "type":"USERLOG", "message": "Test33333 asdfasdfasdf asdf as fs"},
        {"timestamp": "12.12.12", "level": "INFO", "type":"USERLOG", "message": "Test"},
        {"timestamp": "12.12.12", "level": "ERROR", "type":"USERLOG", "message": "Test"},
        {"timestamp": "12.12.12", "level": "INFO", "type":"USERLOG", "message": "Test"},
        {"timestamp": "12.12.12", "level": "ERROR", "type":"USERLOG", "message": "Teddasdfas das dfas da sdfst"},{"timestamp": "12.12.12", "level": "INFO", "type":"USERLOG", "message": "Test"},
        {"timestamp": "12.12.12", "level": "INFO", "type":"USERLOG", "message": "Test"},
        {"timestamp": "12.12.12", "level": "INFO", "type":"USERLOG", "message": "T asdfas dfasd fsad as dfas dfasfest"},
        {"timestamp": "12.12.12", "level": "INFO", "type":"USERLOG", "message": "Test"},

        {"timestamp": "12.12.12", "level": "INFO", "type":"USERLOG", "message": "Test"}]

    constructor() {

    }

    load() {

    }






}
