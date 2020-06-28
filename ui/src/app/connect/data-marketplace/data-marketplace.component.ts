/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {DataMarketplaceService} from './data-marketplace.service';
import {ShepherdService} from "../../services/tour/shepherd.service";
import {ConnectService} from '../connect.service';
import {FilterPipe} from './filter.pipe';
import {AdapterUploadDialog} from './adapter-upload/adapter-upload-dialog.component';
import {MatDialog} from '@angular/material/dialog';
import {
    AdapterDescription,
    AdapterDescriptionList,
    AdapterDescriptionUnion,
    AdapterSetDescription,
    AdapterStreamDescription,
    EventSchema, SpDataSet,
    SpDataStream
} from "../../core-model/gen/streampipes-model";

@Component({
    selector: 'sp-data-marketplace',
    templateUrl: './data-marketplace.component.html',
    styleUrls: ['./data-marketplace.component.css']
})
export class DataMarketplaceComponent implements OnInit {
    adapterDescriptions: AdapterDescriptionUnion[];
    newAdapterFromDescription: AdapterDescriptionUnion;
    filteredAdapterDescriptions: AdapterDescriptionUnion[];
    adapters: AdapterDescriptionUnion[];
    filteredAdapters: AdapterDescriptionUnion[];
    visibleAdapters: AdapterDescriptionUnion[];

    @Output()
    selectAdapterEmitter: EventEmitter<AdapterDescription> = new EventEmitter<AdapterDescription>();

    selectedIndex: number = 0;
    filterTerm: string = "";
    pipe: FilterPipe = new FilterPipe();
    adapterTypes: string[] = ['All types', 'Data Set', 'Data Stream'];
    selectedType: string = "All types";

    adapterCategories: any;
    selectedCategory: any = "All";

    constructor(private dataMarketplaceService: DataMarketplaceService,
                private ShepherdService: ShepherdService,
                private connectService: ConnectService,
                public dialog: MatDialog) {
    }

    ngOnInit() {
        this.updateDescriptionsAndRunningAdatpers();
        this.loadAvailableTypeCategories();
        this.visibleAdapters = this.adapters;
    }

    loadAvailableTypeCategories() {
        this.dataMarketplaceService.getAdapterCategories().subscribe(res => {
            this.adapterCategories = res;
            this.adapterCategories.unshift({type: "All", label: "All categories", description: ""});
        });
    }

    updateDescriptionsAndRunningAdatpers() {
        this.getAdapterDescriptions();
        this.getAdaptersRunning();
    }

    getAdapterDescriptions(): void {
        this.adapterDescriptions = [];

        this.dataMarketplaceService
            .getGenericAndSpecificAdapterDescriptions()
            .subscribe((allAdapters) => {
                this.adapterDescriptions = this.adapterDescriptions.concat(allAdapters[0]);
                this.adapterDescriptions = this.adapterDescriptions.concat(allAdapters[1]);
                this.adapterDescriptions
                    .sort((a, b) => a.name.localeCompare(b.name));
                this.filteredAdapterDescriptions = this.adapterDescriptions;
            });

        this.dataMarketplaceService.getAdapterTemplates().subscribe(adapterTemplates => {
            adapterTemplates.forEach(function (adapterTemplate) {
                (adapterTemplate as any).isTemplate = true;
            });

            this.adapterDescriptions = this.adapterDescriptions.concat(adapterTemplates);
            this.adapterDescriptions
                .sort((a, b) => a.name.localeCompare(b.name));
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

    selectAdapter(adapterDescription: AdapterDescriptionUnion) {
        this.newAdapterFromDescription = this.dataMarketplaceService.cloneAdapterDescription(adapterDescription);
        (this.newAdapterFromDescription as any).templateTitle = this.newAdapterFromDescription.name;
        this.newAdapterFromDescription.name = "";
        this.newAdapterFromDescription.description = "";
        if (this.newAdapterFromDescription instanceof AdapterStreamDescription) {
            this.newAdapterFromDescription.dataStream = new SpDataStream();
            this.newAdapterFromDescription.dataStream["@class"] = "org.apache.streampipes.model.SpDataStream";
            this.newAdapterFromDescription.dataStream.eventSchema = new EventSchema();
            this.newAdapterFromDescription.dataStream.eventSchema["@class"] = "org.apache.streampipes.model.schema.EventSchema";
        }
        if (this.newAdapterFromDescription instanceof AdapterSetDescription) {
            this.newAdapterFromDescription.dataSet = new SpDataSet();
            this.newAdapterFromDescription.dataSet["@class"] = "org.apache.streampipes.model.SpDataSet";
            this.newAdapterFromDescription.dataSet.eventSchema = new EventSchema();
            this.newAdapterFromDescription.dataSet.eventSchema["@class"] = "org.apache.streampipes.model.schema.EventSchema";
        }
        console.log("new adapter");
        console.log(this.newAdapterFromDescription);

        this.ShepherdService.trigger("select-adapter");
    }

    templateFromRunningAdapter(adapter: AdapterDescriptionUnion) {
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
            if ((adapterTemplate as any).isTemplate) {
                delete adapterTemplate['userName'];
                adapterTemplates.push(adapterTemplate);
            }
        });

        let adapterDescriptionList: AdapterDescriptionList  = new AdapterDescriptionList();
        adapterDescriptionList.elementId = "http://streampipes.org/exportedList";
        adapterDescriptionList.list = adapterTemplates as AdapterDescriptionUnion[];


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
        let data = "data:text/json;charset=utf-8," +encodeURIComponent(JSON.stringify(adapterDescriptionList, null, 2));
        let downloader = document.createElement('a');

        downloader.setAttribute('href', data);
        downloader.setAttribute('download', 'all-adapter-templates.json');
        downloader.click();
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

    filterAdapter(event) {
        let filteredAdapterTypes = this.filterAdapterType(this.adapterDescriptions);
        let filteredAdapterTemplateTypes = this.filterAdapterType(this.adapters);

        let filteredAdapterCategories = this.filterAdapterCategory(filteredAdapterTypes);
        let filteredAdapterTemplateCategories = this.filterAdapterCategory(filteredAdapterTemplateTypes);

        this.filteredAdapterDescriptions = filteredAdapterCategories;
        this.filteredAdapters = filteredAdapterTemplateCategories;
    }

    filterAdapterCategory(currentElements: AdapterDescriptionUnion[]): AdapterDescriptionUnion[] {
        if (this.selectedCategory == this.adapterCategories[0].type) {
            return currentElements;
        } else {
            return currentElements.filter(adapterDescription => adapterDescription.category.indexOf(this.selectedCategory) != -1);
        }
    }

    filterAdapterType(currentElements: AdapterDescriptionUnion[]): AdapterDescriptionUnion[] {
        if (this.selectedType == this.adapterTypes[0]) {
            return currentElements;
        } else if (this.selectedType == this.adapterTypes[1]) {
            return currentElements.filter(adapterDescription => this.connectService.isDataSetDescription(adapterDescription));
        } else if (this.selectedType == this.adapterTypes[2]) {
            return currentElements.filter(adapterDescription => !this.connectService.isDataSetDescription(adapterDescription));
        }
    }

}
