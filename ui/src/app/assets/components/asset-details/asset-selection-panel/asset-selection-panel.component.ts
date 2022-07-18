import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { SpAsset, SpAssetModel } from '@streampipes/platform-services';
import { NestedTreeControl } from '@angular/cdk/tree';
import { MatTreeNestedDataSource } from '@angular/material/tree';

@Component({
  selector: 'sp-asset-selection-panel-component',
  templateUrl: './asset-selection-panel.component.html',
  styleUrls: ['./asset-selection-panel.component.scss']
})
export class SpAssetSelectionPanelComponent implements OnInit {

  @Input()
  assetModel: SpAssetModel;

  @Output()
  selectedAssetEmitter: EventEmitter<SpAsset> = new EventEmitter<SpAsset>();

  treeControl = new NestedTreeControl<SpAsset>(node => node.assets);
  dataSource = new MatTreeNestedDataSource<SpAsset>();

  hasChild = (_: number, node: SpAsset) => !!node.assets && node.assets.length > 0;

  ngOnInit(): void {
    this.treeControl = new NestedTreeControl<SpAsset>(node => node.assets);
    this.dataSource = new MatTreeNestedDataSource<SpAsset>();
    this.dataSource.data = [this.assetModel];
  }

  selectNode(asset: SpAsset) {
    this.selectedAssetEmitter.emit(asset);
  }


}
