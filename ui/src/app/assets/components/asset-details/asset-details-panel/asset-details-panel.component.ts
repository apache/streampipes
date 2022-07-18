import { Component, Input, OnInit } from '@angular/core';
import { SpAsset } from '@streampipes/platform-services';


@Component({
  selector: 'sp-asset-details-panel-component',
  templateUrl: './asset-details-panel.component.html',
  styleUrls: ['./asset-details-panel.component.scss']
})
export class SpAssetDetailsPanelComponent implements OnInit {

  @Input()
  asset: SpAsset;

  ngOnInit(): void {
  }

}
