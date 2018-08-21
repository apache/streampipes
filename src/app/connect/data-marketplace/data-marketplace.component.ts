import {Component, OnInit, Output, EventEmitter } from '@angular/core';
import { DataMarketplaceService } from './data-marketplace.service';
import { AdapterDescription } from '../model/connect/AdapterDescription';

@Component({
    selector: 'sp-data-marketplace',
    templateUrl: './data-marketplace.component.html',
    styleUrls: ['./data-marketplace.component.css']
})
export class DataMarketplaceComponent implements OnInit {

    private adapters: AdapterDescription[];
    @Output() selectAdapterEmitter: EventEmitter<AdapterDescription> = new EventEmitter<AdapterDescription>();

    constructor(private dataMarketplaceService: DataMarketplaceService) {
    }

    ngOnInit() {
        this.dataMarketplaceService.getAdapters().subscribe(res => {
            this.adapters = res;
        });
    }

    selectAdapter(adapter: AdapterDescription) {
        this.selectAdapterEmitter.emit(adapter);
    }

}