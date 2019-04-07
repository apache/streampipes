import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { DataMarketplaceService } from './data-marketplace.service';
import { AdapterDescription } from '../model/connect/AdapterDescription';
import { ShepherdService } from "../../services/tour/shepherd.service";
import { ConnectService } from '../connect.service';
import { FilterPipe } from './filter.pipe';
import {AdapterUploadDialog} from './adapter-upload/adapter-upload-dialog.component';
import {MatDialog} from '@angular/material';
import {AdapterDescriptionList} from '../model/connect/AdapterDescriptionList';
import {TsonLdSerializerService} from '../../platform-services/tsonld-serializer.service';

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
    filteredAdapters: AdapterDescription[];
    visibleAdapters: AdapterDescription[];

    @Output()
    selectAdapterEmitter: EventEmitter<AdapterDescription> = new EventEmitter<AdapterDescription>();

    selectedIndex: number = 0;
    filterTerm: string = "";
    pipe: FilterPipe = new FilterPipe();
    categories: string[] = ['All Adapters', 'Data Set', 'Data Stream'];
    selected: string = "All Adapters";

    constructor(private dataMarketplaceService: DataMarketplaceService,
                private ShepherdService: ShepherdService,
                private connectService: ConnectService,
                public dialog: MatDialog,
                private tsonLdSerializerService: TsonLdSerializerService,
    ) {
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
                console.log("Template: ");
                console.log(adapterTemplate);
            });

            this.adapterDescriptions = this.adapterDescriptions.concat(adapterTemplates);
            this.filteredAdapterDescriptions = this.adapterDescriptions;
        });
    }

    getAdaptersRunning(): void {
        this.dataMarketplaceService.getAdapters().subscribe(adapters => {
            this.adapters = adapters;
            this.filteredAdapters = this.adapters;
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

    updateFilterTerm(inputValue) {
        this.filterTerm = inputValue;
    }

    downloadAllAdapterTemplates() {
        var adapterTemplates: AdapterDescription[] = [];
        this.adapterDescriptions.forEach(function (adapterTemplate) {
            if (adapterTemplate.isTemplate) {
                delete adapterTemplate['userName'];
                adapterTemplates.push(adapterTemplate);
            }
        });

        let adapterDescriptionList: AdapterDescriptionList  = new AdapterDescriptionList("http://streampipes.org/exportedList");
        adapterDescriptionList.list = adapterTemplates;


        // this.tsonLdSerializerService.toJsonLd(this.data.adapter).subscribe(res => {
        //     var data = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(res, null, 2));
        //     var downloader = document.createElement('a');
        //
        //     downloader.setAttribute('href', data);
        //     downloader.setAttribute('download', this.data.adapter.label + '-adapter-template.json');
        //     downloader.click();
        //
        // });

        // this.adapterJsonLd = this.tsonLdSerializerService.toJsonLd(this.data.adapter);
        this.tsonLdSerializerService.toJsonLd(adapterDescriptionList).subscribe(res => {
            let data = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(res, null, 2));
            let downloader = document.createElement('a');

            downloader.setAttribute('href', data);
            downloader.setAttribute('download', 'all-adapter-templates.json');
            downloader.click();

        });
    }

    uploadAdapterTemplates() {
        let dialogRef = this.dialog.open(AdapterUploadDialog, {
            width: '70%',
            data: {
                // adapter: adapter
            },
            panelClass: 'sp-no-padding-dialog'
        });

        dialogRef.afterClosed().subscribe(result => {
            this.getAdapterDescriptions()
        });
    }

    filterAdapterCategory(categorie) {

        this.filteredAdapterDescriptions = this.adapterDescriptions;
        this.filteredAdapters = this.adapters;
        if (this.selected == this.categories[1]) {
            for (let adapter of this.filteredAdapterDescriptions) {
                if (!this.connectService.isDataSetDescription(adapter)) {
                    this.filteredAdapterDescriptions = this.filteredAdapterDescriptions.filter(obj => obj !== adapter);
                }
            }
            for (let adapter of this.filteredAdapters) {
                if (!this.connectService.isDataSetDescription(adapter)) {
                    this.filteredAdapters = this.filteredAdapters.filter(obj => obj !== adapter);
                }
            }

        }

        else if (this.selected == this.categories[2]) {
            for (let adapter of this.filteredAdapterDescriptions) {
                if (this.connectService.isDataSetDescription(adapter)) {
                    this.filteredAdapterDescriptions = this.filteredAdapterDescriptions.filter(obj => obj !== adapter);
                }
            }
            for (let adapter of this.filteredAdapters) {
                if (this.connectService.isDataSetDescription(adapter)) {
                    this.filteredAdapters = this.filteredAdapters.filter(obj => obj !== adapter);
                }
            }
        }

    }

}
