import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { DataMarketplaceService } from '../../services/data-marketplace.service';
import { MatSelectChange } from '@angular/material/select';
import { AdapterFilterSettingsModel } from '../../model/adapter-filter-settings.model';

@Component({
  selector: 'sp-connect-filter-toolbar',
  templateUrl: './filter-toolbar.component.html',
  styleUrls: ['./filter-toolbar.component.scss']
})
export class SpConnectFilterToolbarComponent implements OnInit {

  @Output()
  filterChangedEmitter: EventEmitter<AdapterFilterSettingsModel> = new EventEmitter<AdapterFilterSettingsModel>();

  adapterTypes: string[] = ['All types', 'Data Set', 'Data Stream'];
  adapterCategories: any;

  currentFilter: AdapterFilterSettingsModel = {textFilter: '', selectedCategory: 'All', selectedType: 'All types'};

  constructor(private dataMarketplaceService: DataMarketplaceService) {

  }

  ngOnInit(): void {
    this.loadAvailableTypeCategories();
  }

  loadAvailableTypeCategories() {
    this.dataMarketplaceService.getAdapterCategories().subscribe(res => {
      this.adapterCategories = res;
      this.adapterCategories.unshift({ label: 'All categories', description: '', code: 'All' });
      this.filterChangedEmitter.emit(this.currentFilter);
    });
  }

  filterAdapter(event: MatSelectChange) {
    this.filterChangedEmitter.emit(this.currentFilter);
  }

  updateFilterTerm(event: string) {
    this.currentFilter.textFilter = event;
    this.filterChangedEmitter.emit(this.currentFilter);
  }


}
