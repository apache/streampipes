import {Component, OnInit} from '@angular/core';
import { DataMarketplaceService } from './data-marketplace.service';
import { AdapterDescription } from '../model/connect/AdapterDescription';

@Component({
    selector: 'sp-data-marketplace',
    templateUrl: './data-marketplace.component.html',
    styleUrls: ['./data-marketplace.component.css']
})
export class DataMarketplaceComponent implements OnInit {

    private adapters: AdapterDescription[];

    constructor(private dataMarketplaceService: DataMarketplaceService) {
    }

    ngOnInit() {
        this.dataMarketplaceService.getAdapters().subscribe(res => {
            this.adapters = res;
            console.log(this.adapters);
        });
    }

}