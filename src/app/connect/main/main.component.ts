import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import {AdapterDataSource} from '../all-adapters/adapter-data-source.service';
import {RestService} from '../rest.service';
import {AllAdaptersComponent} from '../all-adapters/all.component'
@Component({
    selector: 'sp-main',
    templateUrl: './main.component.html',
    styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit, AfterViewInit {

    @ViewChild(AllAdaptersComponent) allAdapter: AllAdaptersComponent;
    private dataSource: AdapterDataSource;
    private restService: RestService;

    public MainComponent(restService: RestService) {
        this.restService = restService;
    }

    ngOnInit() {
        this.dataSource = new AdapterDataSource(this.restService);
    }

    newAdapterCreated() {
        this.allAdapter.newAdapterStarted(); 
    }
    ngAfterViewInit() {
        
      }
      

}
