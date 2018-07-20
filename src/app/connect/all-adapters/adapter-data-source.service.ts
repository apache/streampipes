import {CollectionViewer, DataSource} from '@angular/cdk/collections';
import {RestService} from '../rest.service';
import {Observable} from 'rxjs/Observable';
import {AdapterDescription} from '../model/AdapterDescription';

import {BehaviorSubject} from 'rxjs/BehaviorSubject';

export class AdapterDataSource extends DataSource<AdapterDescription> {

    private allAdaptersObservable: BehaviorSubject<AdapterDescription[]>;
    private allAdapters: AdapterDescription[];
    private restService;
    connect(collectionViewer: CollectionViewer): Observable<AdapterDescription[]> {

        this.allAdaptersObservable = new BehaviorSubject<AdapterDescription[]>([]);
        this.restService.getAdapters().subscribe(adapters => {
            this.allAdapters = adapters;
            this.allAdaptersObservable.next(adapters);
        });

        return this.allAdaptersObservable;
    }

    constructor(restService: RestService) {
        super();
        this.restService = restService;
    }

    reload(): void {
        this.restService.getAdapters().subscribe(adapters => {
            this.allAdapters = adapters;
            this.allAdaptersObservable.next(adapters);
        });
    }

    delete(adapter: AdapterDescription) {
        var index = -1;
        for (var i = 0; i < this.allAdapters.length; i++) {
           if (this.allAdapters[i].couchDbId == adapter.couchDbId) {
               index = i;
           }
        }

        if (index > -1) {
            this.allAdapters.splice(index, 1);
        }
        this.allAdaptersObservable.next(this.allAdapters);
    }

    disconnect() {}
}