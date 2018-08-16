import {Component, Input, OnInit} from '@angular/core';
import {RestService} from '../rest.service';
import {AdapterDescription} from '../model/connect/AdapterDescription';
import {AdapterDataSource} from './adapter-data-source.service';
import {Subscription} from'rxjs';
@Component({
    selector: 'sp-all-adapters',
    templateUrl: './all.component.html',
    styleUrls: ['./all.component.css']
})
export class AllAdaptersComponent implements OnInit {
    

    private restService: RestService;

    private allAdapters: AdapterDescription[];

    subscription: Subscription;

    displayedColumns = ['id', 'protocol','format', 'delete'];

    @Input() dataSource: AdapterDataSource;

    constructor(restService: RestService) {
        this.restService = restService;


    }

    ngOnInit() {
        const adapter = new AdapterDescription("iii");

        this.allAdapters = [adapter];
        this.dataSource = new AdapterDataSource(this.restService);
    }

    newAdapterStarted() {
        this.dataSource.reload();

    }


    private deleteAdapter(adapter: AdapterDescription) {
        // this.dataSource.delete(adapter);
        this.restService.deleteAdapter(adapter).subscribe(x => {
            this.dataSource.reload();
        });
    }

}
