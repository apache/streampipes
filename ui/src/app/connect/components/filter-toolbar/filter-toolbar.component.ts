import { Component, OnInit } from '@angular/core';
import { DataMarketplaceService } from '../../services/data-marketplace.service';
import { MatSelectChange } from '@angular/material/select';

@Component({
  selector: 'sp-connect-filter-toolbar',
  templateUrl: './filter-toolbar.component.html',
  styleUrls: ['./filter-toolbar.component.scss']
})
export class SpConnectFilterToolbarComponent implements OnInit {

  adapterTypes: string[] = ['All types', 'Data Set', 'Data Stream'];
  selectedType = 'All types';

  adapterCategories: any;
  selectedCategory: any = 'All';

  constructor(private dataMarketplaceService: DataMarketplaceService) {

  }

  ngOnInit(): void {
    this.loadAvailableTypeCategories();
  }

  loadAvailableTypeCategories() {
    this.dataMarketplaceService.getAdapterCategories().subscribe(res => {
      this.adapterCategories = res;
      this.adapterCategories.unshift({ label: 'All categories', description: '', code: 'All' });
    });
  }

  filterAdapter(event: MatSelectChange) {

  }

  updateFilterTerm(event: string) {

  }


}
