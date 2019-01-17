import {Component, OnInit, Output, EventEmitter} from '@angular/core';
import {DataMarketplaceService} from './data-marketplace.service';
import {AdapterDescription} from '../model/connect/AdapterDescription';
import {ShepherdService} from "../../services/tour/shepherd.service";
import {ConnectService} from '../connect.service';
import {GenericAdapterStreamDescription} from '../model/connect/GenericAdapterStreamDescription';
import {GenericAdapterSetDescription} from '../model/connect/GenericAdapterSetDescription';
import {SpecificAdapterSetDescription} from '../model/connect/SpecificAdapterSetDescription';
import {SpecificAdapterStreamDescription} from '../model/connect/SpecificAdapterStreamDescription';
import {MatIconModule} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {MatMenuModule} from '@angular/material/menu';
import {MatButtonModule} from '@angular/material/button';
import {MatSelectModule} from '@angular/material/select';
import { FilterPipe } from './filter.pipe';

@Component({
    selector: 'sp-data-marketplace',
    templateUrl: './data-marketplace.component.html',
    styleUrls: ['./data-marketplace.component.css']
})
export class DataMarketplaceComponent implements OnInit {
    adapterDescriptions: AdapterDescription[];
    newAdapterFromDescription: AdapterDescription;
    filteredAdapterDescriptions: AdapterDescription[];
    adapters: AdapterDescription[];
    visibleAdapters: AdapterDescription[];

    @Output()
    selectAdapterEmitter: EventEmitter<AdapterDescription> = new EventEmitter<AdapterDescription>();

    selectedIndex: number = 0;
    filterTerm: string = ""; //term anpassen
    pipe: FilterPipe = new FilterPipe();
    categories: string[] = ['All', 'Data Set', 'Data Stream'];
    selected: string = "All";

    constructor(private dataMarketplaceService: DataMarketplaceService, private ShepherdService: ShepherdService,
                private connectService: ConnectService) {
    }

    ngOnInit() {
        this.updateDescriptionsAndRunningAdatpers();
        this.visibleAdapters = this.adapters;
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
                    this.filteredAdapterDescriptions = this.adapterDescriptions;
                });
            });

        this.dataMarketplaceService.getAdapterTemplates().subscribe(adapterTemplates => {
            adapterTemplates.forEach(function (adapterTemplate) {
                adapterTemplate.isTemplate = true;
            });

            this.adapterDescriptions = this.adapterDescriptions.concat(adapterTemplates);
            this.filteredAdapterDescriptions = this.adapterDescriptions;
        });
    }

    getAdaptersRunning(): void {
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
//e anpassen
    updateFilterTerm(inputValue) {
        this.filterTerm = inputValue;         
        }


    filterAdapterCategory(categorie) {
        console.log(categorie.value);
        
        this.filteredAdapterDescriptions = this.adapterDescriptions;

        if(this.selected == this.categories[1]) {
            for(let adapter of this.filteredAdapterDescriptions){
                if(!this.connectService.isDataSetDescription(adapter)){
                    this.filteredAdapterDescriptions = this.filteredAdapterDescriptions.filter(obj => obj !== adapter);
                }
            
            }
        }
        else if (this.selected == this.categories[2]){
            for(let adapter of this.filteredAdapterDescriptions){
                if(this.connectService.isDataSetDescription(adapter)){
                    this.filteredAdapterDescriptions = this.filteredAdapterDescriptions.filter(obj => obj !== adapter);
                }
            
            }
        }
    }
    


}
