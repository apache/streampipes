import {Component, OnInit, Output, EventEmitter} from '@angular/core';
import {DataMarketplaceService} from './data-marketplace.service';
import {AdapterDescription} from '../model/connect/AdapterDescription';
import {ShepherdService} from "../../services/tour/shepherd.service";

@Component({
    selector: 'sp-data-marketplace',
    templateUrl: './data-marketplace.component.html',
    styleUrls: ['./data-marketplace.component.css'],
})
export class DataMarketplaceComponent implements OnInit {
    adapterDescriptions: AdapterDescription[];
    newAdapterFromDescription: AdapterDescription;
    adapters: AdapterDescription[];
    @Output()
    selectAdapterEmitter: EventEmitter<AdapterDescription> = new EventEmitter<AdapterDescription>();

    selectedIndex: number = 0;

    constructor(private dataMarketplaceService: DataMarketplaceService, private ShepherdService: ShepherdService) {
    }

    ngOnInit() {
        this.dataMarketplaceService
            .getGenericAndSpecifigAdapterDescriptions()
            .subscribe(res => {
                res.subscribe(adapterDescriptions => {
                    this.adapterDescriptions = adapterDescriptions;
                });
            });
        this.getAdapters();
    }

    getAdapters(): void {
        this.dataMarketplaceService.getAdapters().subscribe(adapters => {
            this.adapters = adapters;
        });
    }

    selectedIndexChange(index: number) {
        this.selectedIndex = index;
    }

    startAdapterTutorial() {
        this.ShepherdService.startAdapterTour();
    }

    startAdapterTutorial2() {
        this.ShepherdService.startAdapterTour2();
    }

    selectAdapter(adapterDescription: AdapterDescription) {
        this.newAdapterFromDescription = adapterDescription;
        this.ShepherdService.trigger("select-adapter");
    }

    removeSelection() {
        this.newAdapterFromDescription = undefined;
    }
}
