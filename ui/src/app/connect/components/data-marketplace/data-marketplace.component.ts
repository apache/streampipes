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
import {DataMarketplaceService} from '../../services/data-marketplace.service';
import {ShepherdService} from "../../../services/tour/shepherd.service";
import {ConnectService} from '../../services/connect.service';
import {FilterPipe} from '../../filter/filter.pipe';
import {AdapterUploadDialog} from '../../dialog/adapter-upload/adapter-upload-dialog.component';
import {
  AdapterDescription,
  AdapterDescriptionUnion,
  AdapterSetDescription,
  AdapterStreamDescription,
  EventSchema,
  SpDataSet,
  SpDataStream
} from "../../../core-model/gen/streampipes-model";
import {PanelType} from "../../../core-ui/dialog/base-dialog/base-dialog.model";
import {DialogService} from "../../../core-ui/dialog/base-dialog/base-dialog.service";

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
    selectAdapterEmitter: EventEmitter<AdapterDescriptionUnion> = new EventEmitter<AdapterDescriptionUnion>();

    selectedIndex: number = 0;
    filterTerm: string = "";
    pipe: FilterPipe = new FilterPipe();
    adapterTypes: string[] = ['All types', 'Data Set', 'Data Stream'];
    selectedType: string = "All types";

    adapterCategories: any;
    selectedCategory: any = "All";

    adaptersLoading: boolean = true;
    adapterLoadingError: boolean = false;

    constructor(private dataMarketplaceService: DataMarketplaceService,
                private ShepherdService: ShepherdService,
                private connectService: ConnectService,
                private dialogService: DialogService) {
    }

    ngOnInit() {
        this.updateDescriptionsAndRunningAdatpers();
        this.loadAvailableTypeCategories();
        this.visibleAdapters = this.adapters;
    }

    loadAvailableTypeCategories() {
        this.dataMarketplaceService.getAdapterCategories().subscribe(res => {
            this.adapterCategories = res;
            this.adapterCategories.unshift({label: "All categories", description: "", code: "All"});
        });
    }

    updateDescriptionsAndRunningAdatpers() {
        this.getAdapterDescriptions();
        this.getAdaptersRunning();
    }

    getAdapterDescriptions(): void {
        this.adaptersLoading = true;
        this.adapterDescriptions = [];

        this.dataMarketplaceService
            .getGenericAndSpecificAdapterDescriptions()
            .subscribe((allAdapters) => {
                this.adapterDescriptions = this.adapterDescriptions.concat(allAdapters[0]);
                this.adapterDescriptions = this.adapterDescriptions.concat(allAdapters[1]);
                this.adapterDescriptions
                    .sort((a, b) => a.name.localeCompare(b.name));
                this.filteredAdapterDescriptions = this.adapterDescriptions;
                this.adaptersLoading = false;
            }, error => {
                console.log(error);
                this.adaptersLoading = false;
                this.adapterLoadingError = true;
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

            // Create new SpDataStream and EventSchema if not already exists, e.g. in adapter template
            if (this.newAdapterFromDescription.dataStream == undefined) {
                this.newAdapterFromDescription.dataStream = new SpDataStream();
                this.newAdapterFromDescription.dataStream["@class"] = "org.apache.streampipes.model.SpDataStream";
                this.newAdapterFromDescription.dataStream.eventSchema = new EventSchema();
                this.newAdapterFromDescription.dataStream.eventSchema["@class"] = "org.apache.streampipes.model.schema.EventSchema";
            }
        }
        if (this.newAdapterFromDescription instanceof AdapterSetDescription) {

            // Create new SpDataSet and EventSchema if not already exists, e.g. in adapter template
            if (this.newAdapterFromDescription.dataSet == undefined || this.newAdapterFromDescription.dataSet["@class"] == undefined    ) {
                this.newAdapterFromDescription.dataSet = new SpDataSet();
                this.newAdapterFromDescription.dataSet["@class"] = "org.apache.streampipes.model.SpDataSet";
                this.newAdapterFromDescription.dataSet.eventSchema = new EventSchema();
                this.newAdapterFromDescription.dataSet.eventSchema["@class"] = "org.apache.streampipes.model.schema.EventSchema";
            }
        }
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

        let data = "data:text/json;charset=utf-8," +encodeURIComponent(JSON.stringify(adapterTemplates, null, 2));
        let downloader = document.createElement('a');

        downloader.setAttribute('href', data);
        downloader.setAttribute('download', 'all-adapter-templates.json');
        downloader.click();
    }

    uploadAdapterTemplates() {
        let dialogRef = this.dialogService.open(AdapterUploadDialog,{
            panelType: PanelType.STANDARD_PANEL,
            title: "Upload adapter templates",
            width: "50vw"
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
        if (this.selectedCategory == this.adapterCategories[0].code) {
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
