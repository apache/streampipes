import { Component, OnInit } from '@angular/core';
import {AdapterDataSource} from '../all-adapters/adapter-data-source.service';
import {RestService} from '../rest.service';

@Component({
    selector: 'sp-main',
    templateUrl: './main.component.html',
    styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit {

    private dataSource: AdapterDataSource;
    private restService: RestService;

    public MainComponent(restService: RestService) {
        this.restService = restService;
    }

    ngOnInit() {
        this.dataSource = new AdapterDataSource(this.restService);
    }

}
