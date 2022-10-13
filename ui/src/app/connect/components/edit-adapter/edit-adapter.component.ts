import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { AdapterDescriptionUnion, AdapterService } from '@streampipes/platform-services';
import { SpBreadcrumbService } from '@streampipes/shared-ui';
import { SpConnectRoutes } from '../../connect.routes';

@Component({
  selector: 'sp-edit-adapter',
  templateUrl: './edit-adapter.component.html',
  styleUrls: ['./edit-adapter.component.scss']
})
export class EditAdapterComponent implements OnInit {

  initialized = false;
  adapterName = '';
  adapter: AdapterDescriptionUnion = undefined;

  constructor(private adapterService: AdapterService,
              private breadcrumbService: SpBreadcrumbService,
              private route: ActivatedRoute) { }

  ngOnInit(): void {

    this.adapterService.getAdapter(this.route.snapshot.params.elementId).subscribe(adapter => {

      this.adapter = adapter;
      this.adapterName = adapter.name;
      this.initialized = true;

      this.breadcrumbService.updateBreadcrumb(this.breadcrumbService
        .makeRoute([SpConnectRoutes.BASE, SpConnectRoutes.EDIT], this.adapterName));
    });
  }

}
