import { Component, OnInit } from '@angular/core';
import { SpBreadcrumbService } from '@streampipes/shared-ui';
import { ActivatedRoute } from '@angular/router';
import { AssetConstants } from '../../constants/asset.constants';
import { GenericStorageService, SpAsset, SpAssetModel } from '@streampipes/platform-services';
import { SpAssetRoutes } from '../../assets.routes';

@Component({
  selector: 'sp-asset-details-component',
  templateUrl: './asset-details.component.html',
  styleUrls: ['./asset-details.component.scss']
})
export class SpAssetDetailsComponent implements OnInit {

  asset: SpAssetModel;

  selectedAsset: SpAsset;

  constructor(private breadcrumbService: SpBreadcrumbService,
              private genericStorageService: GenericStorageService,
              private route: ActivatedRoute) {

  }

  ngOnInit(): void {
    const assetId = this.route.snapshot.params.assetId;
    this.loadAsset(assetId);
  }

  loadAsset(assetId: string): void {
    this.genericStorageService.getDocument(AssetConstants.ASSET_APP_DOC_NAME, assetId).subscribe(asset => {
      this.asset = asset;
      this.breadcrumbService.updateBreadcrumb([SpAssetRoutes.BASE, {label: this.asset.assetName}]);
      console.log(this.asset);
    });
  }
}
