import {Component, OnInit, Output, EventEmitter} from '@angular/core';
import {DataMarketplaceService} from './data-marketplace.service';
import {AdapterDescription} from '../model/connect/AdapterDescription';
import {ShepherdService} from "../../services/tour/shepherd.service";
import {ConnectService} from '../connect.service';
import {GenericAdapterStreamDescription} from '../model/connect/GenericAdapterStreamDescription';
import {GenericAdapterSetDescription} from '../model/connect/GenericAdapterSetDescription';
import {SpecificAdapterSetDescription} from '../model/connect/SpecificAdapterSetDescription';
import {SpecificAdapterStreamDescription} from '../model/connect/SpecificAdapterStreamDescription';

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

    constructor(private dataMarketplaceService: DataMarketplaceService, private ShepherdService: ShepherdService,
                private connectService: ConnectService) {
    }

    ngOnInit() {
        this.updateDescriptionsAndRunningAdatpers();
    }

    updateDescriptionsAndRunningAdatpers() {
        this.getAdapterDescriptions();
        this.getAdaptersRunning();
    }

    getAdapterDescriptions(): void {
        var self = this;

        this.adapterDescriptions = [];

        this.dataMarketplaceService
            .getGenericAndSpecifigAdapterDescriptions()
            .subscribe(res => {
                res.subscribe(adapterDescriptions => {
                    this.adapterDescriptions = this.adapterDescriptions.concat(adapterDescriptions);

                });
            });

        this.dataMarketplaceService.getAdapterTemplates().subscribe(adapterTemplates => {
            adapterTemplates.forEach(function (adapterTemplate) {
                adapterTemplate.isTemplate = true;
            });

            this.adapterDescriptions = this.adapterDescriptions.concat(adapterTemplates);

        });
    }

    getAdaptersRunning(): void {
        this.dataMarketplaceService.getAdapters().subscribe(adapters => {
            this.adapters = adapters;
        });
    }

    // getAdapterTemplates(): void {
    //     this.dataMarketplaceService.getAdapterTemplates().subscribe(adapterTemplates => {
    //         adapterTemplates.forEach(function (adapterTemplate) {
    //             adapterTemplate.isTemplate = true;
    //         });
    //
    //         this.adapterTemplates = adapterTemplates;
    //     });
    // }

    selectedIndexChange(index: number) {
        this.selectedIndex = index;
    }

    startAdapterTutorial() {
        this.ShepherdService.startAdapterTour();
    }

    startAdapterTutorial2() {
        this.ShepherdService.startAdapterTour2();
    }

    startAdapterTutorial3() {
        this.ShepherdService.startAdapterTour3();
    }

    selectAdapter(adapterDescription: AdapterDescription) {
        this.newAdapterFromDescription = this.dataMarketplaceService.cloneAdapterDescription(adapterDescription);

        this.newAdapterFromDescription.templateTitle = this.newAdapterFromDescription.label;
        this.newAdapterFromDescription.label = "";
        this.newAdapterFromDescription.description = "";

        this.ShepherdService.trigger("select-adapter");
    }

    templateFromRunningAdapter(adapter: AdapterDescription) {
        this.selectedIndexChange(0);
        this.selectAdapter(adapter);

    }

    removeSelection() {
        this.newAdapterFromDescription = undefined;
    }
}
