import { Component } from '@angular/core';
import { BaseAssetDetailsDirective } from '../base-asset-details.directive';
import { SpBreadcrumbService } from '@streampipes/shared-ui';
import {
    GenericStorageService,
    LocationConfig,
    LocationConfigService,
} from '@streampipes/platform-services';
import { ActivatedRoute } from '@angular/router';

@Component({
    selector: 'sp-view-asset',
    templateUrl: './view-asset.component.html',
    styleUrls: ['./view-asset.component.scss'],
})
export class SpViewAssetComponent extends BaseAssetDetailsDirective {
    locationConfig: LocationConfig;

    constructor(
        breadcrumbService: SpBreadcrumbService,
        genericStorageService: GenericStorageService,
        route: ActivatedRoute,
        private locationConfigService: LocationConfigService,
    ) {
        super(breadcrumbService, genericStorageService, route);
    }

    onAssetAvailable() {
        this.locationConfigService
            .getLocationConfig()
            .subscribe(config => (this.locationConfig = config));
    }
}
