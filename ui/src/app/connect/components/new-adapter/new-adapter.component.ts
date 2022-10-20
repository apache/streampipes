import { Component, OnInit } from '@angular/core';
import { AdapterDescriptionUnion, AdapterService } from '@streampipes/platform-services';
import { ActivatedRoute } from '@angular/router';
import { ConnectService } from '../../services/connect.service';
import { SpConnectRoutes } from '../../connect.routes';
import { SpBreadcrumbService } from '@streampipes/shared-ui';

@Component({
  selector: 'sp-new-adapter',
  templateUrl: './new-adapter.component.html',
  styleUrls: ['./new-adapter.component.scss']
})
export class NewAdapterComponent implements OnInit {

  initialized = false;
  adapterTypeName = '';
  adapter: AdapterDescriptionUnion = undefined;

  constructor(private breadcrumbService: SpBreadcrumbService,
              private connectService: ConnectService,
              private adapterService: AdapterService,
              private route: ActivatedRoute) { }

  ngOnInit(): void {

    this.adapterService.getAdapterDescriptions().subscribe(adapters => {
      const adapter = adapters.find(a => a.appId === this.route.snapshot.params.appId);
      this.adapterTypeName = adapter.name;
      this.adapter = this.connectService.cloneAdapterDescription(adapter);

      this.breadcrumbService.updateBreadcrumb(this.breadcrumbService
        .makeRoute([SpConnectRoutes.BASE, SpConnectRoutes.CREATE], this.adapterTypeName));
      this.adapter.name = '';
      this.adapter.description = '';
      this.initialized = true;
    });
  }
}
