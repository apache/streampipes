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

import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { DataMarketplaceService } from '../../services/data-marketplace.service';
import { ShepherdService } from '../../../services/tour/shepherd.service';
import { ConnectService } from '../../services/connect.service';
import { FilterPipe } from '../../filter/filter.pipe';
import { AdapterDescriptionUnion } from '../../../../../projects/streampipes/platform-services/src/lib/model/gen/streampipes-model';
import { DialogService } from '../../../core-ui/dialog/base-dialog/base-dialog.service';

@Component({
  selector: 'sp-data-marketplace',
  templateUrl: './data-marketplace.component.html',
  styleUrls: ['./data-marketplace.component.scss']
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

  selectedIndex = 0;
  filterTerm = '';
  pipe: FilterPipe = new FilterPipe();
  adapterTypes: string[] = ['All types', 'Data Set', 'Data Stream'];
  selectedType = 'All types';

  adapterCategories: any;
  selectedCategory: any = 'All';

  adaptersLoading = true;
  adapterLoadingError = false;

  constructor(private dataMarketplaceService: DataMarketplaceService,
              private shepherdService: ShepherdService,
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
      this.adapterCategories.unshift({ label: 'All categories', description: '', code: 'All' });
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
      .getAdapterDescriptions()
      .subscribe((allAdapters) => {
        this.adapterDescriptions = allAdapters;
        // this.adapterDescriptions = this.adapterDescriptions.concat(allAdapters[1]);
        this.adapterDescriptions
          .sort((a, b) => a.name.localeCompare(b.name));
        this.filteredAdapterDescriptions = this.adapterDescriptions;
        this.adaptersLoading = false;
      }, error => {
        console.log(error);
        this.adaptersLoading = false;
        this.adapterLoadingError = true;
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
    this.shepherdService.startAdapterTour();
  }

  startAdapterTutorial2() {
    this.shepherdService.startAdapterTour2();
  }

  startAdapterTutorial3() {
    this.shepherdService.startAdapterTour3();
  }

  selectAdapter(adapterDescription: AdapterDescriptionUnion) {
    this.newAdapterFromDescription = this.dataMarketplaceService.cloneAdapterDescription(adapterDescription);
    (this.newAdapterFromDescription as any).templateTitle = this.newAdapterFromDescription.name;
    this.newAdapterFromDescription.name = '';
    this.newAdapterFromDescription.description = '';

    this.shepherdService.trigger('select-adapter');
  }

  templateFromRunningAdapter(adapter: AdapterDescriptionUnion) {
    adapter.elementId = undefined;
    adapter._rev = undefined;
    this.selectedIndexChange(0);
    this.selectAdapter(adapter);

  }

  removeSelection() {
    this.newAdapterFromDescription = undefined;
  }

  updateFilterTerm(inputValue) {
    this.filterTerm = inputValue;
  }

  filterAdapter(event) {
    const filteredAdapterTypes = this.filterAdapterType(this.adapterDescriptions);
    const filteredAdapterTemplateTypes = this.filterAdapterType(this.adapters);

    const filteredAdapterCategories = this.filterAdapterCategory(filteredAdapterTypes);
    const filteredAdapterTemplateCategories = this.filterAdapterCategory(filteredAdapterTemplateTypes);

    this.filteredAdapterDescriptions = filteredAdapterCategories;
    this.filteredAdapters = filteredAdapterTemplateCategories;
  }

  filterAdapterCategory(currentElements: AdapterDescriptionUnion[]): AdapterDescriptionUnion[] {
    if (this.selectedCategory === this.adapterCategories[0].code) {
      return currentElements;
    } else {
      return currentElements.filter(adapterDescription => adapterDescription.category.indexOf(this.selectedCategory) !== -1);
    }
  }

  filterAdapterType(currentElements: AdapterDescriptionUnion[]): AdapterDescriptionUnion[] {
    if (this.selectedType === this.adapterTypes[0]) {
      return currentElements;
    } else if (this.selectedType === this.adapterTypes[1]) {
      return currentElements.filter(adapterDescription => this.connectService.isDataSetDescription(adapterDescription));
    } else if (this.selectedType === this.adapterTypes[2]) {
      return currentElements.filter(adapterDescription => !this.connectService.isDataSetDescription(adapterDescription));
    }
  }

}
