import {Component, OnInit, Output, EventEmitter } from '@angular/core';
import { DataMarketplaceService } from './data-marketplace.service';
import { AdapterDescription } from '../model/connect/AdapterDescription';
import { ProtocolDescription } from '../model/connect/grounding/ProtocolDescription';
import { GenericAdapterSetDescription } from '../model/connect/GenericAdapterSetDescription';
import { GenericAdapterStreamDescription } from '../model/connect/GenericAdapterStreamDescription';

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
        this.dataMarketplaceService.getAdapters().subscribe(adapters => {
            this.adapters = adapters.filter(adapter => !adapter.id.includes('generic'));
            this.dataMarketplaceService.getProtocols().subscribe(protocols => {
                for(let protocol of protocols) {
                    let newAdapter: AdapterDescription;
                    if(protocol.id.includes('sp:protocol/set')) {
                        newAdapter = new GenericAdapterSetDescription("http://streampipes.org/adapter/generic/dataset");
                    } else if(protocol.id.includes('sp:protocol/stream')) {
                        newAdapter = new GenericAdapterStreamDescription("http://streampipes.org/adapter/generic/datastream");
                    }
                    newAdapter.label = protocol.label;
                    newAdapter.description = protocol.description;
                    newAdapter.config = protocol.config;
                    this.adapters.push(newAdapter);
                }
            });
        });
    }

    selectAdapter(adapter: AdapterDescription) {
        this.selectAdapterEmitter.emit(adapter);
    }

}